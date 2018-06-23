package fluffuwa.card;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;

import fluffuwa.card.Cards.Card;
import fluffuwa.card.Enemies.Enemy;

import static fluffuwa.card.Cards.Card.cardWidth;

public class Battle extends GameState {
    ArrayList<Card> playerDeck;
    ArrayList <Card> playerPlayed = new ArrayList();
    ArrayList <Card> playerHand = new ArrayList();
    ArrayList <Card> enemyDeck;
    ArrayList <Card> enemyPlayed = new ArrayList();
    ArrayList <Card> enemyHand = new ArrayList ();

    ArrayList <Card> allCards = new ArrayList();

    Card transferring = null;
    //double transferringCoordX = 0;
    //double transferringCoordY = 0;
    int transferStage = -1;

    Enemy opponent;

    Enemy player;

    static public boolean revealedEnemyCards;


    //int handSize = 5;
    //int enemyHandSize = 5;

    public Battle (String enemy){
        super ("Battle");
        revealedEnemyCards = gsc.read ("revealedenemycards", "false").equals ("true");//only affects opponent's hand, really
        //for (int x = 0; x < gsc.deck.size(); x ++){
        //    touchables.add (Card.getCard (gsc.deck.get(x).getName()));
        //}
        opponent = Enemy.getEnemy(enemy);
        opponent.curX = w/2.0;
        opponent.curY = opponent.i.getHeight()/2.0;
        enemyDeck = opponent.decklist;
        for (int x = 0; x < enemyDeck.size(); x ++){
            enemyDeck.get(x).revealed = revealedEnemyCards;
            enemyDeck.get(x).playerCard = false;
            allCards.add (enemyDeck.get(x));
        }

        player = Enemy.getEnemy ("player");
        player.curX = w/2.0;
        player.curY = h - player.i.getHeight()/2.0;
        playerDeck = player.decklist;
        for (int x = 0; x < playerDeck.size(); x ++){
            playerDeck.get(x).revealed = true;//duh
            playerDeck.get(x).playerCard = true;
            allCards.add (playerDeck.get(x));
        }

        //for (int x = 0; x < playerDeck.size(); x ++)
        //    allCards.add (playerDeck.get(x));
        //for (int x = 0; x < enemyDeck.size(); x ++)
        //    allCards.add (enemyDeck.get(x));

        //animate
        //maybe these should've been in the Enemy superclass.....
        while (playerHand.size() < startingHandSize && playerDeck.size() > 0){
            drawCard(true);
            //playerHandPositions.add (-Math.PI/2.0);
            //playerHand.add (playerDeck.remove ((int)(Math.random()*playerDeck.size())));
            //playerHand.get (playerHand.size()-1).cardBackgroundActive = true;
            ////playerHand.get(playerHand.size()-1).mode = Card.Mode.GAMEPLAY;
            //playerHandXY.add (new Point (-1000, -1000));
        }
        while (enemyHand.size () < startingHandSize && enemyDeck.size()>0){
            drawCard (false);
            //enemyHandPositions.add (Math.PI/2.0);
            //enemyHand.add (enemyDeck.remove ((int)(Math.random()*enemyDeck.size())));
            //enemyHand.get (enemyHand.size()-1).cardBackgroundActive = true;
            ////playerHand.get(playerHand.size()-1).mode = Card.Mode.GAMEPLAY;
            //enemyHandXY.add (new Point (-1000, -1000));
        }

    }

    public void drawCard (boolean player){
        if (player) {
            if (playerDeck.size() > 0)
            playerHand.add(playerDeck.remove((int) (Math.random() * playerDeck.size())).addToHand());
        }
        else {
            if (enemyDeck.size() > 0)
            enemyHand.add(enemyDeck.remove((int) (Math.random() * enemyDeck.size())).addToHand());
        }
    }

    int startingHandSize = 5;

    //GIVE COORDINATES AS THINGS LIKE "K2"
    //THESE IS A LIST OF OPTIMAL COORDINATES FOR EACH COORDINATE NAME
    //IN CARD, THE CARD THEN TRIES TO MOVE TOWARDS THAT LOCATION (DOESN'T NEED TO CHECK IF IT'S THERE ALREADY LOL)
    //THE CARD IS THEN DRAWN
    //SO ALL YOU HAVE TO DO IN ONDRAW IS SET THE COORDINATE NAMES FOR ALL OF THE CARDS
    //THEN AS THEY'RE ALL DRAWN IN THE SAME FOR LOOP, THEY DO THEIR CALCULATIONS THINGS
    //IF THEY'RE OUTSIDE OF THE ZONE, THEY DON'T EVEN NEED TO BE DRAWN, AND THAT CAN BE WRITTEN IN THE CARD DRAW METHOD
    //SO THE BULK HERE IN BATTLE GOES DOWN SIGNIFICANTLY
    //IF IT'S GIVEN A COORDINATE LIKE H1, IT CAN TOTALLY PATH ON AN ARC TO THAT LOCATION
    //LIKE, CENTERED AROUND THE PLAYER, AN ARC FROM WHEREVER IT IS, AS THOUGH ON AN OVAL WITH THE PLAYER/ENEMY AT THE CENTER

    public void onDraw (Canvas c){
        opponent.onDraw (c);
        player.onDraw(c);

        for (int x = 0; x < playerHand.size(); x ++){
            playerHand.get(x).targetLocation = "P,"  + playerHand.size() + "," + x;
        }
        for (int x = 0; x < enemyHand.size(); x ++){
            enemyHand.get(x).targetLocation = "P,"  + enemyHand.size() + "," + x;
        }

        if (transferStage == 10){
            transferring.targetLocation = "L-1";
            playerPlayed.add (transferring);
            //transferStage = -1;
            //transferStage ++;//opponent's turn now REEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
            //transferring = null;

            transferStage = 11;
            int chosen = (int)(Math.random()*enemyHand.size());
            enemyHand.get(chosen).whenTouched();
            transferring = enemyHand.remove (chosen);
        }
        if (transferStage == 21){
            transferring.targetLocation = "L-1";
            enemyPlayed.add (transferring);
            transferStage = -1;
        }

        for (int x = 0; x < playerPlayed.size (); x ++){
            playerPlayed.get(x).targetLocation = "L," + x;
        }
        for (int x = 0; x < enemyPlayed.size (); x ++){
            enemyPlayed.get(x).targetLocation = "L," + x;
        }

        //System.out.println (transferStage);

        if (transferStage != -1){
            //move shit around yo
            double preferredX = 0;
            double preferredY = 0;
            switch (transferStage){
                case 0://upright
                    transferring.cardBackgroundActive = false;
                    preferredX = w - cardWidth/2.0 - w/36.0;
                    preferredY = h/2.0 + 3.0/2.0*cardWidth + w/18.0;
                    break;
                case 1://up
                    preferredX = w - cardWidth/2.0 - w/36.0;
                    preferredY = h/2.0 + cardWidth/2.0 + w/36.0;
                    break;
                case 2://left
                    preferredX = cardWidth / 2.0 + w/36.0;
                    preferredY = h/2.0 + cardWidth/2.0 + w/36.0;
                    break;
                case 3://up
                    preferredX = cardWidth / 2.0 + w/36.0;
                    preferredY = h/2.0 - cardWidth/2.0 - w/36.0;
                    break;
                case 4://right
                    preferredX = w - cardWidth/2.0 - w/36.0;
                    preferredY = h/2.0 - cardWidth/2.0 - w/36.0;
                    break;
                case 5://up
                    preferredX = w - cardWidth/2.0 - w/36.0;
                    preferredY = h/2.0 - 3.0/2.0*cardWidth - w/18.0;
                    break;
                case 6://left
                    preferredX = w/2.0;
                    preferredY = h/2.0 - 3.0/2.0*cardWidth - w/18.0;
                    break;
                case 7://up
                    preferredX = w/2.0;
                    preferredY = cardWidth/2.0;
                    break;
                case 8://slide in
                    drawCard (false);
                    transferring.setCur(w + cardWidth, h/2.0 + cardWidth/2.0 + w/36.0);
                    transferStage ++;
                    transferring.cardBackgroundActive = true;
                    if (playerPlayed.size() == 5){
                        playerPlayed.remove (0);
                    }
                case 9:
                    preferredX = (playerPlayed.size())*(cardWidth + w/36.0) + cardWidth/2.0 + w/36.0;
                    preferredY = h/2.0 + cardWidth/2.0 + w/36.0;
                    break;

                    //opponent's card

                case 11://downleft
                    drawCard (true);
                    transferring.cardBackgroundActive = false;
                    transferring.revealed = true;
                    preferredX = w - cardWidth/2.0 - w/36.0;
                    preferredY = h/2.0 + 3.0/2.0*cardWidth + w/18.0;

                    break;
                //case 1://up
                //    preferredX = w - cardWidth/2.0 - w/36.0;
                //    preferredY = h/2.0 + cardWidth/2.0 + w/36.0;
                //    break;
                //case 2://left
                //    preferredX = cardWidth / 2.0 + w/36.0;
                //    preferredY = h/2.0 + cardWidth/2.0 + w/36.0;
                //    break;
                //case 3://up
                //    preferredX = cardWidth / 2.0 + w/36.0;
                //    preferredY = h/2.0 - cardWidth/2.0 - w/36.0;
                //    break;
                //case 4://right
                //    preferredX = w - cardWidth/2.0 - w/36.0;
                //    preferredY = h/2.0 - cardWidth/2.0 - w/36.0;
                //    break;
                //case 5://up
                //    preferredX = w - cardWidth/2.0 - w/36.0;
                //    preferredY = h/2.0 - 3.0/2.0*cardWidth - w/18.0;
                //    break;
                //case 6://left
                //    preferredX = w/2.0;
                //    preferredY = h/2.0 - 3.0/2.0*cardWidth - w/18.0;
                //    break;
                //case 7://up
                //    preferredX = w/2.0;
                //    preferredY = cardWidth/2.0;
                //    break;
                //case 8://slide in
                //    enemyHandSize ++;
                //    transferring.setCur(w + cardWidth, h/2.0 + cardWidth/2.0 + w/36.0);
                //    transferStage ++;
                //    transferring.cardBackgroundActive = true;
                //    if (playerPlayed.size() == 5){
                //        playerPlayed.remove (0);
                //    }
                //case 9:
                //    preferredX = (playerPlayed.size())*(cardWidth + w/36.0) + cardWidth/2.0 + w/36.0;
                //    preferredY = h/2.0 + cardWidth/2.0 + w/36.0;
                //    break;
            }
            transferring.targetLocation = preferredX +","+ preferredY;
            if (transferring.distanceToTarget < 3)
                transferStage ++;
        }


        //draw
        //for (int x = 0; x < playerHand.size(); x ++){
        //    if (playerHand.get(x).getImage() != null)
        //        c.drawBitmap (Card.cardBackground, (float) playerHand.get(x).curX, (float) playerHand.get(x).curY, p);
        //}
        //for (int x = 0; x < playerHand.size(); x ++){
        //    if (playerHand.get(x).getImage() != null)
        //        c.drawBitmap (playerHand.get(x).getImage(), (float) playerHand.get(x).curX, (float) playerHand.get(x).curY, p);
        //}
        //if (Settings.showOpponentCards){
        //    for (int x = 0; x < enemyHand.size(); x ++){
        //        if (enemyHand.get(x).getImage() != null)
        //            c.drawBitmap (Card.cardBackground, (float) enemyHand.get(x).curX, (float) enemyHand.get(x).curY, p);
        //    }
        //    for (int x = 0; x < enemyHand.size(); x ++){
        //        if (enemyHand.get(x).getImage() != null)
        //            c.drawBitmap (enemyHand.get(x).getImage(), (float) enemyHand.get(x).curX, (float) enemyHand.get(x).curY, p);
        //    }
        //}
        //else{
        //    for (int x = 0; x < enemyHand.size(); x ++){
        //        if (enemyHand.get(x).getImage() != null)
        //            c.drawBitmap (Card.cardBack, (float)enemyHand.get(x).curX, (float) enemyHand.get(x).curY, p);
        //    }
        //}
        for (int x = 0; x < allCards.size(); x ++){
            allCards.get(x).draw (c);
        }
        c.drawLine((float)w, (float)h, 0, 0, p);
    }

    int cardTouched = -1;

    //double distance (double x, double y, double x2, double y2){
    //    return (Math.sqrt ((x-x2)*(x-x2) + (y - y2)*(y-y2)));
    //}

    @Override
    public void onTouch (MotionEvent m){
        super.onTouch (m);

        if (transferStage != -1)
            return;

        Rect rect = new Rect ();
        gsc.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        double x = m.getX();
        double y = m.getY() - rect.top;

        switch (m.getAction()){
            case MotionEvent.ACTION_DOWN:
                for (int z = 0; z < playerHand.size(); z++) {
                    if (playerHand.get(z).within (x, y)) {
                        cardTouched = z;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                for (int z = 0; z < playerHand.size(); z++) {
                    if (cardTouched == z && playerHand.get(z).within (x, y)) {
                        //play the card
                        transferStage = 0;
                        playerHand.get(z).whenTouched();
                        transferring = playerHand.remove (z);
                    }
                }
                break;
            default:

                break;
        }
    }
}

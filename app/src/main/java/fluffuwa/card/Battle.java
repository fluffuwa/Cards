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
    //ArrayList<Card> playerDeck;
    //ArrayList <Card> playerPlayed = new ArrayList();
    //ArrayList <Card> playerHand = new ArrayList();
    //ArrayList <Card> enemyDeck;
    //ArrayList <Card> enemyPlayed = new ArrayList();
    //ArrayList <Card> enemyHand = new ArrayList ();

    ArrayList <Card> allCards = new ArrayList();

    Card transferring = null;
    //double transferringCoordX = 0;
    //double transferringCoordY = 0;
    int transferStage = -1;

    //Enemy opponent;
//
    //Enemy player;

    static public boolean revealedEnemyCards;

    Enemy [] units = new Enemy [2];


    //int handSize = 5;
    //int enemyHandSize = 5;

    public Battle (String enemy){
        super ("Battle");
        revealedEnemyCards = gsc.read ("revealedenemycards", "false").equals ("true");//only affects opponent's hand, really
        //for (int x = 0; x < gsc.deck.size(); x ++){
        //    touchables.add (Card.getCard (gsc.deck.get(x).getName()));
        //}
        units[0] = Enemy.getEnemy ("player");
        units [1] = Enemy.getEnemy(enemy);
        for (Enemy unit:units){
            unit.curX = w/2.0;
            unit.curY = unit.i.getHeight()/2.0;

            for (int x = 0; x < unit.deck.size(); x ++){
                unit.deck.get(x).revealed = unit.isPlayer()?revealedEnemyCards:true;
                unit.deck.get(x).playerCard = unit.isPlayer();
                allCards.add (unit.deck.get(x));
            }

            while (unit.hand.size() < startingHandSize && unit.deck.size() > 0){
                unit.drawCard ();
            }
        }
        //opponent.curX = w/2.0;
        //opponent.curY = opponent.i.getHeight()/2.0;
        //for (int x = 0; x < enemyDeck.size(); x ++){
        //    enemyDeck.get(x).revealed = revealedEnemyCards;
        //    enemyDeck.get(x).playerCard = false;
        //    allCards.add (enemyDeck.get(x));
        //}
//
        //player.curX = w/2.0;
        //player.curY = h - player.i.getHeight()/2.0;
        //playerDeck = player.decklist;
        //for (int x = 0; x < playerDeck.size(); x ++){
        //    playerDeck.get(x).revealed = true;//duh
        //    playerDeck.get(x).playerCard = true;
        //    allCards.add (playerDeck.get(x));
        //}

        //player = Enemy.getEnemy ("player");
        //        //enemyDeck = opponent.decklist;
        //for (int x = 0; x < playerDeck.size(); x ++)
        //    allCards.add (playerDeck.get(x));
        //for (int x = 0; x < enemyDeck.size(); x ++)
        //    allCards.add (enemyDeck.get(x));

        //animate
        //maybe these should've been in the Enemy superclass.....
        //while (playerHand.size() < startingHandSize && playerDeck.size() > 0){
        //    drawCard(true);
        //    //playerHandPositions.add (-Math.PI/2.0);
        //    //playerHand.add (playerDeck.remove ((int)(Math.random()*playerDeck.size())));
        //    //playerHand.get (playerHand.size()-1).cardBackgroundActive = true;
        //    ////playerHand.get(playerHand.size()-1).mode = Card.Mode.GAMEPLAY;
        //    //playerHandXY.add (new Point (-1000, -1000));
        //}
        //while (enemyHand.size () < startingHandSize && enemyDeck.size()>0){
        //    drawCard (false);
        //    //enemyHandPositions.add (Math.PI/2.0);
        //    //enemyHand.add (enemyDeck.remove ((int)(Math.random()*enemyDeck.size())));
        //    //enemyHand.get (enemyHand.size()-1).cardBackgroundActive = true;
        //    ////playerHand.get(playerHand.size()-1).mode = Card.Mode.GAMEPLAY;
        //    //enemyHandXY.add (new Point (-1000, -1000));
        //}

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

        for (Enemy unit:units){
            unit.onDraw (c);
            for (int x = 0; x < unit.hand.size(); x ++){
                unit.hand.get(x).targetLocation = "P,"  + unit.hand.size() + "," + x;
            }

        }

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
                for (int z = 0; z < units[0].hand.size(); z++) {
                    if (units[0].hand.get(z).within (x, y)) {
                        cardTouched = z;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                for (int z = 0; z < units[0].hand.size(); z++) {
                    if (cardTouched == z && units[0].hand.get(z).within (x, y)) {
                        //play the card
                        transferStage = 0;
                        units[0].hand.get(z).whenTouched();
                        transferring = units[0].hand.remove (z);
                    }
                }
                break;
            default:

                break;
        }
    }
}

package fluffuwa.card.Cards;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Hashtable;

import fluffuwa.card.GameStateController;
import fluffuwa.card.Touchable;

public abstract class Card {

    //after it's been played and is still in the center, call this at the end of each turn
    public abstract void endOfTurnEffect ();
    //when an attack has been chosen and must go through the center card's effects.
    //this is the same side as the attack that's played
    //the attack may get boosted as a result of previous attacks that have been played
    public abstract Card usuallyBoostAttack (Card e);
    //this is the opposing side as the attack that's played
    //the damage taken may increase as a result of previous hits that have been taken
    public abstract Card usuallyWeakenAttack (Card e);
    //damage dealt to opponent
    double damage;
    //damage dealt to self. can be negative for healing, y'know!!!
    double selfDamage;
    //after effectiveness.
    //after the attack is made, how much effectiveness it still has against other attacks. 0 means it's basically not there, 1.0 means it's natural effectiveness.
    //effectiveness is always multiplied by effectiveness to change effectiveness. two -50% effectiveness gives 25% rather than 0% effectiveness.
    double ae = 1.0;

    static GameStateController gsc;

    String name;

    Bitmap i;

    public static double w;
    public static double h;
    public static double px;
    public static double py;
    public static double cardWidth;
    public static double cardHeight;
    //card background image
    protected static Bitmap cardBackground;
    protected static Bitmap cardBack;

    public String getName (){
        return name;
    }

    public static void setGSC (GameStateController gsc2){
        gsc = gsc2;
        w = gsc.displaySize.x;
        h = gsc.displaySize.y;
        //Rect rect = new Rect ();
        //gsc.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //System.out.println ("top: " + rect.top);
        //h = h- rect.top;
        h -= 24;
        px = w/90.0;
        py = h/160.0;
        cardWidth = w/6.0;
        cardHeight = w/6.0;

        cardBackground = getImage ("background");
        cardBack = getImage ("back");

        p = new Paint ();

        cards = new Card []{new Punch (), new Slime ()};
    }

    protected static final Bitmap getImage (String name){
        Bitmap i = gsc.getDrawable(name, true);
        return i;
    }

    //load image
    public Card (String name, double damage){
        if (!name.equals (""))
            i = getImage (name);
        this.name = name;
        this.damage = damage;
    }
    public Card (String name, double damage, double selfDamage){
        this (name, damage);
        this.selfDamage = selfDamage;
    }

    static Paint p;

    public boolean cardBackgroundActive = true;
    public boolean revealed;

    public double curX = 0;
    public double curY = 0;

    public void setCur (double x, double y){
        curX = x;
        curY = y;
    }

    public String targetLocation = "";

    double speed = 10;//higher is slower

    public double distanceToTarget;

    private double handPosition;//PI thing

    public boolean playerCard;

    public Card addToHand (){
        cardBackgroundActive = true;
        handPosition = -1*Math.PI/2.0;
        return this;
    }

    public void draw (Canvas c){
        double preferredX = 0;
        double preferredY = 0;
        if (targetLocation.length() > 0) {
            switch (targetLocation.charAt(0)) {
                //player hand
                case 'P':
                    int playerHandPositionsSize = Integer.parseInt(targetLocation.split(",")[1]);
                    int x = Integer.parseInt(targetLocation.split(",")[2]);
                    double correctPosition = (playerHandPositionsSize - x) * Math.PI / (playerHandPositionsSize + 1.0);
                    handPosition = (correctPosition - handPosition) / speed + handPosition;
                    curX = (int) (Math.cos(handPosition) * w / 3.0 + w / 2.0);
                    curY = (int) (-Math.sin(handPosition) * w / 3.0 - w / 12.0 + h);
                    preferredX = curX;
                    preferredY = curY;
                    break;
                    //player cards
                case 'L':
                    int y = Integer.parseInt(targetLocation.split(",")[1]);
                    preferredX = cardWidth * y + y * w / 36.0 + cardWidth / 2.0 + w / 36.0;
                    preferredY = h / 2.0 + w / 36.0 + cardWidth / 2.0;
                    break;
                default:
                    preferredX = Double.parseDouble(targetLocation.split(",")[0]);
                    preferredY = Double.parseDouble(targetLocation.split(",")[1]);
                    break;
            }
            if (!playerCard)
                preferredY = h - preferredY;

            curX = (preferredX - curX) / speed + curX;
            curY = (preferredY - curY) / speed + curY;
            double tempX = curX - (cardWidth / 2.0);
            double tempY = curY - (cardWidth / 2.0);

            distanceToTarget = Math.sqrt((preferredX - curX) * (preferredX - curX) + (preferredY - curY) * (preferredY - curY));

            if (revealed) {
                if (cardBackgroundActive)
                    c.drawBitmap(cardBackground, (int) (tempX), (int) tempY, p);
                c.drawBitmap(i, (int) tempX, (int) tempY, p);
            } else
                c.drawBitmap(cardBack, (int) tempX, (int) tempY, p);
        }
    }

    public boolean within (double pointx, double pointy){
        double cardx = curX;
        double cardy = curY;
        return (Math.sqrt ((pointx - cardx) * (pointx - cardx) +
                (pointy - cardy) * (pointy - cardy))) < cardWidth/2.0;
    }

    //public enum Mode {GAMEPLAY, ANIMATING, ENEMY, COLLECTION, NONE};
    //public Mode mode = Mode.GAMEPLAY;

    public void whenTouched (){
    }
    public void whenHeld (){
        //switch (mode){
        //    case GAMEPLAY:
        //        mode = Mode.ANIMATING;
        //        break;
        //    case COLLECTION:
        //        break;
        //    case ENEMY:
        //    case ANIMATING:
        //    case NONE:
        //        break;
        //}
    }
    public void whileHeld (double dx, double dy){
        //switch (mode){
        //    case GAMEPLAY:
        //        mode = Mode.ANIMATING;
        //        break;
        //    case COLLECTION:
        //        break;
        //    case ENEMY:
        //    case ANIMATING:
        //    case NONE:
        //        break;
        //}
    }

    public static Card getCard (String cardName){
        try {
            for (int x = 0; x < cards.length; x ++){
                if (cards [x].name.equals (cardName))
                    return cards [x].getClass().newInstance();
            }
        }
        catch (Exception e){
            System.out.println (cardName);
            e.printStackTrace();
        }
        System.out.println (cardName + " card not found");
        return null;
    }


    public static Card [] cards;
}

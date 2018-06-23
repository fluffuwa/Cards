package fluffuwa.card.Enemies;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

import fluffuwa.card.Battle;
import fluffuwa.card.Cards.Card;
import fluffuwa.card.GameStateController;
import fluffuwa.card.Touchable;

public abstract class Enemy {

    public ArrayList<Card> decklist = new ArrayList ();

    public String name;

    public Bitmap i;
    public static double w;
    public static double h;
    public static double px;
    public static double py;

    public static double width;
    public static double height;

    public double curX;
    public double curY;

    static Paint p;

    public static void setup (GameStateController gsc2){
        gsc = gsc2;
        w = gsc.displaySize.x;
        h = gsc.displaySize.y;
        //Rect rect = new Rect ();
        //gsc.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //h = h- rect.top;
        h -= 24;
        px = w/90.0;
        py = h/160.0;
        width = w/3.0;
        height = w/3.0;
        p = new Paint ();
        enemies = new Enemy []{new Slimey (), new Player()};
    }

    public void onDraw (Canvas c){
        c.drawBitmap (i, (int)(curX - width/2.0), (int)(curY - width/2.0), p);
    }

    static GameStateController gsc;

    public Enemy (String name, double health){
        this.name = name;
        this.health = health;
        if (!name.equals (""))
            i = getImage (name);
        try {
            i = Bitmap.createScaledBitmap(i, (int)width, (int)height, false);
        }
        catch (Exception e){
            System.out.println ("couldn't resize bitmap..." + name);
        }
    }

    protected static final Bitmap getImage (String name){
        Bitmap i = gsc.getDrawable(name, false);
        return i;
    }

    double health;

    public static Enemy [] enemies;
    public static Enemy getEnemy (String enemyName){
        try {
            for (int x = 0; x < enemies.length; x ++){
                if (enemies [x].name.equals (enemyName))
                    return enemies [x].getClass().newInstance();
            }
        }
        catch (Exception e){
            System.out.println (enemyName);
            e.printStackTrace();
        }
        return null;
    }
}

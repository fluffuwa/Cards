package fluffuwa.card;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

import fluffuwa.card.Cards.Card;
import fluffuwa.card.Enemies.Enemy;

import static android.os.SystemClock.uptimeMillis;
import static fluffuwa.card.Cards.Card.cardHeight;
import static fluffuwa.card.Cards.Card.cardWidth;

//C:\Users\joeym\AppData\Local\Android\Sdk\platform-tools
//192.168.1.112
public class GameStateController extends Activity {

    //used for reading data
    public static SharedPreferences settings;
    public static SharedPreferences.Editor editor;

    public static ArrayList <Card> deck = new ArrayList ();
    public static ArrayList <Card> collection = new ArrayList ();
    public static String [] completedCounts;
    public static double health;

    public static final String defaultDeck = "punch,punch,punch,punch,punch,punch,punch,punch,punch,punch";
    public static final String defaultCollection = "";
    public static final String defaultHealth = "100";
    public static final String defaultCompleteds = "0,0,0,0,0,0,0,0,0,0,0,0,0,0";

    public static String read (String key, String def){
        return settings.getString (key, def);
    }

    public static void write (String key, String text){
        editor.putString (key, text);
        editor.commit();
    }

    public void setValues (){
        //read saved values
        String [] currentDeck = settings.getString ("deck", defaultDeck/*default deck??*/).split (",");
        for (int x = 0; x < currentDeck.length; x ++){
            if (!currentDeck [x].equals (""))
                deck.add (Card.getCard (currentDeck [x]));
        }
        System.out.println ("current deck size: " + currentDeck.length);
        String [] currentCollection = settings.getString ("collection", defaultCollection/*default collection??*/).split (",");
        for (int x = 0; x < currentCollection.length; x ++){
            if (!currentCollection [x].equals (""))
                collection.add (Card.getCard (currentCollection [x]));
        }
        completedCounts = settings.getString ("completedCounts", defaultCompleteds).split (",");

        health = Double.parseDouble (settings.getString ("health", defaultHealth));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //get thing to read images
        options = new BitmapFactory.Options();
        options.inScaled = false;
        //get display size
        displaySize = new Point();
        getWindowManager ().getDefaultDisplay().getSize (displaySize);
        //get thing to read saved values
        settings = getSharedPreferences ("thing", 0);
        editor = settings.edit();
        //set card's access to gamestatecontroller so it can get card images
        Card.setGSC(this);
        Touchable.setGSC (this);
        Enemy.setup (this);
        GameState.setGameStateController(this);

        setValues ();

        //set up background color
        final Paint w = new Paint();
        w.setColor (Color.WHITE);
        //draw shit
        cl = findViewById(R.id.space);
        v = new View (getBaseContext()){
            protected void onDraw (Canvas canvas) {
                scheduleDrawable(getBackground(), redrawer, uptimeMillis() + 17);
                //canvas.drawPaint (w);//reset drawing space

                gs.draw (canvas);
            }

        };
        cl.addView (v);

        new Settings ();

        gs = new EnemyList ();
    }
    private final Runnable redrawer = new Runnable() {
        @Override
        public void run() {
            v.invalidate();
        }
    };
    private ConstraintLayout cl;
    private View v;
    public static Point displaySize;

    GameState gs;

    private long areYouSure;

    @Override
    public void onBackPressed ()
    {
        switch (gs.name){
            case "EnemyList":
                gs = new Settings ();
                break;
            case "Settings":
                gs = new EnemyList ();
                break;
            case "Battle":
                if (uptimeMillis () - areYouSure < 1500){
                    //should be fine to just exit without some close operation
                    gs = new EnemyList ();
                }
                else{
                    areYouSure = uptimeMillis();
                    Toast.makeText(this, "Press back again to escape", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Collection":
                gs = new EnemyList();
                break;
        }
        //if on enemy select, switch to settings
        //if on if on settings, switch to enemy select
        //if in battle, ask for confirmation to leave. no penalty for leaving battle.
        //if in deck/collection menu, switch to enemy select
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();
        //reset game timers I guess for animations
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //load images
    public static Hashtable <String, Bitmap> images = new Hashtable();
    private BitmapFactory.Options options;
    public Bitmap getDrawable (String id, boolean isCard)
    {
        if (id.equals (""))
            return null;
        if (images.containsKey (id)){
            return images.get (id);
        }
        //System.out.println (getResources ().getIdentifier (id, "drawable", getApplicationContext().getPackageName ()));
        Bitmap bmp2 = BitmapFactory.decodeResource (getResources (), getResources ().getIdentifier (id, "drawable", getPackageName ()), options);
        Bitmap bmp = bmp2.copy (Bitmap.Config.ARGB_8888, true);
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        System.out.println (getResources ().getIdentifier (id, "drawable",getPackageName ()));
        //System.out.println (R.drawable.back);
        //bmp = BitmapFactory.decodeResource (getResources(), R.drawable.back);

        //RESIZE IMAGE HERE REEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
        try {
            bmp.setDensity(Bitmap.DENSITY_NONE);
            images.put (id, bmp);
        }
        catch (Exception e)
        {
            System.out.println ("problem loading image " + id + ".");
            e.printStackTrace();
        }
        if (isCard) {
            try {
                bmp = Bitmap.createScaledBitmap(bmp, (int)cardWidth, (int)cardHeight, false);
            }
            catch (Exception e){
                System.out.println ("couldn't resize bitmap " + id + "...");
            }}
        return bmp;
    }


    public boolean onTouchEvent (MotionEvent event)
    {
        //double x = event.getX();
        //double y = event.getY();
        gs.onTouch (event);
        //if (event.getAction () == MotionEvent.ACTION_DOWN)
        //{
//
        //}
        //if (event.getAction () == MotionEvent.ACTION_UP)
        //{
//
        //}
        return super.onTouchEvent(event);
    }
}

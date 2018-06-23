package fluffuwa.card;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;

import java.util.ArrayList;

public abstract class GameState {
    ArrayList<Touchable> touchables = new ArrayList ();
    public String name;
    protected static GameStateController gsc;
    static double w;
    static double h;
    static double px;
    static double py;
    public static void setGameStateController (GameStateController gsc2){
        gsc = gsc2;
        w = gsc.displaySize.x;
        h = gsc.displaySize.y;
        //Rect rect = new Rect ();
        //gsc.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //h -= rect.top;
        h -= 24;//status bar
        px = w/90.0;
        py = h/160.0;
    }
    public GameState (String name){
        this.name = name;
        p = new Paint ();
        p.setColor (Color.BLACK);

    }
    Paint p;

    public abstract void onDraw (Canvas c);

    public final void draw (Canvas c){
        //for (int x = 0; x < touchables.size(); x ++){//um....
        //    touchables.get(x).getPath ().offset((float)touchables.get(x).curX, (float)touchables.get(x).curY);
        //    c.drawPath (touchables.get(x).getPath(), p);
        //    touchables.get(x).getPath ().offset(-(float)touchables.get(x).curX, -(float)touchables.get(x).curY);
        //}
        for (int x = 0; x < touchables.size(); x ++){
            if (touchables.get(x).getImage() != null)
                c.drawBitmap (touchables.get(x).getImage(), (float)touchables.get(x).curX, (float)touchables.get(x).curY, p);
        }
        onDraw (c);
    }

    long timeWhenPressed;
    boolean [] touchableTouched = new boolean [0];
    int superPressed = -1;
    double lastX;
    double lastY;
    //can vibrate to signify long-press action available, and do touch actions when finger is lifted.
    public void onTouch (MotionEvent m){
        Rect rect = new Rect ();
        gsc.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        double x = m.getX();
        double y = m.getY() - rect.top;

        switch (m.getAction()){
            case MotionEvent.ACTION_DOWN:
                System.out.println (x + ", " + y);
                timeWhenPressed = System.currentTimeMillis();
                touchableTouched = new boolean [touchables.size()];
                for (int z = 0; z < touchables.size(); z++) {
                    touchableTouched [z] = touchables.get(z).touched (x,y);//true for touched, false for not touched
                    superPressed = z;
                    lastX = x;
                    lastY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                for (int z = 0; z < touchables.size(); z++) {
                    if (touchableTouched [z] && touchables.get(z).touched(x, y)) {
                        touchables.get(z).whenTouched();
                    }
                }
                superPressed = -1;
                lastX = -1;
                lastY = -1;
                break;
            default:
                for (int z = 0; z < touchables.size(); z++) {
                    if (touchableTouched[z]) {
                        if (touchables.get(z).touched(x, y)) {
                            if (System.currentTimeMillis() - timeWhenPressed > 1000) {
                                //Vibrator v = (Vibrator) gsc.getSystemService(Context.VIBRATOR_SERVICE);
                                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                //    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                //} else
                                //    v.vibrate(500);

                                touchables.get(z).whenHeld();
                            }
                        }
                        else{
                            touchableTouched [z] = false;
                        }
                    }
                }
                if (superPressed != -1)
                    touchables.get(superPressed).whileHeld(x-lastX, y-lastY);
                lastX = x;
                lastY = y;
                break;
        }
    }
}

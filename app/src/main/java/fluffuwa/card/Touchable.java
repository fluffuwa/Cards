package fluffuwa.card;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;


public abstract class Touchable {

    //protected Path p;
    //private Region r;
    protected Bitmap i;
    protected String name;
    double width;
    double height;
    public double curX;
    public double curY;
    //get name
    public final String getName(){
        return name;
    }

    public Touchable (String name, double width, double height, double x, double y){
        this.name = name;
        this.width = width;
        this.height = height;
        curX = x;
        curY = y;
        if (!name.equals (""))
            i = getImage (name);
        try {
            i = Bitmap.createScaledBitmap(i, (int)width, (int)height, false);
        }
        catch (Exception e){
            System.out.println ("couldn't resize bitmap..." + name);
        }
    }

    /*public Touchable (String name, double [] xs, double [] ys, double x, double y) {
        this.name = name;
        p = new Path();
        p.moveTo((float) xs[0], (float) ys[0]);
        for (int a = 1; a < xs.length; a++) {
            p.lineTo((float) xs[a], (float) ys[a]);
        }
        p.close();
        RectF rect = new RectF();
        p.computeBounds (rect, true);
        width = rect.width();
        height =rect.height();
        System.out.println (x + ", " + y + ", " + width + ", " + height);
        curX = x;
        curY = y;
        updateRegion();
        if (!name.equals (""))
            i = getImage (name);
        try {
            i = Bitmap.createScaledBitmap(i, (int)width, (int)height, false);
        }
        catch (Exception e){
            System.out.println ("couldn't resize bitmap...");
        }
    }
    public Touchable (String name, double width, double height){
        this (name, width, height, 0, 0);
    }
    public Touchable (String name, double width, double height, double x, double y){
         this (name, new double [] {0, width, width, 0}, new double []{0, 0, height, height}, x, y);
    }*/


    public static double w;
    public static double h;
    public static double px;
    public static double py;


    //used to get images
    private static GameStateController es;
    public static final void setGSC (GameStateController es2){
        es = es2;
        w = es.displaySize.x;
        h = es.displaySize.y;
        //Rect rect = new Rect ();
        //es.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //h -= rect.top;
        h -= 24;
        px = w/90.0;
        py = h/160.0;
    }
    protected static final Bitmap getImage (String name){
        Bitmap i = es.getDrawable(name, false);
        return i;
    }

    public void setImage (Bitmap i){
        this.i = i;
    }
    public Bitmap getImage (){
        return i;
    }

    //in case it wants to be edited some other way
    //public Path getPath (){
    //    return p;
    //}
    ////should be called after playing with getPath
    //public void updateRegion (){
    //    RectF rectF = new RectF();
    //    p.computeBounds(rectF, true);
//
    //    r = new Region();
    //    r.setPath(p, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
    //}

    /*double prevrot = 0;
    public Bitmap setRotation (double deg){
        setImage (getRotImage(deg, name));
        rotate (deg-prevrot);
        prevrot = deg;
        return getRotImage(deg, name);
    }

    //will need to do some fancy things to keep it centered
    public static final Bitmap getRotImage(double angle, String name){
        if (GameStateController.images.containsKey(name + angle)){
            return GameStateController.images.get (name + angle);
        }

        Matrix m = new Matrix ();
        m.setRotate((float)angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(GameStateController.images.get (name) , 0, 0,
                GameStateController.images.get (name) .getWidth(), GameStateController.images.get (name) .getHeight(), m, true);
        GameStateController.images.put (name + angle, rotatedBitmap);
        return rotatedBitmap;
    }


    //degrees
    public void rotate (double rot){
        //RectF rectF = new RectF();
        //p.computeBounds(rectF, true);
        //p.offset ((rectF.left - rectF.right)/2.0f, (rectF.top - rectF.bottom)/2.0f);
        Matrix m = new Matrix();
        m.setRotate((float)rot);
        p.transform(m);
        //p.offset ((rectF.right - rectF.left)/2.0f, (rectF.bottom - rectF.top)/2.0f);
        updateRegion ();
    }*/


    //when let go aftet less than 1 second
    public abstract void whenTouched ();
    //when let go after 1 second
    public abstract void whenHeld ();
    //when dragged
    public abstract void whileHeld (double dx, double dy);

    //can be overridden so that in collection, you can drag and drop cards
    public boolean touched(double pointX, double pointY){
        //pointX -= curX;
        //pointY -= curY;
        return (pointX > curX && pointX < (curX + width) && pointY > curY && pointY < (curY + height));
        //return (r.contains ((int)pointX, (int)pointY));
    }

}

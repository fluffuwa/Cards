package fluffuwa.card;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import fluffuwa.card.Enemies.Enemy;

public class EnemyList extends GameState {

    public EnemyList (){
        super ("EnemyList");
        touchables.add (new Touchable ("tocollection", px*20, py*8, w*0.7, h*0.1){
            public void whenTouched (){
                gsc.gs = new Collection ();
            }
            public void whileHeld (double dx, double dy){

            }
            public void whenHeld(){

            }
        });

        for (int x = 0; x < Enemy.enemies.length; x ++) {
            final int z = x;
            touchables.add(new Touchable(Enemy.enemies [x].name, px * 15, py * 15, w * 0.3, h * 0.5 + h*0.1*x) {
                public void whenTouched() {
                    gsc.gs = new Battle (Enemy.enemies [z].name);
                }

                public void whileHeld(double dx, double dy) {

                }

                public void whenHeld() {

                }
            });
        }
    }

    public void onDraw (Canvas c){
        Paint p = new Paint();
        p.setColor (Color.BLACK);
        c.drawRect (50, 50, 100, 100, p);
    }
}

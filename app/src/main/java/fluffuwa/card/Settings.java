package fluffuwa.card;

import android.graphics.Canvas;

public class Settings extends GameState {

    static boolean showOpponentCards = false;

    public Settings (){
        super ("Settings");

        showOpponentCards = gsc.read ("revealedenemycards", "false").equals ("true");
        touchables.add (new Touchable ("revealedenemycards", w/3, h/10, w/3, h/5){
            public void whenTouched (){
                showOpponentCards = !showOpponentCards;
                gsc.write ("revealedenemycards", showOpponentCards?"true":"false");
            }
            public void whenHeld (){

            }
            public void whileHeld (double dx, double dy){

            }
        });

        touchables.add (new Touchable ("reset", w/3, h/10, w/3, h/5*3){
            public void whenTouched (){
                //reset values
                gsc.write ("deck", gsc.defaultDeck);
                gsc.write ("collection", gsc.defaultCollection);
                gsc.write ("health", gsc.defaultHealth);
                gsc.write ("completedCounts", gsc.defaultCompleteds);
                gsc.setValues();
            }
            public void whenHeld (){

            }
            public void whileHeld (double dx, double dy){

            }
        });
    }

    public void onDraw (Canvas c){

    }
}

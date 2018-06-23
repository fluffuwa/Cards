package fluffuwa.card.Cards;

public class Punch extends Card {
    public Punch (){
        super ("punch", 5);
    }
    //when this and going-through card came from same unit. go down the column of the spreadsheet.
    public Card usuallyBoostAttack (Card card){
        switch (card.getName()){
            case "punch":
                card.damage ++;
                break;
            case "bite":
                card.damage *= 1.2;
                break;

        }
        return card;
    }
    //when going-through card comes from opponent. go down the column of the spreadsheet.
    public Card usuallyWeakenAttack (Card card){
        switch (card.getName()){
            case "punch":
                card.damage --;
                break;
            case "kick":
                card.damage ++;
                break;
            case "bite":
                card.damage *= 0.9;
                break;
        }
        return card;
    }
    public void endOfTurnEffect (){
        //idk how this will work
    }
}

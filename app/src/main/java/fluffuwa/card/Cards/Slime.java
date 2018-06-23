package fluffuwa.card.Cards;

public class Slime extends Card {
    public Slime (){
        super ("slime", 2);
    }
    //when this and going-through card came from same unit. go down the column of the spreadsheet.
    public Card usuallyBoostAttack (Card card){
        switch (card.getName()){
            case "punch":
                card.damage *= 0.8;
                break;
            case "kick":
                card.damage *= 0.8;
                break;
            case "slime":
                card.damage ++;
            case "claw":
                card.damage *= 0.5;
                break;
            case "bite":
                card.damage *= 0.1;
                break;
            case "acid":
                ae = 0;
                break;
        }
        return card;
    }
    //when going-through card comes from opponent. go down the column of the spreadsheet.
    public Card usuallyWeakenAttack (Card card){
        switch (card.getName()){
            case "punch":
                card.damage *= 0.7;
                break;
            case "kick":
                card.damage *= 0.7;
                break;
            case "claw":
                card.damage *= 0.8;
                break;
            case "bite":
                card.damage *= 0.05;
                break;
            case "acid":
                ae = 0;
                break;
        }
        return card;
    }
    public void endOfTurnEffect (){
        //idk how this will work
    }
}

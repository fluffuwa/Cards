package fluffuwa.card.Enemies;

import fluffuwa.card.Cards.Card;
import fluffuwa.card.GameStateController;

public class Player extends Enemy {
    public Player (){
        super ("player", GameStateController.health);

        for (int x = 0; x < gsc.deck.size(); x ++){
            decklist.add (Card.getCard (gsc.deck.get(x).getName()));
        }
    }
}

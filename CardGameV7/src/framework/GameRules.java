package framework;

import java.util.List;

import game.Card;
import game.Player;

public interface GameRules {
	
	public boolean isTalonEmpty();

	boolean isCardsEmpty();

	public void addCardOnTalon(Card card);
	
	public void removeCardOnTalon(Card card);
	
	public void removeCardOnTalon(int index);

	public void addCardsOnTalon(List<Card> card);

	public void createAllCards();

	public Player isWon(Player player1, Player player2);


	
}

package framework;

import java.rmi.Remote;
import java.rmi.RemoteException;

import game.Card;

public interface PlayerRules{

	public void addHandCard(Card card);
	
	public void deleteHandCard(Card card);
	
	public void deleteHandCards(int index);
	
	public void addTakenCards(Card card);
	
	public int getPoints();
	
	public int getNumberOfTakenCards();
	
	public int getScore();

}

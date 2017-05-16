package game;

import java.util.ArrayList;
import java.util.List;

import framework.PlayerRules;
import main.ProgramGUI;


public class Player implements PlayerRules{

	private static final long serialVersionUID = 6496823018095577540L;
	
	ProgramGUI program;
	private List<Card> handCards;
	private List<Card> takenCards  = new ArrayList<>();
	private int numberOfTable;
	static private boolean myMove = false;
	
	public boolean isMyMove() {
		return myMove;
	}

	public void setMyMove(boolean myMove) {
		this.myMove = myMove;
	}

	public Player(){
		this.handCards = new ArrayList<Card>(6);  //***********************na pocetku ima 6 karte u ruci
		this.numberOfTable = 0;
	
	}
	
	public void addNumberOfTable(){
		numberOfTable++;
	}
	public int getNumberOfTable() {
		return numberOfTable;
	}

	public void setNumberOfTable(int numberOfTable) {
		this.numberOfTable = numberOfTable;
	}

	public Player(List<Card> handCards){
		this.handCards = handCards;
	}

	
	public Card getCardByIndex(int index){
		return handCards.get(index);
	}
	
	public void removeCard(Card card){
		for(int i=0; i< handCards.size(); i++){
			if(handCards.get(i).equals(card)){
				handCards.remove(i);
			}
		}
	}
	
	public List<Card> getHandCards() {
		return handCards;
	}

	public void setHandCards(List<Card> handCards) {
		this.handCards = handCards;
	}

	
	public List<Card> getTakenCards() {
		return takenCards;
	}

	public void setTakenCards(List<Card> takenCards) {
		this.takenCards = takenCards;
	}
	
	public void addHandCard(Card card){
		handCards.add(card);
	}
	
	public void deleteHandCard(Card card){
		handCards.remove(card);
	}
	
	public void deleteHandCards(int index){
		handCards.remove(index);
	}
	
	
	public void addTakenCards(Card card){
		takenCards.add(card);
	}

	public int getPoints(){
		int count = 0;
		
		for (Card card : takenCards) {
			count += card.getPoints();
		}
		return count;
	}
	
	
	public int getNumberOfTakenCards(){
		return takenCards.size();
	}

	@Override
	public int getScore() {
		int score = 0;
		score = getPoints() + getNumberOfTable()*3;
		return score;
	}
}

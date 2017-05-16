package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import framework.GameRules;

public class GameBase implements GameRules {

	private final int LIMIT = 101;

	static private List<Card> cards;
	//static private List<Card> cardsOnTalon;

	public GameBase() {
		{
			cards = new ArrayList<Card>();
			//cardsOnTalon = new ArrayList<Card>();
			createFirstTen();
			createCardsWithImage();
			Collections.shuffle(cards);
		}

	}

	public void setCards(List<Card> list){
		this.cards=list;
	}
	
	public GameBase(List<Card> cards) {
		this.cards = cards;
	//	cardsOnTalon = new ArrayList<Card>();


	}

	// vraca karte sa talona koje je potrebno poslati preko klijentu
	public List<Card> getCards() {
		return Collections.unmodifiableList(cards);
	}

	@Override
	public boolean isTalonEmpty() {
		return false;//cardsOnTalon.isEmpty();
	}

	@Override
	public boolean isCardsEmpty() {
		return cards.isEmpty();
	}

	@Override
	public void addCardOnTalon(Card card) {
		//cardsOnTalon.add(card);   //*******************************************postavlja kartu za talon u listu
	}

	@Override
	public void addCardsOnTalon(List<Card> card) {
		/*for (Card c : card) {
			cardsOnTalon.add(c);
		}*/
	}

	@Override
	public Player isWon(Player player1, Player player2) {  //**************************ovo treba promeniti

		if (player1.getScore() < LIMIT || player2.getScore() < LIMIT) {
			return null;
		}
		if (player1.getScore() > player2.getScore()) {
			return player1;
		}
		if (player2.getScore() > player1.getScore()) {
			return player2;
		} else if (player1.getNumberOfTakenCards() > player2.getNumberOfTakenCards()) {
			return player1;
		} else {
			return player2;
		}
	}

	public void createAllCardsAndShuffle() {
		Collections.shuffle(cards);
	}

	public Card popCard() {
		Card card = cards.get(0);
		cards.remove(0);
		return card;
	}

	@Override
	public void createAllCards() {   //ovo nista ne radi?
		// createFirstTen();
		// createCardsWithImage();
	}
	@Override
	public void removeCardOnTalon(Card card) {
		//cardsOnTalon.remove(card);
	}
	@Override
	public void removeCardOnTalon(int index) {
		//cardsOnTalon.remove(index);
	}

	public List<Card> getList() {
		return cards;
	}
	
	/*public List<Card> getListOnTalon(){ //dodala sam da bih testirala
		return cardsOnTalon;
	}*/

	private static void createFirstTen() {  
		/**
		 * Pravimo kartu A posebno zbog bodovanja
		 */
		{
			int i = 1;
			for (int j = 0; j < 4; j++) {
				cards.add(new Card(TypesOfCards.values()[j].toString(), i, 1));
			}
		}
		/**
		 * Zatim pravimo i ostale prazne karte
		 */
		for (int i = 2; i <= 9; i++) {
			for (int j = 0; j < 4; j++) {
				cards.add(new Card(TypesOfCards.values()[j].toString(), i, 0));
			}
		}
		/**
		 * Karte 10 izdvajamo od prvih 9 jer imaju 1 poen Proveravamo da li je
		 * karta tipa Kocka i postavljamo njenu vrednost poena na 2
		 */
		int i = 10;
		for (int j = 0; j < 4; j++) {
			String mark = TypesOfCards.toString(TypesOfCards.values()[j]);
			if (mark.equalsIgnoreCase(TypesOfCards.toString(TypesOfCards.DIAMONDS))) {
				cards.add(new Card(TypesOfCards.values()[j].toString(), i, 2));
			} else {
				cards.add(new Card(TypesOfCards.values()[j].toString(), i, 1));
			}
		}
		/**
		 * Trazimo kartu 2 karo i postavimo joj vrednost poena na 1
		 */
		for (Card card : cards) {
			if (card.getName().equalsIgnoreCase(TypesOfCards.toString(TypesOfCards.CLUBS)) && card.getNumber() == 2) {
				card.setPoints(1);
				break;
			}
		}
	}

	private static void createCardsWithImage() {
		for (int i = 12; i <= 14; i++) {
			for (int j = 0; j <= 3; j++) {
				cards.add(new Card(TypesOfCards.toString(TypesOfCards.values()[j]), i, 1));
			}
		}
	}


	/* Za testiranje
	public static void main(String[] args) {
		GameBase bg = new GameBase();
		List<Card> cards = bg.getList();
		
		List<Card> cardsOnTalon=bg.getListOnTalon();
		
		System.out.println("Karte na talonu: ");
		int j = 0;
		while (j < cards.size()) {
			System.out.println(bg.popCard());
			System.out.println(bg.cardsOnTalon.size());
			System.out.println(bg.popCard());
			System.out.println(bg.cardsOnTalon.size());
		}
		
		
		// for (Card c : cards) {
		// System.out.println(c);

		// System.out.println(bg.popCard());
		// }
		System.out.println("Ceo spil sa 51 kartom: ");
		int i = 0;
		while (i < cards.size()) {
			System.out.println(bg.popCard());
			System.out.println(bg.cards.size());
			System.out.println(bg.popCard());
			System.out.println(bg.cards.size());
		}
		
		System.out.println("Karte na talonu: ");   // ova lista ostaje prazna da
	    for(Card c: cardsOnTalon){
	    	System.out.println(c);   
	    }
	    
	    System.out.println("Da li je talon prazan? " + bg.isTalonEmpty());
	}*/
}

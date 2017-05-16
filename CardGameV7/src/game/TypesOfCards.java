package game;

public enum TypesOfCards {
	
	CLUBS, SPADES, HEARTS, DIAMONDS;
	
	
	@SuppressWarnings("rawtypes")
	public static String toString(Enum type){
		return type+"";
	}
}

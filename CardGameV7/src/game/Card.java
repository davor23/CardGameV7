package game;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Card extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private Icon icon;
	private String name;
	private int number;
	private int points;
	private JLabel labela = new JLabel();

	
	public Card(String icon){
		this.icon = new ImageIcon(icon);
	}
	
	public Card(String name, int number, int points) {
		//labela = new JLabel();
		
		this.name = name;
		this.number = number;
		this.points = points;
		this.icon = new ImageIcon("res/"+number+name+".png");
		
		
		add(labela);
		labela.setIcon(icon);
		//Providna pozadina
		setBackground(new Color(0f,0f,0f,.0f ));
	}

	
	public String getName() {
		return name;
	}


	public void setName(String nanameziv) {
		this.name = name;
	}


	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return getName()+"-"+getNumber()+"-"+getPoints();
	}
	
	public static Card fromString(String line){
		String[] lines = line.split("-");
		return new Card(lines[0], Integer.parseInt(lines[1]), Integer.parseInt(lines[2]));

	}
	
	
	
	@Override
	public int hashCode() {
		int result=3;
		int c=name.hashCode()+number*points;
		result=37*result+c;
		return result;
		
		
		
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// Objekat je identican
		if (this.hashCode() == obj.hashCode()) {
			return true;
		}
		// Null je uvek razlicit
		if (obj == null) {
			return false;
		}
		// Ako su klase razlicite, objekti ne mogu bili jednaki
		if (getClass() != obj.getClass()) {
			return false;
		}

		// pretvaramo objekat u kartu
		Card c = (Card) obj;

		// Prvo proveravamo naziv karte
		if (name != c.getName()) {
			return false;
		}

		// A potom broj karte
		if (number != c.getNumber()) {
			return false;
		}
		// A onda i broj poena
		if (points != c.getPoints()) {
			return false;
		}
		// Proverili smo polja i sva su jednaka
		return true;
	} 
}

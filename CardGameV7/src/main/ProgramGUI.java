package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;

import Server.Reader;
import Server.Server;
import game.Card;
import game.GameBase;
import game.Player;
import javax.swing.JTextField;

public class ProgramGUI extends JFrame {

	private static final long serialVersionUID = 5676444787596641610L;

	private GameBase gameBase;
	private static Player serverPlayer = new Player();
	private static Player clientPlayer = new Player();

	private Socket opponent;
	private Server server;
	private PrintWriter socketOut;

	private boolean myMove;
	private boolean iAmClient;
	private boolean closing;
	
	
	private int clientScore = 0;
	private int serverScore= 0;
	
	JPanel prviGrid;
	JPanel gore;
	JPanel centar;
	JPanel dole;
	JPanel desno;

	private static Color GREEN = new Color(46, 139, 87);
	private List<Card> selected = new ArrayList<Card>();
	private List<Card> talon = new ArrayList<Card>();

	JButton nosi = new JButton("Nosi");
	JButton dalje = new JButton("Dalje");

	static DefaultListModel dlm;
	static JList<Card> bottomCards;
	static {

		dlm = new DefaultListModel<>();
		bottomCards = new JList<Card>();
		bottomCards.setVisibleRowCount(1);
		bottomCards.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		bottomCards.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		bottomCards.setBackground(new Color(46, 139, 87));
	}

	DefaultListModel dlm2;
	JList<Card> topFakeCards;
	{
		dlm2 = new DefaultListModel<>();
		topFakeCards = new JList<Card>();
		topFakeCards.setVisibleRowCount(1);
		topFakeCards.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		topFakeCards.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		topFakeCards.setBackground(new Color(46, 139, 87));
		topFakeCards.setModel(dlm2);

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProgramGUI window = new ProgramGUI();
					window.setTitle("Tablic"); //
					window.setMinimumSize(new Dimension(768, 700));
					window.setSize(768, 700);
					window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */

	public ProgramGUI() {

		// Ovo instanciranje ne sme biti ovde, samo je privremeno
		// dok se ne uspostavi prenos celog spila

		this.gameBase = new GameBase();
		
		initGui();
		//createGUI();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				// Zada korisnik zatvori prozor, "cistimo" resurse
				closing = true;
				if (server != null) {
					server.close();
					server = null;
				}
				if (opponent != null) {
					try {
						opponent.close();
					} catch (IOException ex) {
					}
					opponent = null;
				}
			}
		});
	}

	private void createGUI() {

		if (!iAmClient)
			sendSpil();

		gore = new JPanel();
		gore.setBackground(GREEN);
		centar = new JPanel();
		centar.setBackground(GREEN);
		dole = new JPanel();
		dole.setBackground(GREEN);
		// prekomplikovano sa dve labele
		prviGrid = new JPanel();
		// drugiGrid = new JPanel();
		prviGrid.setBackground(GREEN);
		// drugiGrid.setBackground(Color.GREEN);
		prviGrid.setLayout(new GridLayout(2, 10));
		// drugiGrid.setLayout(new GridLayout(2, 4));

		centar.add(prviGrid);
		// centar.add(drugiGrid);

		// staviNaTalon();
		getContentPane().add(gore, BorderLayout.NORTH);
		getContentPane().add(centar, BorderLayout.CENTER);
		getContentPane().add(dole, BorderLayout.SOUTH);

		nosi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// refreshButtons(myMove); - zakljucuvanje/ otkljucavanje
				// dugmica zbog testiranje
				if (!myMove) {
					JOptionPane.showMessageDialog(null, "Nije tvoj red na igranje");
					return;
				}
				String poruka = "";
				boolean ok = false;
				if (selected.isEmpty() && bottomCards.getSelectedValue() == null) {
					JOptionPane.showMessageDialog(centar, "Morate izbaciti jednu kartu!");
					
				} else if (selected.isEmpty() && bottomCards.getSelectedValue() != null) {
					JOptionPane.showMessageDialog(centar, "Niste oznacili kartu koju zelite da odnesete!");

				} else {
					// Uzmemo sliku karte koju izbacujemo i nadjemo koja je o
					// karta

					Icon icon = (Icon) bottomCards.getSelectedValue();
					Card karta = findCard(icon);

					ok = proveri(karta);

				}

				if (!ok) {

					JOptionPane.showMessageDialog(centar, "Karte koje ste oznacili ne mozete odneti!");
					return;
				}

				// pakovanje vrednosti za slanje preko socketa

				String poruka2 = "";

				//Card karta = findCard((Icon) bottomCards.getSelectedValue());
				Card karta = findCardByIndex(bottomCards.getSelectedIndex());
				poruka += karta.toString();

				for (Card c : selected) {
					poruka2 += ":" + c.toString();
				}
				poruka += poruka2;
				
				if(iAmClient){
					clientPlayer.removeCard(karta);
				}else{
					serverPlayer.removeCard(karta);
				}
				// According to the javadoc, using remove() instead of
				// removeElementAt() is recommended
				int index = bottomCards.getSelectedIndex();
				if (index != -1) {
					dlm.remove(index);
				}
				refreshBottomCards();
			

				for (int i = 0; i < selected.size(); i++) {
					talon.remove(selected.get(i));
					prviGrid.remove(selected.get(i));
					
				}
				selected.clear();
				

				if (talon.size() == 0) {
					JOptionPane.showMessageDialog(centar, "Talon size: " + talon.size());

					// treba obrnuto

					if (!iAmClient)
						serverPlayer.addNumberOfTable();
					else
						clientPlayer.addNumberOfTable();

					// ovo zbog protivnickog igraca
					JOptionPane.showMessageDialog(null, "Kliknite na dalje");

				} else {
					
					selected.clear();

				}

				// Potez saljemo preko mreze - da pokusam ovo da prebacim na
				// drugo mesto - nista ne menja jer je poruka poslata

				System.out.println("Slanje POTEZA: " + poruka);
				socketOut.println(poruka);

				validate();
				repaint();

				// Da li je neko pobedio?
				// checkState();

				// Protivnik je sada na redu

				myMove = false;
			}
		});

		desno = new JPanel();
		desno.setLayout(new BoxLayout(desno, BoxLayout.Y_AXIS));
		desno.setBackground(GREEN);
		
		hisScore = new JPanel();
		hisScore.setBackground(GREEN);
		desno.add(hisScore);
		
		hisPoints = new JTextField();
		hisPoints.setBackground(GREEN);
		hisPoints.setText("Protivnicki poeni:");
		hisPoints.setEditable(false);
		hisScore.add(hisPoints);
		hisPoints.setColumns(10);
		
		lblHisPoints = new JLabel("");
		hisScore.add(lblHisPoints);
		desno.add(Box.createVerticalStrut(50));
		desno.add(nosi);

		dalje.addActionListener(new ActionListener() { // ovo bi trebalo da je
														// ok

			public void actionPerformed(ActionEvent e) {
				// refreshButtons(myMove);

				if (!myMove) {
					JOptionPane.showMessageDialog(null, "Nije tvoj red na igranje");
					return;
				}
				if (bottomCards.isSelectionEmpty()) {
					JOptionPane.showMessageDialog(centar, "Morate oznaciti kartu koju zelite da izbacite!");
				} else {

					String poruka = "";
				
					Card karta = findCard((Icon) bottomCards.getSelectedValue());
					if (karta != null)
						poruka += karta.toString() + ":";
					else
						JOptionPane.showMessageDialog(null, "Error: karta nije nadjenja u ruci");

					socketOut.println(poruka);

					Card card = findCardByIndex(bottomCards.getSelectedIndex());
					card.addMouseListener(mouseListerTalon);
				
					int index = bottomCards.getSelectedIndex();
					if (index != -1) {
						dlm.remove(index);
					}
					refreshBottomCards();


					talon.add(card);
					prviGrid.add(card);
					validate();
					repaint();

					myMove=false;

				}
			}
		});

		
		desno.add(dalje);
		desno.add(Box.createVerticalStrut(50));
		getContentPane().add(desno, BorderLayout.EAST);
		
		myScore = new JPanel();
		myScore.setBackground(GREEN);
		desno.add(myScore);
		
		myPoints = new JTextField();
		myPoints.setBackground(GREEN);
		myPoints.setText("Vasi poeni:");
		myPoints.setEditable(false);
		myScore.add(myPoints);
		myPoints.setColumns(10);
		
		lblMyPoints = new JLabel("");
		myScore.add(lblMyPoints);
		
		levo = new JPanel();
		levo.setLayout(new BoxLayout(levo, BoxLayout.Y_AXIS));
		levo.setBackground(GREEN);
		getContentPane().add(levo, BorderLayout.WEST);
	
		setButtonAllCards();
		btnSpil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chackCurrentScore();
				setCurrentPoints();
				if(dlm.isEmpty() && dlm2.isEmpty()){
					if(iAmClient){
						deliKlijent();
					}else{
						deliServer();
					}
				}else{
					JOptionPane.showMessageDialog(centar, "Karte se dele kada nemate ni jednu u rukama!");
				}
				
				if(gameBase.getCards().size() == 3){
					setButtonThreeCards();
				}else if(gameBase.getCards().size() == 2){
					setButtonTwoCards();
				}else if(gameBase.getCards().size() == 1){
					setButtonOneCards();
				}else{
					setButtonEmptyCards();
				}
			}
		});
		
		levo.add(btnSpil);
		setCurrentPoints();
		validate();
		repaint();

		if (!iAmClient) {
			initTalon();
			deliServer();
		}

		else{
			initTalon();
			deliKlijent();
		}
	}

	public void refreshButtons(boolean myMove) {
		if (myMove) {
			dalje.setEnabled(true);
			nosi.setEnabled(true);
		} else {
			dalje.setEnabled(false);
			nosi.setEnabled(false);
		}
	}

	private Card findCardByIndex(int index) {
		if (!iAmClient) {
			return serverPlayer.getCardByIndex(index);
		} else {
			return clientPlayer.getCardByIndex(index);
		}
	}

	// postaviNaTalon() - preimenovala u initTalon(), bio mi je konfuzan naziv
	// metoda kada se zove sa nekog drugog meta
	private void initTalon() {
		System.out.println("Postavlja se na talon, ");
		for (int i = 0; i < 4; i++) {
			// JToggleButton dodaj = new JToggleButton();
			// dodaj.setSize(new Dimension(100, 140));
			// dodaj.setMaximumSize(new Dimension(100, 140));

			Card card = gameBase.popCard();
			card.addMouseListener(mouseListerTalon);
			// dodaj.add(card);
			talon.add(card);
			prviGrid.add(card);
		}
		validate();
		repaint();
	}

	final MouseAdapter mouseListerTalon = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			Card se = (Card) e.getComponent();
			if (se.getBackground().equals(Color.YELLOW)) {
				se.setBackground(GREEN);
			} else {
				se.setBackground(Color.YELLOW);
			}
			if (selected.contains(se)) {
				selected.remove(se);
				validate();
				repaint();
			} else {
				selected.add(se);
				validate();
				repaint();
			}
		}

	};
	private JPanel levo;
	private JButton btnSpil;
	private JPanel myScore;
	private JPanel hisScore;
	private JTextField hisPoints;
	private JTextField myPoints;
	private JLabel lblHisPoints;
	private JLabel lblMyPoints;

	/*
	 * private void dodajNaTalon(Card card) { // mozda ovaj metod ne radi
	 * 
	 * // karta.addMouseListener(mb); prviGrid.add(card); talon.add(card);
	 * 
	 * validate(); repaint(); }
	 */

	private void refreshBottomCards() {
		bottomCards.setModel(dlm);
	}

	private void refreshFakeCards() {
		topFakeCards.setModel(dlm2);
	}

	/*
	 * public void deli(){ if (clientPlayer.isMyMove()) { for (int i = 0; i < 6;
	 * i++) { Card card = gameBase.popCard(); clientPlayer.addHandCard(card);
	 * dlm.addElement(card.getIcon()); } // clientPlayer.setMyMove(false);
	 * validate(); repaint(); } else { for (int i = 0; i < 6; i++) { Card card =
	 * gameBase.popCard(); serverPlayer.addHandCard(card);
	 * dlm.addElement(card.getIcon()); } // serverPlayer.setMyMove(true);
	 * validate(); repaint(); } bottomCards.setModel(dlm);
	 * dole.add(bottomCards); printFakeCards(6); }
	 */

	public void deliServer() {
		System.out.println("Deli server");
		
		//prvo dodeli klijentu
		for (int i = 0; i < 6; i++) {
			gameBase.popCard();
		}
		//pa onda sebi
		for (int i = 0; i < 6; i++) {
			Card card = gameBase.popCard();
			serverPlayer.addHandCard(card);
			dlm.addElement(card.getIcon());
		}

		validate();
		repaint();

		

		bottomCards.setModel(dlm);
		dole.add(bottomCards);
		printFakeCards(6);

	}

	public void deliKlijent() {
		System.out.println("Deli klijent");
		//prvo dodeli sebi
		for (int i = 0; i < 6; i++) {
			Card card = gameBase.popCard();
			clientPlayer.addHandCard(card);
			dlm.addElement(card.getIcon());
		}

		validate();
		repaint();

		//pa onda serveru
		for (int i = 0; i < 6; i++) {
			gameBase.popCard();
		}
		bottomCards.setModel(dlm);
		dole.add(bottomCards);
		printFakeCards(6);

	}

	private void printFakeCards(int count) {
		for (int i = 0; i < count; i++) {
			Card fake = new Card("res/0BACK.png");
			dlm2.addElement(fake.getIcon());
		}
		topFakeCards.setModel(dlm2);
		gore.add(topFakeCards);
		validate();
		repaint();
	}

	private void initGui() {


		JPanel top = new JPanel();
		final JButton btnServer = new JButton("Server");
		top.add(btnServer);
		final JButton btnClient = new JButton("Client");
		top.add(btnClient);

		btnServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String val = JOptionPane.showInputDialog(ProgramGUI.this, "Server port:", "6060");
				if (val == null || val.length() == 0)
					return;
				try {
					int port = Integer.parseInt(val);
					// Pokreni server na ovom portu
					server = new Server(ProgramGUI.this, port);
					server.start();
					// ovde bi isao serverPlaying ako mi bude trebalo
					getContentPane().removeAll();
					getContentPane().add(new JLabel("Waiting for a client..."));
					validate(); // Kada menjamo komponente u prozoru,
					repaint(); // moramo pozvati ove metode da bi se izmene
								// videle
					// ovo sam ja dodala - i dalje brljavi sa redosledom
					// myMove=false;

				} catch (Exception ex) {
					System.out.println("Error while starting the server: " + ex.getMessage());
					JOptionPane.showMessageDialog(ProgramGUI.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String val = JOptionPane.showInputDialog(ProgramGUI.this, "Server address and port:", "localhost:6060");
				if ((val == null) || (val.length() == 0)) {
					return;
				}
				try {
					int n = val.indexOf(':');
					String host = val.substring(0, n);
					int port = Integer.parseInt(val.substring(n + 1));
					// Konektuj se na zadatu adresu i otvori komunikaciju
					Socket client = new Socket(host, port);
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				
					clientPlayer = new Player();
					//klijent uvek igra prvi
					iAmClient = true;
					myMove = true;

					onConnectionEstablished2(client, in, out, clientPlayer);
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("Error while starting the server: " + ex.getMessage());
					JOptionPane.showMessageDialog(ProgramGUI.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		getContentPane().add(top, BorderLayout.NORTH);

	
	}

	private ProgramGUI vratiFrejm() {
		return this;
	}

	public void onRemoteMove(String move) {
		
		
		if (myMove) {
			if(!iAmClient)
				return;
		}
		
		System.out.println("Stigla nam je poruka: " + move);

		// ako je poslat ceo spil??

		String naslov = move.substring(0, move.indexOf(":"));

		if (naslov.equalsIgnoreCase("SPIL")) {
			if(!iAmClient)
				return;
			System.out.println("Stigao nam je SPIL");
			List<Card> listaKarata = new ArrayList<>();
			String[] splitPoDvotacki = move.split(":");
			for (int i = 1; i < splitPoDvotacki.length; i++) {
				Card c = Card.fromString(splitPoDvotacki[i]);
				listaKarata.add(c);
			}
			// gameBase = new GameBase(listaKarata);
			gameBase.setCards(listaKarata);
			
			createGUI();
			
			
			
			return;
		} 

		System.out.println("Stigao nam je potez koji nije slanje spila"); // samo
																			// zbog
																			// testiranja
		String[] odvajamoPoDvotacki = move.split(":");

		// za testiranje
		// ispise se
		System.out.println("Provera niza splitovana po :-u");
		for (String s : odvajamoPoDvotacki) {
			System.out.println(s);
		}

		Card bacenaKarta = Card.fromString(odvajamoPoDvotacki[0]);

		if (odvajamoPoDvotacki.length != 1) {

			List<Card> takenCards = new ArrayList<>();
			for (int i = 1; i < odvajamoPoDvotacki.length; i++) {
				takenCards.add(Card.fromString(odvajamoPoDvotacki[i]));
			}
			System.out.println("Posle provere,kreirana lista karata,sledi uklanjanje sa talona..");

			for (int i = 0; i < takenCards.size(); i++) { // udje u petlju
				System.out.println("Udjemo u for");
				System.out.println("karta u takenCards: " + takenCards.get(i).toString());
				boolean uspelo = brisiSaTalona(takenCards.get(i));
				System.out.println(uspelo + " :obrisali kartu?");

			}

			int index = 0;
			dlm2.remove(index);
			refreshFakeCards();

			validate();
			repaint();

		} else { // znaci da je karta bacena
			System.out.println("Korisnik je bacio kartu: " + bacenaKarta);
			bacenaKarta.addMouseListener(mouseListerTalon); // poruka
															// izgleda -
															// karta
			talon.add(bacenaKarta);
			prviGrid.add(bacenaKarta);
			int index = 0;
			dlm2.remove(index);
			refreshFakeCards();
			validate();
			repaint();

		}

		// Da li je neko pobedio?
		// checkState(); 
		
		myMove=true;
		
		

	}

	private boolean brisiSaTalona(Card c) {
		int ind = -1;
		boolean uspeo = talon.remove(c);
		// prviGrid.remove(c);
		System.out.println("hash code prosledjene karte: " + c.hashCode());
		System.out.println("Componente u prviGrid-u: ");
		Component[] niz = prviGrid.getComponents();
		for (int i = 0; i < niz.length; i++) {
			Component com = niz[i];
			System.out.println(com.toString());
			System.out.println("hash code componente: " + com.hashCode());
			if (com.equals(c)) {
				System.out.println("nasao");
				ind = i;
				break;
			}
		}
		prviGrid.remove(ind);

		prviGrid.validate();
		prviGrid.repaint();

		return uspeo;
	}

	public boolean proveri(Card c) {
		// mora ici Find
		int droppedCard = c.getNumber();
		// nisu pokriveni svi uslovi, ali skoro jesu - ne radi kada kec ima
		// vrednost 11
		if (selected.size() == 0) {
			// mojPotez=false;
			JOptionPane.showMessageDialog(null, "Nista nije selektovano, selektujte ili kliknite dalje");

			return false;
		} else {

			// Proveravamo da li ima vece karte od bacene
			for (Card card : selected) {
				if (card.getNumber() > droppedCard) {
					JOptionPane.showMessageDialog(null, "Ne mozete nositi kartu koja je veceg broja od bacene!");
					return false;
				}
			}

			int sum = 0;
			int sumOfA = 0;
			for (Card card : selected) {
				sum += card.getNumber();
				if (card.getNumber() == 1) {
					sumOfA++;
				}
			}

			// Cudna formula ali za sada funkcionise odlicno
			if (((selected.size() * droppedCard) - sum) % droppedCard == 0) {
				return true;
			}

			// Zbog istog uslova da manja karta ne moze da nosi vecu
			if (droppedCard > 10) {
				for (int i = 0; i < sumOfA; i++) {
					sum += 10;
					if (((selected.size() * droppedCard) - sum) % droppedCard == 0) {
						return true;
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "Niste dobro izracunali, pokusajte ponovo ili kliknite dalje");
				return false;
			}
		}
		// ?? da li treba true ili false?
		return false;
	}

	private void checkState() {
		if (clientPlayer.getHandCards().size() == 0 && serverPlayer.getHandCards().size() == 0 && talon.size() == 0) {
			Player winner = checkWinner();
			// Za ovo nisam siguran da li tako ide
			/*
			 * if (myMove) { if (winner == clientPlayer) {
			 * JOptionPane.showMessageDialog(centar, "You won!" +
			 * "\nThe game will now exit."); } else {
			 * JOptionPane.showMessageDialog(centar, "You lost!" +
			 * "\nThe game will now exit."); } }
			 */
		}
		closing = true;
		dispose();
	}

	private Card findCard(Icon icon) {
		int i = 0;
		if (!iAmClient) {
			for (i = 0; i < serverPlayer.getHandCards().size(); i++) {
				if (serverPlayer.getCardByIndex(i).getIcon().equals(icon)) {
					return serverPlayer.getCardByIndex(i);
				}
				// JOptionPane.showMessageDialog(centar,
				// serverPlayer.getHandCards().get(i).getIcon());
			}
		} else {
			for (i = 0; i < clientPlayer.getHandCards().size(); i++) {
				if (clientPlayer.getCardByIndex(i).getIcon().equals(icon)) {
					return clientPlayer.getCardByIndex(i);
				}
				// JOptionPane.showMessageDialog(centar,
				// clientPlayer.getHandCards().get(i).getIcon());
			}
		}
		System.out.println("nije pronasao kartu u ruci igraca");
		return null;
	}

	private Player checkWinner() {
		if (clientPlayer.getScore() > serverPlayer.getScore()) {

			System.out.println("slanje spila");
			String poruka = "SPIL:";
			List<Card> karte = gameBase.getCards();
			for (Card c : karte) {
				poruka += c.toString() + ":";
			}

			System.out.println(poruka);
			socketOut.println(poruka);

			return clientPlayer;
		}
		if (clientPlayer.getScore() < serverPlayer.getScore()) {
			return serverPlayer;
		}
		if (serverPlayer.getScore() == clientPlayer.getScore()) {
			if (clientPlayer.getTakenCards().size() > serverPlayer.getTakenCards().size()) {
				return clientPlayer;
			} else {
				return serverPlayer;
			}
		}
		return null;
	}

	public void onConnectionEstablished(Socket client, BufferedReader in, PrintWriter out, Player serverPlayer) {

		 this.opponent = client;
		this.socketOut = out;
		ProgramGUI.serverPlayer = serverPlayer;

		new Reader(this, in).start();

		getContentPane().removeAll();

		createGUI();

		validate();
		repaint();

	}

	public void onConnectionEstablished2(Socket client, BufferedReader in, PrintWriter out, Player clientPlayer) {

		System.out.println("Client");
		this.opponent = client;
		this.socketOut = out;
		ProgramGUI.clientPlayer = clientPlayer;

		new Reader(this, in).start();
		getContentPane().removeAll();

		validate();
		repaint();

	}

	public void onConnectionLost() {
		if (!closing) {
			JOptionPane.showMessageDialog(this, "Connection lost, the game will now exit");
			dispose();
		}
	}

	public void sendSpil() {

		System.out.println("slanje spila");
		String poruka = "SPIL:";
		List<Card> karte = gameBase.getCards();
		for (Card c : karte) {
			poruka += c.toString() + ":";
		}

		System.out.println(poruka);
		socketOut.println(poruka);

	}
	
	/**
	 * 
	 * Vrste dugmeta za podelu karata
	 * 
	 */
	

	private void setButtonAllCards(){
		ImageIcon img = new ImageIcon("res/ALLBACK.png");
		btnSpil = new JButton(img);
		btnSpil.setMaximumSize(new Dimension(114, 139));
		btnSpil.setPreferredSize(new Dimension(114, 139));
	}
	
	
	private void setButtonThreeCards(){
		ImageIcon img = new ImageIcon("res/3LASTCARDS.png");
		btnSpil = new JButton(img);
		btnSpil.setMaximumSize(new Dimension(114, 139));
		btnSpil.setPreferredSize(new Dimension(114, 139));
	}
	private void setButtonTwoCards(){
		ImageIcon img = new ImageIcon("res/2LASTCARDS.png");
		btnSpil = new JButton(img);
		btnSpil.setMaximumSize(new Dimension(105, 139));
		btnSpil.setPreferredSize(new Dimension(105, 139));
	}
	private void setButtonOneCards(){
		ImageIcon img = new ImageIcon("res/0BACK.png");
		btnSpil = new JButton(img);
		btnSpil.setMaximumSize(new Dimension(99, 141));
		btnSpil.setPreferredSize(new Dimension(99, 141));
	}
	
	private void setButtonEmptyCards(){
		ImageIcon img = new ImageIcon("res/EMPTY.png");
		btnSpil = new JButton(img);
		btnSpil.setMaximumSize(new Dimension(100, 143));
		btnSpil.setPreferredSize(new Dimension(100, 143));
	}
	
	
	/**
	 * 
	 * Metodi za racunanje trenutnog rezultata
	 * 
	 */
	
	private void chackCurrentScore(){
		if(iAmClient){
			clientScore = clientPlayer.getScore();
		}else{
			serverScore = serverPlayer.getScore();
		}
	}
	
	private void setCurrentPoints(){
		if(iAmClient){
			lblMyPoints.setText(clientScore+"");
			lblHisPoints.setText(serverScore+"");
		}else{			
			lblMyPoints.setText(serverScore+"");
			lblHisPoints.setText(clientScore+"");
		}
	}
}



	
	


package Server;

import java.awt.EventQueue;
import java.io.BufferedReader;

import game.Player;
import main.ProgramGUI;

/**
 * Pomocna nit koja ucitava poteze protivnika i salje ih glavnom frejmu.
 */
public class Reader extends Thread {

	//ne salje poteze protivniku
	
	private BufferedReader in;
	private ProgramGUI frame;
	//private Player clientPlayer;  //zasto je ovo polje tu??? -radi isto i bez njega -napravilo bi 2 klijent plyera
	
	public Reader(ProgramGUI frame, BufferedReader in) {
		this.frame = frame;
		this.in = in;
		//this.clientPlayer = new Player();
	}

	@Override
	public void run() {
		try {
			while (!interrupted()) {
				final String line = in.readLine();
				if (line == null) { // Protivnik je prekinuo igru
					break;
				}

				
				// Ovo moramo izvrsiti na EDT-u
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
					//	frame.addPlayer(clientPlayer);
						
						//da li ovaj metod pravi porb? - mislim da ne
						frame.onRemoteMove(line);
					}
				});

			}
		} catch (Exception ex) {
			// Nista
		} finally {

			// Javljamo glavnom frejmu da je protivnik prekinuo igru
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					frame.onConnectionLost();
				}
			});

		}
	}
}

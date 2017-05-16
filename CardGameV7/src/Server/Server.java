package Server;

import static java.lang.System.out;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import game.Player;
import main.ProgramGUI;

/**
 * Serverska strana mrezne igre Tablic. Server osluskuje konekciju na zadatom
 * portu i obavestava prozor kad se klijent konektuje.

 */
public class Server extends Thread {

	private ProgramGUI frame;
	private ServerSocket server;
	private Player serverPlayer;  
	
	public Server(ProgramGUI frame, int port) throws IOException {
		try {
			this.frame = frame;
			server = new ServerSocket(port);
			out.println("Accepting connections on port " + port);
		} catch (IOException ex) {
			out.println("Could not listen on port " + port + ": " + ex.getMessage());
			throw ex;
		}
	}

	@Override
	public void run() {

		// Inicijalizacija
		Socket client = null;
		BufferedReader in = null;
		PrintWriter socketOut = null;

		try {

			// Prihvatamo novu konekciju
			try {
				client = server.accept();
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				socketOut = new PrintWriter(client.getOutputStream(), true);
				out.println("Accepted a connection from " + client.getRemoteSocketAddress());
			} catch (IOException ex) {
				out.println("Cannot accept a connection: " + ex.getMessage());
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						frame.onConnectionLost();
					}
				});
				return;
			}

	
			
			final Socket client2 = client;
			final BufferedReader in2 = in;
			final PrintWriter out2 = socketOut;
			serverPlayer = new Player();
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					frame.onConnectionEstablished(client2, in2, out2, serverPlayer); 
				}
			});

		// Na kraju moramo osloboditi resurse
		} finally {
			close();
		}

	}

	public void close() {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				// Uradili smo sta smo mogli
			}
			server = null;
		}
	}
}

//Devon Kooker

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;

/*
 * A chat server that delivers public and private messages.
 */
public class Server {

	// The server socket.
	private static ServerSocket serverSocket = null;
	// The client socket.
	private static Socket clientSocket = null;

	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 1000;
	private static final clientThread[] threads = new clientThread[maxClientsCount];

	public static void main(String args[]) {

		// Port Number
		int PORT = 2222;
		System.out.println("Server is Running on PORT: " + PORT);

		// open socket to clients
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println(e);
		}

		// Create new thread for each client
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxClientsCount; i++) {
					if (threads[i] == null) {
						(threads[i] = new clientThread(clientSocket, threads)).start();
						break;
					}
				}
				if (i == maxClientsCount) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}

/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. When a client leaves the chat room this thread informs also
 * all the clients about that and terminates.
 */
class clientThread extends Thread {

	private BufferedReader in = null;
	private PrintStream out = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	boolean inChat = false;

	public clientThread(Socket clientSocket, clientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}

	public void run() {
		int maxClientsCount = this.maxClientsCount;
		clientThread[] threads = this.threads;

		try {
			String accessCode = "cs319spring2020";
			String userCode = null;

			/*
			 * Create input and output streams for this client.
			 */
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			out = new PrintStream(clientSocket.getOutputStream());
			out.println("*** Connected ***");
			out.println(" ");
			out.println("Enter Name: ");
			String name = in.readLine().trim();
			out.println("Enter Access Code: ");
			userCode = in.readLine().trim();
			// verify access code
			while (!userCode.equals(accessCode)) {
				out.println("*** Invalaid Code, Please Try Again ***");
				out.println("Enter Access Code: ");
				userCode = in.readLine().trim();
			}
			out.flush();
			out.println(" ");

			// int userCount = Thread.activeCount();
			System.out.println(" ");
			System.out.println("*** User " + name + " Connected ***");
			System.out.println(" ");

			out.println("Hello " + name
					+ ", welcome to the chat room.\nTo leave enter /quit in a new line.\n___________________________________");
			inChat = true;
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] != null && threads[i] != this && threads[i].inChat == true) {
					out.println(" ");
					threads[i].out.println("*** User " + name + " Has Entered The Chat ***");
					out.println(" ");
				}
			}
			while (true) {
				String line = in.readLine();
				if (line.startsWith("/quit")) {
					break;
				}

				System.out.println(" ");
				System.out.println("<" + name + ">- " + line);

				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this && threads[i].inChat == true) {
						threads[i].out.println("<" + name + ">- " + line);
					}
				}
			}
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] != null && threads[i] != this && threads[i].inChat == true) {
					threads[i].out.println("*** User " + name + " Has Left The Chat ***");
				}
			}
			out.println("*** Bye " + name + " ***");

			/*
			 * Clean up. Set the current thread variable to null so that a new client could
			 * be accepted by the server.
			 */
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] == this) {
					threads[i] = null;
				}
			}
			// }

			/*
			 * Close the output stream, close the input stream, close the socket.
			 */
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
		}

	}
}
//Devon Kooker

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

	// The client socket
	private static Socket clientSocket = null;
	// The output stream
	private static PrintStream out = null;
	// The input stream
	private static BufferedReader in = null;

	private static BufferedReader inputLine = null;
	private static boolean closed = false;

	public static void main(String[] args) {

		// The default port.
		int portNumber = 2222;
		// The default host.
		String IP_adress = "localhost";

		System.out.println("Trying to connect to server, IP: " + IP_adress + ", PORT: " + portNumber);
		System.out.println(" ");

		// Open a socket on a given IP_address and PORT. Open input and output streams.
		try {
			clientSocket = new Socket(IP_adress, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			out = new PrintStream(clientSocket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
		} catch (UnknownHostException e) {
			System.err.println("could not connect to IP: " + IP_adress);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the IP: " + IP_adress);
		}

		// Thread to send Data
		if (clientSocket != null && out != null && in != null) {
			try {

				/* Create a thread to read from the server. */
				new Thread(new Client2()).start();
				while (!closed) {
					out.println(inputLine.readLine().trim());
					System.out.println(" ");
					
				}
				/*
				 * Close the output stream, close the input stream, close the socket.
				 */
				out.close();
				in.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	// thread to read server data
	public void run() {

		// keep reading until user leaves
		String responseLine;
		try {
			while ((responseLine = in.readLine()) != null) {
				if (responseLine.indexOf("Enter Name") != -1 || responseLine.indexOf("Enter Access Code") != -1) {
					System.out.print(responseLine);
				} else if (responseLine.indexOf("*** Bye") != -1) {
					break;
				}

				else {
					System.out.println(responseLine);
					System.out.println(" ");
				}
			}

			closed = true;
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
	}
}
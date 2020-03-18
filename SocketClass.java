//Devon Kooker

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClass {

	PrintWriter out;
	BufferedReader in = null;
	Socket s = null;
	

	protected void sendData(String data) {
		try {
			out = new PrintWriter(s.getOutputStream());
			out.write(data.toString());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getData() {
		try {
			while (s.isConnected()) {
				in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
				String msg = in.readLine();
				return msg;
			}
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "NULL";
	}

}

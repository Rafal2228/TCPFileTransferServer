package skj.raf.server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

	private final static String PRE_CONSOLE = "ClientHandler: ";
	
	private BufferedReader _reader;
	private PrintWriter _writer;
	private Socket _client;
	private boolean _running = true;
	
	public ClientHandler(Socket client) {
		try {	
			_client = client;
			_reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			_writer = new PrintWriter(client.getOutputStream(), true);
			System.out.println(PRE_CONSOLE + "Created new connection handler");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void close() {
		try {
			_reader.close();
			_writer.close();
			_client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		String tmp;
		
		while(_running) {
			try {
				System.out.println(PRE_CONSOLE + "Listening for input");
				tmp = _reader.readLine();
				if(tmp != null) {
					if(tmp.equals("close")) _running = false;
					System.out.println(PRE_CONSOLE + tmp);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		close();
	}

}

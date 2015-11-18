package skj.raf.server.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

	private final static String PRE_CONSOLE = "ClientHandler: ";
	
	private DownloadHandler _downloader;
	private PrintWriter _writer;
	private Socket _client;
	private boolean _running = true;
	
	public ClientHandler(Socket client) {
		try {	
			_client = client;
			_downloader = new DownloadHandler(client.getInputStream());
			_writer = new PrintWriter(client.getOutputStream(), true);
			System.out.println(PRE_CONSOLE + "Created new connection handler");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			_downloader.close();
			_writer.close();
			_client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(_running) {
			System.out.println(PRE_CONSOLE + "Listening for input");
			_running = _downloader.execute();
		}
		close();
	}

}

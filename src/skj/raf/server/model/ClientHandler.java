package skj.raf.server.model;

import java.io.IOException;
import java.net.Socket;

import skj.raf.server.controller.ConnectionCtrl;

public class ClientHandler implements Runnable {

	private final static String PRE_CONSOLE = "ClientHandler: ";
	
	private DownloadHandler _downloader;
	private Socket _client;
	private boolean _closed = false;
	private boolean _running = true;
	
	public ClientHandler(Socket client) {
		try {
			_client = client;
			_downloader = new DownloadHandler(client.getInputStream(), client.getOutputStream());
			System.out.println(PRE_CONSOLE + "Created new connection handler");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		_running = false;
		_closed = true;
		_downloader.close();
		ConnectionCtrl.clientDisconnect(this);
	}
	
	@Override
	public void run() {
		System.out.println(PRE_CONSOLE + "Listening for input");
		while(_running && !_closed) {
			_running = _downloader.execute();
			if(_client.isClosed()) {
				ConnectionCtrl.clientDisconnect(this);
			}
		}
		System.out.println(PRE_CONSOLE + "Becoming deaf");
		
		try {
			if(!_closed){
				close();
				_client.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

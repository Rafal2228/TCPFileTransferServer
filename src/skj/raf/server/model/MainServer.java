package skj.raf.server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import skj.raf.server.controller.RenderCtrl;
import skj.raf.server.controller.RenderCtrl.Status;

public class MainServer {
	
	private final static String PRE_CONSOLE = "MainServer: ";
	
	private int _port;
	private String _ip;
	private ServerSocket _server = null;
	private boolean _started = false;
	private List<ClientHandler> _allSessions;
	private List<String> _whitelist;
	
	public MainServer(int port) {
		_port = port;
		_allSessions = new ArrayList<>();
		_whitelist = new ArrayList<>();
		RenderCtrl.updatePort(_port);
		RenderCtrl.updateSessions(_allSessions.size());
		RenderCtrl.updateStatus(Status.STOPPED);
		RenderCtrl.updateWhitelist(_whitelist.size() + "");
	}
	
	public MainServer(int port, List<String> whitelist){
		this(port);
		_whitelist.addAll(whitelist);
		RenderCtrl.updateWhitelist(_whitelist.size() + "");
	}
	
	private void createSession(Socket client) {
		System.out.println(PRE_CONSOLE + "New client connected from: " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
		ClientHandler session = new ClientHandler(client);
		_allSessions.add(session);
		RenderCtrl.updateSessions(_allSessions.size());
		new Thread(session).start();
	}
	
	public void start() {
		if(!_started){
			try {
				_server = new ServerSocket(_port);
				_ip = _server.getInetAddress().getCanonicalHostName();
				RenderCtrl.updateIP(_ip);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println(PRE_CONSOLE + "Starting main server");
			_started = true;
			RenderCtrl.updateStatus(Status.ACTIVE);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while(_started) {
							System.out.println(PRE_CONSOLE + "waiting for client at: " + _ip + ":" + _port);
							Socket client = _server.accept();
							if(_whitelist.size() > 0) {
								boolean test = false;
								for(String regexp : _whitelist) {
									if(client.getInetAddress().getHostAddress().matches(regexp)) test = true;
								}
								if(test) {
									createSession(client);
								} else {
									System.out.println(PRE_CONSOLE + "Rejected: " + client.getInetAddress().getHostAddress());
									client.close();
								}
							} else {
								createSession(client);
							}
						}
					} catch (IOException e) {
						
					}
				}
			}).start();
		} else {
			System.out.println(PRE_CONSOLE + "Error server is already running!");
		}
	}
	
	public void stop() {
		if(_started) {
			_started = false;
			RenderCtrl.updateStatus(Status.STOPPED);
			for(ClientHandler session : _allSessions) {
				session.close();
			}
			_allSessions.removeAll(_allSessions);
			RenderCtrl.updateSessions(_allSessions.size());
			if(_server != null)
				try {
					_server.close();
					System.out.println(PRE_CONSOLE + "Stopping main server");
				} catch (IOException e) {
					e.printStackTrace();
				}
		} else {
			System.out.println(PRE_CONSOLE + "Error server is not running!");
		}
	}

	public boolean isRunning() {
		return _started;
	}
	
	public int getPort() {
		return _port;
	}
	
	public String getIP() {
		return _ip;
	}
	
	public void addToWhitelist(String regexp) {
		if(!isRunning()) {
			_whitelist.add(regexp);
			RenderCtrl.updateWhitelist(_whitelist.size() + "");
		}
	}
}

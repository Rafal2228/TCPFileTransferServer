package skj.raf.server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import skj.raf.server.controller.RenderCtrl;
import skj.raf.server.controller.RenderCtrl.Status;

public class MainServer {
	
	private final static String PRE_CONSOLE = "MainServer: ";
	
	private int _port;
	private String _ip;
	private ServerSocket _server;
	private boolean _started = false;
	private ArrayList<ClientHandler> _allSessions;
	
	public MainServer(int port) {
		try {
			_port = port;
			_server = new ServerSocket(_port);
			_ip = _server.getInetAddress().getCanonicalHostName();
			_allSessions = new ArrayList<>();
			RenderCtrl.updatePort(_port);
			RenderCtrl.updateSessions(_allSessions.size());
			RenderCtrl.updateStatus(Status.STOPPED);
			RenderCtrl.updateIP(_ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		if(!_started){
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
							ClientHandler session = new ClientHandler(client);
							_allSessions.add(session);
							RenderCtrl.updateSessions(_allSessions.size());
							new Thread(session).start();
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
				_allSessions.remove(session);
			}
			RenderCtrl.updateSessions(_allSessions.size());
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
}

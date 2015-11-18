package skj.raf.server.controller;

import java.util.Date;

import skj.raf.server.model.MainServer;

public class ConnectionCtrl {
	
	private static final String PRE_CONSOLE = "ConnectionCtrl: ";
	private static MainServer _mainServer;
	
	public static void createServer(int port) {
		System.out.println(PRE_CONSOLE + "Main server created at: " + new Date().toString());
		_mainServer = new MainServer(port);
	}
	
	public static void startServer() {
		_mainServer.start();
	}
	
	public static void stopServer() {
		_mainServer.stop();
	}
	
}

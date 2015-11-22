package skj.raf.server.controller;

import java.util.Date;
import java.util.List;

import skj.raf.server.model.ClientHandler;
import skj.raf.server.model.DownloadHandler;
import skj.raf.server.model.MainServer;

public class ConnectionCtrl {
	
	private static final String PRE_CONSOLE = "ConnectionCtrl: ";
	private static MainServer _mainServer;
	
	public static void createServer(int port) {
		createServer(port, null);
	}
	
	public static void createServer(int port, String folder) {
		DownloadHandler.changeRootDirectory(folder); 
		System.out.println(PRE_CONSOLE + "Main server created at: " + new Date().toString());
		_mainServer = new MainServer(port);
	}
	
	public static void createServer(int port, String folder, List<String> whitelist) {
		DownloadHandler.changeRootDirectory(folder); 
		System.out.println(PRE_CONSOLE + "Main server created at: " + new Date().toString());
		_mainServer = new MainServer(port, whitelist);
	}
	
	public static void startServer() {
		_mainServer.start();
	}
	
	public static void stopServer() {
		_mainServer.stop();
	}
	
	public static void changeRootDirectory(String folder) {
		if(!_mainServer.isRunning()){
			DownloadHandler.changeRootDirectory(folder);
		}
	}
	
	public static void clientDisconnect(ClientHandler client) {
		_mainServer.removeSession(client);
	}
}

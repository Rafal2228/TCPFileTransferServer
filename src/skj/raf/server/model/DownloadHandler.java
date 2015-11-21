package skj.raf.server.model;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import skj.raf.server.controller.RenderCtrl;

public class DownloadHandler {
	
	private static final String PRE_CONSOLE = "DownloadHandler: ";
	private static String _rootFolder = "";
	
	private InputStream _client;
	private PrintWriter _printer;
	private BufferedReader _buffered;
	private boolean _running = true;	
	
	public DownloadHandler(InputStream client, OutputStream clientOut) {
		_client = client;
		_buffered = new BufferedReader(new InputStreamReader(_client));
		_printer = new PrintWriter(clientOut);
	}
	
	public static void changeRootDirectory(String folder) {
		if(folder != null){
			File dir = new File(folder);
			if(dir.isDirectory() && dir.canWrite()) {
				if(!(folder.charAt(folder.length() - 1) == '/')) folder += "/";
				DownloadHandler._rootFolder = folder;
				RenderCtrl.updateRootFolder(folder);
			} else {
				System.out.println(PRE_CONSOLE + "Cannot use specified path");
			}
		}
		
		if(_rootFolder.equals("")) {
			System.out.println(PRE_CONSOLE + "Folder not specified, using wd");
			DownloadHandler._rootFolder = System.getProperty("user.dir");
			RenderCtrl.updateRootFolder(DownloadHandler._rootFolder);
		} 
	}
	
	public boolean execute() {
		if(_running){
			try {
				String command = _buffered.readLine();
				
				if(command != null){
					switch(command) {
						case "close": {
							close();
						} break;
						
						case "file": {
							int fileSize = Integer.parseInt(_buffered.readLine()); // reads file size
							String filePath = _buffered.readLine(); // reads file path and name
							long checkSum = Long.parseLong(_buffered.readLine()); // reads checksum
							
							File file = new File(_rootFolder + filePath);
							if(!file.exists()) {
								_printer.println("OK");
								
								byte[] buffer = new byte[fileSize];
								int downloaded = _client.read(buffer, 0, buffer.length);
								int total = downloaded;
														
								do {
									downloaded = _client.read(buffer, total, buffer.length - total);
									if(downloaded >= 0) total += downloaded;
							    } while(total < fileSize);
								
								file.createNewFile();
								Checksum recivedSum = new CRC32();
								recivedSum.update(buffer, 0, buffer.length);
								long recivedCheckSum = recivedSum.getValue();
								
								if(checkSum == recivedCheckSum) {
									BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(file));
									fileOutput.write(buffer, 0, total);
									fileOutput.flush();
									fileOutput.close();
								} else {
									System.out.println(PRE_CONSOLE + "Checksum error : " + checkSum + " : " + recivedCheckSum);
								}
							} else {
								_printer.println("EXIST");
								System.out.println(PRE_CONSOLE + "File already exists!");
							}
							
							
						} break;
						
						case "dir": {							
							String folderPath = _buffered.readLine();
							System.out.println(PRE_CONSOLE + "Creating: " + folderPath);
							File dir = new File(_rootFolder + folderPath);
							if(dir.exists()) {
								if(!dir.isDirectory()) break;
							} else {
								dir.mkdir();
							}
						} break;
						
						default : {
							System.out.println(PRE_CONSOLE + "Invalid command:" + command);
						} break;
					}
				}
			} catch (Exception e) {
				close();
			};
		}
		
		return _running;
	}
	
	public void close() {
		_running = false;
	}
}

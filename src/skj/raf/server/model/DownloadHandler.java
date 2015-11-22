package skj.raf.server.model;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import skj.raf.server.controller.RenderCtrl;
import skj.raf.server.controller.RenderCtrl.Status;

public class DownloadHandler {
	
	private static final String PRE_CONSOLE = "DownloadHandler: ";
	private static final int PACKAGE_SIZE = 4096;
	private static String _rootFolder = "";
	
	private int _totalFiles;
	private int _totalFolders;
	private int _totalErrors;
	
	private InputStream _client;
	private OutputStream _clientOut;
	private PrintWriter _printer;
	private BufferedReader _buffered;
	private boolean _running = true;	
	
	public DownloadHandler(InputStream client, OutputStream clientOut) {
		_client = client;
		_buffered = new BufferedReader(new InputStreamReader(_client));
		_printer = new PrintWriter(clientOut, true);
		_clientOut = clientOut;
		
		_totalFiles = _totalErrors = _totalFolders = 0;
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
				
				if(command != null)
				switch(command) {
					case "close": {
						System.out.println(PRE_CONSOLE + "Host disconnected");
						close();
					} break;
					
					case "file": {
						RenderCtrl.updateStatus(Status.DOWNLOADING);
						float fileSize = Float.parseFloat(_buffered.readLine()); // reads file size
						String filePath = _buffered.readLine(); // reads file path and name
						
						File file = new File(_rootFolder + filePath);
						if(!file.exists()) {
							_printer.println("OK");
							
							byte[] buffer = new byte[PACKAGE_SIZE];
							int downloaded = 0;
							float total = downloaded;
//							int percent = 0;
							Checksum recivedSum = new CRC32();
							
							file.createNewFile();
							BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(file));
							
							do {
								downloaded = _client.read(buffer, 0, PACKAGE_SIZE);
								
								if(downloaded >= 0) {
									recivedSum.update(buffer, 0, downloaded);
									fileOutput.write(buffer, 0, downloaded);
									fileOutput.flush();
									total += downloaded;
								}
								
//								percent = (int)((total/fileSize)* 100);
//								System.out.println(PRE_CONSOLE + "Progress of " + file.getName() + ": " + percent + "%");
						    } while(total < fileSize);
							
							_printer.println("RECIVED");
							
							long recivedCheckSum = recivedSum.getValue();
							long checkSum = Long.parseLong(_buffered.readLine()); // reads checksum
							
							if(checkSum == recivedCheckSum) {
								System.out.println(PRE_CONSOLE + "Checksum ok : " + filePath);
								_totalFiles++;
								_printer.println("CHECKSUM_OK");
								_printer.flush();
							} else {
								System.out.println(PRE_CONSOLE + "Checksum error : " + checkSum + " : " + recivedCheckSum);
								_totalErrors++;
								_printer.println("CHECKSUM_ERROR");
								_printer.flush();
							}
							fileOutput.close();
						} else {
							_printer.println("EXIST");
							System.out.println(PRE_CONSOLE + "File already exists!");
							_totalErrors++;
						}
						RenderCtrl.updateStatus(Status.ACTIVE);
					} break;
					
					case "dir": {							
						String folderPath = _buffered.readLine();
						System.out.println(PRE_CONSOLE + "Creating: " + folderPath);
						File dir = new File(_rootFolder + folderPath);
						if(dir.exists()) {
							if(!dir.isDirectory()) break;
							_totalErrors++;
						} else {
							dir.mkdir();
							_totalFolders++;
						}
					} break;
					
					default : {
						System.out.println(PRE_CONSOLE + "Invalid command:" + command);
					} break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			};
		}
		
		if(!_running) {
			try {
				_client.close();
				_clientOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		updateView();
		
		return _running;
	}
	
	private void updateView() {
		RenderCtrl.updateTotal(_totalFolders, _totalFiles, _totalErrors);
	}
	
	public void close() {
		_running = false;
	}
}

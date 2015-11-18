package skj.raf.server.model;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DownloadHandler {
	
	private static final String PRE_CONSOLE = "DownloadHandler: ";
	
	private InputStream _client;
	private BufferedReader _buffered;
	
	public DownloadHandler(InputStream client) {
		_client = client;
		_buffered = new BufferedReader(new InputStreamReader(_client));
	}
	
	public boolean execute() {
		boolean running = true;

		try {
			String command = _buffered.readLine();
			
			if(command != null){
				switch(command) {
					case "close": {
						running = false;
						close();
					} break;
					
					case "file": {
						int fileSize = Integer.parseInt(_buffered.readLine()); // reads file size
						String filePath = _buffered.readLine(); // reads file path and name
						
						byte[] buffer = new byte[fileSize];
						int downloaded = _client.read(buffer, 0, buffer.length);
						int total = downloaded;
												
						do {
							downloaded = _client.read(buffer, total, buffer.length - total);
							if(downloaded >= 0) total += downloaded;
					    } while(total < fileSize);
												
						File file = new File(filePath);
						if(!file.exists()) {
							file.createNewFile();
						}
						BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(file));
						fileOutput.write(buffer, 0, total);
						fileOutput.flush();
						fileOutput.close();
					} break;
					
					default : {
						System.out.println(PRE_CONSOLE + "Invalid command");
					} break;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		};
		
		
		return running;
	}
	
	public void close() {
		try {
			_buffered.close();
			_client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

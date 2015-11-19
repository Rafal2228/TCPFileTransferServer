package skj.raf.server.controller;

import java.util.ArrayList;
import java.util.List;

public class MainCtrl {

	public static void main(String[] args) {
		
		int port = 9999;
		String folder = null;
		List<String> whitelist = new ArrayList<>();
		
		if(args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		
		if(args.length > 1) {
			folder = args[1];
		}
		
		if(args.length > 2) {
			for(int i = 2; i < args.length ; i++) {
				String pattern = args[i];
				pattern.replaceAll(".", "\\.");
				pattern.replaceAll("\\?", ".");
				pattern.replaceAll("\\*", ".*");
				whitelist.add(pattern);
			}
		}
		
		RenderCtrl.drawMain();
		ConnectionCtrl.createServer(port, folder, whitelist);
	}

}

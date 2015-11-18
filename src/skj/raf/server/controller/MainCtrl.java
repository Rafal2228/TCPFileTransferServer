package skj.raf.server.controller;

public class MainCtrl {

	public static void main(String[] args) {
		
		int port = 9999;
		
		if(args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		
		RenderCtrl.drawMain();
		ConnectionCtrl.createServer(port);
	}

}

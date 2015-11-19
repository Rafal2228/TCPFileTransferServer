package skj.raf.server.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import skj.raf.server.controller.ConnectionCtrl;
import skj.raf.server.controller.RenderCtrl;

public class MainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private JLabel _ip;
	private JLabel _port;
	private JLabel _status;
	private JLabel _sessions;
	private JLabel _folder;
	private JLabel _whitelist;
	
	public MainFrame(){
		super("Server v.0.1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(50,50);
		
		setLayout(new BorderLayout());
		
		// Creates menu
		
		JPanel menuPanel = new JPanel();
		menuPanel.setPreferredSize(new Dimension(150, 300));
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

		menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		JButton start = new JButton("Start server");
		start.setAlignmentX(CENTER_ALIGNMENT);
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectionCtrl.startServer();
			}
		});
		menuPanel.add(start);
		
		menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		JButton stop = new JButton("Stop server");
		stop.setAlignmentX(CENTER_ALIGNMENT);
		stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectionCtrl.stopServer();
			}
		});
		menuPanel.add(stop);
		
		menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JButton folder = new JButton("Change folder");
		folder.setAlignmentX(CENTER_ALIGNMENT);
		folder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int state = chooser.showOpenDialog(null);
				
				if(state == JFileChooser.APPROVE_OPTION) {
					ConnectionCtrl.changeRootDirectory(chooser.getSelectedFile().getPath());
				}
			}
		});
		menuPanel.add(folder);
		
		menuPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		
		JButton end = new JButton("End");
		end.setAlignmentX(CENTER_ALIGNMENT);
		end.setAlignmentY(BOTTOM_ALIGNMENT);
		end.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				RenderCtrl.end();
			}
		});
		menuPanel.add(end);
		
		add(menuPanel, BorderLayout.WEST);
		
		// Creates body
		
		JPanel bodyPanel = new JPanel();
		bodyPanel.setPreferredSize(new Dimension(300, 300));
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));

		bodyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		_ip = new JLabel("IP: ");
		_ip.setAlignmentX(LEFT_ALIGNMENT);
		bodyPanel.add(_ip);
		bodyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		_port = new JLabel("PORT: ");
		_port.setAlignmentX(LEFT_ALIGNMENT);
		bodyPanel.add(_port);
		bodyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		_status = new JLabel("Status: ");
		_status.setAlignmentX(LEFT_ALIGNMENT);
		bodyPanel.add(_status);
		bodyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		_folder = new JLabel("Folder: ");
		_folder.setAlignmentX(LEFT_ALIGNMENT);
		bodyPanel.add(_folder);
		bodyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		_sessions = new JLabel("Sessions: ");
		_sessions.setAlignmentX(LEFT_ALIGNMENT);
		bodyPanel.add(_sessions);
		bodyPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		_whitelist = new JLabel("Whitelist: ");
		_whitelist.setAlignmentX(LEFT_ALIGNMENT);
		bodyPanel.add(_whitelist);
		bodyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		add(bodyPanel, BorderLayout.CENTER);
	}

	public void updateIP(String ip) {
		_ip.setText("IP: " + ip);
	}
	
	public void updatePort(String port) {
		_port.setText("PORT: " + port);
	}
	
	public void updateStatus(String status) {
		_status.setText("Status: " + status);
	}
	
	public void updateSessions(String sessions) {
		_sessions.setText("Sessions: " + sessions);
	}
	
	public void updateFolder(String folder) {
		_folder.setText("Folder: " + folder);
	}
	
	public void updateWhitelist(String whitelist) {
		_whitelist.setText("Whitelist: " + whitelist);
	}
	
	public void bootstrap() {
		pack();
		setVisible(true);
	}
	
	public void close() {
		setVisible(false);
		dispose();
	}
}
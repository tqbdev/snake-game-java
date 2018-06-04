package com.tqbdev.gui.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.tqbdev.client_core.ClientThread;
import com.tqbdev.client_core.ConnectListener;
import com.tqbdev.client_core.DoneListener;
import com.tqbdev.gui.components.Dialog;

public class ConnectedScreen extends JFrame implements ActionListener, ConnectListener, DoneListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2875568510482210508L;

	private JPanel contentPane;
	
	private JFrame previousJFrame;
	private ClientThread clientThread;

	/**
	 * Create the frame.
	 */
	public ConnectedScreen(JFrame previousJFrame, ClientThread clientThread) {
		this.previousJFrame = previousJFrame;
		this.clientThread = clientThread;
		this.clientThread.addConnectListener(this);
		this.clientThread.addDoneListener(this);
		
		setTitle("Registration...");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		contentPane.add(initButtons());
		
		setContentPane(contentPane);
	}
	
	private JPanel initButtons() {
		JPanel jPanel = new JPanel();
		jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
		jPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));
		
		JButton createRoomBtn = new JButton("Create Room");
		createRoomBtn.addActionListener(this);
		createRoomBtn.setActionCommand("CREATE");
		createRoomBtn.setAlignmentX(CENTER_ALIGNMENT);
		jPanel.add(createRoomBtn);
		
		JButton joinRoomBtn = new JButton("Join Room");
		joinRoomBtn.addActionListener(this);
		joinRoomBtn.setActionCommand("JOIN");
		joinRoomBtn.setAlignmentX(CENTER_ALIGNMENT);
		jPanel.add(joinRoomBtn);
		
		JButton quitBtn = new JButton("Disconnect");
		quitBtn.addActionListener(this);
		quitBtn.setActionCommand("QUIT");
		quitBtn.setAlignmentX(CENTER_ALIGNMENT);
		jPanel.add(quitBtn);
		
		return jPanel;
	}
	
	public JFrame getPreviousJFrame() {
		return previousJFrame;
	}

	public void setPreviousJFrame(JFrame previousJFrame) {
		this.previousJFrame = previousJFrame;
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		String actionCommand = arg.getActionCommand();

		if (actionCommand.equals("CREATE")) {
			clientThread.createRoom();
		} else if (actionCommand.equals("JOIN")) {
			JPanel jPanel = new JPanel();
			jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
			
			jPanel.add(new JLabel("Enter Room Code: "));
			JTextField roomCodeField = new JTextField();
			jPanel.add(roomCodeField);
			
			int selection = JOptionPane.showConfirmDialog(this, jPanel, "Room code", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			
			if (selection == JOptionPane.OK_OPTION)
		    {
		        String roomCode = roomCodeField.getText();
		        clientThread.joinRoom(roomCode);
		    }
		} else if (actionCommand.equals("QUIT")) {
			clientThread.quit();
			this.setVisible(false);
			previousJFrame.setVisible(true);
			this.dispose();
		}
	}

	@Override
	public void CreateRoomRespone(String respone, boolean isOK) {
		if (isOK) {
			Dialog.showInform(this, "Create Room Successfully." + "\r\nRoom code: " + respone, "Notice...");
			this.setVisible(false);
			this.clientThread.removeDoneListener(this);
			RoomScreen roomScreen = new RoomScreen(this, clientThread, respone);
			roomScreen.setVisible(true);
		} else {
			Dialog.showErrorMessage(this, "Create Room Failure." + "\r\nError: " + respone, "Error...");
		}		
	}

	@Override
	public void JoinRoomRespone(String respone, boolean isOK) {
		if (isOK) {
			Dialog.showInform(this, "Join Room Successfully.", "Notice...");
			this.setVisible(false);
			this.clientThread.removeDoneListener(this);
			RoomScreen roomScreen = new RoomScreen(this, clientThread, respone);
			roomScreen.setVisible(true);
		} else {
			Dialog.showErrorMessage(this, "Join Room Failure." + "\r\nError: " + respone, "Error...");
		}	
	}

	@Override
	public void ConnectionDone(ClientThread clientThread) {
		Dialog.showErrorMessage(this, "Server is disconnect", "Error...");
		
		this.setVisible(false);
		previousJFrame.setVisible(true);
		this.dispose();		
	}

}

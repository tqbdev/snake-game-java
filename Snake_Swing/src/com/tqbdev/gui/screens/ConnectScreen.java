package com.tqbdev.gui.screens;


import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.tqbdev.client_core.ClientThread;
import com.tqbdev.gui.components.Dialog;

public class ConnectScreen extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2436712845599945651L;
	
	private JTextField ipAddressField;
	private JTextField portNumberField;
	
	private static String PORT = "5000";
	private static String IP = "127.0.0.1";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception weTried) {
				}

				try {
					ConnectScreen frame = new ConnectScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ConnectScreen() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 310, 200);
		setResizable(false);
		setTitle("Connect to game server...");
		setLocationRelativeTo(null); // CENTER of Screen

		// Components submit form username and password
		GridBagConstraints c = new GridBagConstraints();

		ipAddressField = new JTextField(5);
		ipAddressField.setText(IP);
		portNumberField = new JTextField(5);
		portNumberField.setText(PORT);

		JButton connectBtn = new JButton("Connect");
		connectBtn.setActionCommand("Connect");
		connectBtn.addActionListener(this);
		this.getRootPane().setDefaultButton(connectBtn);
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setActionCommand("Cancel");
		cancelBtn.addActionListener(this);

		JPanel myPanel = new JPanel();
		myPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		myPanel.setLayout(new GridBagLayout());

		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 10, 5, 10); // top, right padding
		c.weightx = 0.0;
		c.gridwidth = 2;

		c.gridx = 0;
		c.gridy = 0;
		myPanel.add(new JLabel("IP Address:"), c);

		c.gridx = 0;
		c.gridy = 1;
		myPanel.add(ipAddressField, c);

		c.gridx = 0;
		c.gridy = 2;
		myPanel.add(new JLabel("Port:"), c);

		c.gridx = 0;
		c.gridy = 3;
		myPanel.add(portNumberField, c);

		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 4;
		myPanel.add(connectBtn, c);

		c.gridx = 1;
		c.gridy = 4;
		myPanel.add(cancelBtn, c);

		setContentPane(myPanel);
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		String actionCommand = arg.getActionCommand();

		if (actionCommand.equals("Connect")) {
			String ipAddress = ipAddressField.getText();
			String portNumber = portNumberField.getText();
			
			if (ipAddress == null || ipAddress.length() == 0 ||
					portNumber == null || portNumber.length() == 0) {
				Dialog.showWarning(this, "Please enter ip address and port field", "Warning...");
				return;
			}
			
			Socket socket = null;
			try {
				socket = new Socket(ipAddress, Integer.parseInt(portNumber));
			} catch (NumberFormatException e) {
				Dialog.showWarning(this, "Port number is invalid.\r\nPlease try again.", "Warning...");
				portNumberField.setText(PORT);
				socket = null;
			} catch (UnknownHostException e) {
				Dialog.showWarning(this, "IP address error.\r\nPlease try again.", "Warning...");
				ipAddressField.setText(ipAddress);
				socket = null;
			} catch (IOException e) {
				Dialog.showWarning(this, "Connect error.\r\nPlease try again.", "Warning...");
				socket = null;
			}
			
			if (socket != null) { // Switching screen
				this.setVisible(false);
				ClientThread clientThread = new ClientThread(socket);
				clientThread.start();
				ConnectedScreen connectedScreen = new ConnectedScreen(this, clientThread);
				connectedScreen.setVisible(true);
			}
		} else if (actionCommand.equals("Cancel")) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

}

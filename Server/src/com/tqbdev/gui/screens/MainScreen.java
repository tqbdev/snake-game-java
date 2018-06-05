package com.tqbdev.gui.screens;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.tqbdev.server_core.Server;

public class MainScreen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4543157284590095403L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainScreen frame = new MainScreen();
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
	public MainScreen() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel label = new JLabel("Server is listening port 5000");
		label.setFont(new Font("TimesRoman", Font.PLAIN, 32));
		contentPane.add(label);
		
		setContentPane(contentPane);
		pack();		
		
		Server server = new Server();
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					server.run();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		t.start();
	}

}

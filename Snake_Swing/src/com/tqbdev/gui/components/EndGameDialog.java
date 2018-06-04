package com.tqbdev.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class EndGameDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6618618174189418723L;

	public EndGameDialog(JFrame parent, String message, int point) {
		super(parent, false);
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("End game...");
		
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JLabel messLable = new JLabel(message);
		messLable.setFont(new Font("TimesRoman", Font.PLAIN, 30));
		messLable.setForeground(Color.BLACK);
		contentPanel.add(messLable);
		
		JLabel pointLable = new JLabel("Your point is: " + point);
		pointLable.setFont(new Font("TimesRoman", Font.BOLD, 30));
		pointLable.setForeground(Color.BLUE);
		contentPanel.add(pointLable);
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		setVisible(true);
		pack();
	}
}

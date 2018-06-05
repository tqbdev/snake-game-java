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

import com.tqbdev.client_core.CountDownListener;

public class WaitDialog extends JDialog implements CountDownListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7687195061491166168L;
	
	JLabel messLable;

	/**
	 * Create the dialog.
	 */
	public WaitDialog(JFrame parent) {
		super(parent, false);
		setBounds(100, 100, 200, 100);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle("Notice...");
		
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		messLable = new JLabel("Game start in 10");
		messLable.setFont(new Font("TimesRoman", Font.PLAIN, 30));
		messLable.setForeground(Color.BLUE);
		contentPanel.add(messLable);
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		setVisible(true);
		setResizable(false);
		pack();
	}

	@Override
	public void updateValue(int value) {
		messLable.setText("Game start in " + value);
		pack();
	}

}

package com.tqbdev.gui.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;

import com.tqbdev.client_core.ClientThread;
import com.tqbdev.client_core.GameListener;
import com.tqbdev.client_core.RoomListener;
import com.tqbdev.gui.components.Dialog;
import com.tqbdev.gui.components.GameCanvas;
import com.tqbdev.snake_core.Direction;
import com.tqbdev.snake_core.StateCell;

public class RoomScreen extends JFrame implements ActionListener, KeyListener, RoomListener, GameListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7489044168436813293L;
	private JPanel contentPane;
	private GameCanvas gameCanvas;

	private JFrame previousJFrame;
	private ClientThread clientThread;

	private JLabel amountInRoomLabel;
	private JButton newGameBtn;
	private JButton stopGameBtn;

	private String roomCode;

	private StateCell[][] boardGame;

	/**
	 * Create the frame.
	 */
	public RoomScreen(JFrame previousJFrame, ClientThread clientThread, String roomCode) {
		this.previousJFrame = previousJFrame;
		this.clientThread = clientThread;
		this.roomCode = roomCode;

		this.clientThread.addRoomListener(this);
		this.clientThread.addGameListener(this);

		setTitle("In room: " + this.roomCode);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 650);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		contentPane.add(initButtons());

		initBoardGame();
		gameCanvas = new GameCanvas(boardGame);
		gameCanvas.setBorder(BorderFactory.createLineBorder(Color.black));
		gameCanvas.setDoubleBuffered(true);
		gameCanvas.setMinimumSize(new Dimension(600, 600));
		gameCanvas.setMaximumSize(new Dimension(600, 600));
		gameCanvas.setBackground(Color.DARK_GRAY);
		contentPane.add(gameCanvas);
		
		setFocusable(true);
		addKeyListener(this);
		requestFocus();

		setContentPane(contentPane);
	}

	private void initBoardGame() {
		boardGame = new StateCell[60][60];

		for (int i = 0; i < 60; i++) {
			for (int j = 0; j < 60; j++) {
				boardGame[i][j] = StateCell.EMPTY;
			}
		}
	}

	private JPanel initButtons() {
		JPanel jPanel = new JPanel();
		jPanel.setBorder(new EmptyBorder(0, 10, 0, 30));
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

		amountInRoomLabel = new JLabel("Now room have 1/4 player.");
		amountInRoomLabel.setFont(new Font("TimesRoman", Font.PLAIN, 16));
		amountInRoomLabel.setForeground(Color.BLUE);
		jPanel.add(amountInRoomLabel);

		jPanel.add(new JLabel("Auto update 3s"));

		jPanel.add(Box.createVerticalGlue());

		Font btnFont = new Font("TimesRoman", Font.PLAIN, 14);
		newGameBtn = new JButton("New game");
		newGameBtn.setEnabled(clientThread.isHost());
		newGameBtn.setFont(btnFont);
		newGameBtn.addActionListener(this);
		newGameBtn.setActionCommand("START");
		jPanel.add(newGameBtn);
		jPanel.add(Box.createVerticalGlue());

		stopGameBtn = new JButton("Stop game");
		stopGameBtn.setEnabled(clientThread.isHost());
		stopGameBtn.setFont(btnFont);
		stopGameBtn.addActionListener(this);
		stopGameBtn.setActionCommand("STOP");
		jPanel.add(stopGameBtn);
		jPanel.add(Box.createVerticalGlue());

		JButton leaveRoomBtn = new JButton("Leave room");
		leaveRoomBtn.setFont(btnFont);
		leaveRoomBtn.addActionListener(this);
		leaveRoomBtn.setActionCommand("LEAVE");
		jPanel.add(leaveRoomBtn);
		jPanel.add(Box.createVerticalGlue());

		return jPanel;
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		String actionCommand = arg.getActionCommand();

		if (actionCommand.equals("START")) {
			clientThread.startGame();
		} else if (actionCommand.equals("STOP")) {
			clientThread.stopGame();
		} else if (actionCommand.equals("LEAVE")) {
			clientThread.leaveRoom();
			this.setVisible(false);
			this.dispose();
			previousJFrame.setVisible(true);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("Key");
		
		switch (e.getKeyCode()) {
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			clientThread.changeDirection(Direction.DOWN);
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			clientThread.changeDirection(Direction.UP);
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			clientThread.changeDirection(Direction.LEFT);
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			clientThread.changeDirection(Direction.RIGHT);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void changeHost() {
		clientThread.setHost(true);
		newGameBtn.setEnabled(clientThread.isHost());
		stopGameBtn.setEnabled(clientThread.isHost());

		Dialog.showInform(this, "Now, you is host of this room", "Notice...");
	}

	@Override
	public void updateRoom(int numberOfPlayers) {
		if (amountInRoomLabel != null) {
			amountInRoomLabel.setText("Now room have " + numberOfPlayers + "/4 player.");
		}
	}

	@Override
	public void updateBoard(String boardStr) {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				char[] tmp = boardStr.toCharArray();

				for (int i = 0; i < tmp.length; i++) {
					
					StateCell cell = StateCell.values()[tmp[i] - '0'];
					
					int t = i % 60;
					int k = i / 60;
					boardGame[k][t] = cell;
				}
				gameCanvas.repaint();				
			}
		});
		t.start();
	}

	@Override
	public void beginGame() {
		addKeyListener(this);
		requestFocus();
		// TODO show text in board game ready to start
	}

	@Override
	public void endGame(String response) {
		removeKeyListener(this);
		// TODO show point
	}
}

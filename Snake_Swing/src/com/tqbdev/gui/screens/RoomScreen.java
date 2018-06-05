package com.tqbdev.gui.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;

import com.tqbdev.client_core.ClientThread;
import com.tqbdev.client_core.DoneListener;
import com.tqbdev.client_core.GameListener;
import com.tqbdev.client_core.RoomListener;
import com.tqbdev.gui.components.Dialog;
import com.tqbdev.gui.components.EndGameDialog;
import com.tqbdev.gui.components.GameCanvas;
import com.tqbdev.gui.components.WaitDialog;
import com.tqbdev.snake_core.Direction;
import com.tqbdev.snake_core.EndGameState;
import com.tqbdev.snake_core.StateCell;

public class RoomScreen extends JFrame implements ActionListener, RoomListener, GameListener, DoneListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7489044168436813293L;
	private JPanel contentPane;
	private GameCanvas gameCanvas;

	private JFrame previousJFrame;
	private ClientThread clientThread;

	private JLabel amountInRoomLabel;
	private JLabel[] pointInses;
	private JLabel[] pointLabels;
	private JButton newGameBtn;
	private JButton stopGameBtn;

	private String roomCode;

	private StateCell[][] boardGame;

	private WaitDialog waitDialog;
	private EndGameDialog endGameDialog;

	private static final String[] snakePlayerStrs = { "Snake 1 (Blue):", "Snake 2 (Green):", "Snake 3 (White):",
			"Snake 4 (Yellow):" };

	/**
	 * Create the frame.
	 */
	public RoomScreen(JFrame previousJFrame, ClientThread clientThread, String roomCode) {
		this.previousJFrame = previousJFrame;
		this.clientThread = clientThread;
		this.roomCode = roomCode;

		this.clientThread.addRoomListener(this);
		this.clientThread.addGameListener(this);
		this.clientThread.addDoneListener(this);

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
		requestFocus();

		InputMap im = gameCanvas.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = gameCanvas.getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "Right");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "Right");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Left");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "Left");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "Up");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "Up");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "Down");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "Down");

		am.put("Right", new ArrowAction(Direction.RIGHT));
		am.put("Left", new ArrowAction(Direction.LEFT));
		am.put("Up", new ArrowAction(Direction.UP));
		am.put("Down", new ArrowAction(Direction.DOWN));

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

		JLabel pointIns = new JLabel("Point of snakes:");
		pointIns.setFont(new Font("TimesRoman", Font.BOLD, 16));
		jPanel.add(pointIns);

		Font fontPlain = new Font("TimesRoman", Font.PLAIN, 14);
		Font fontBold = new Font("TimesRoman", Font.BOLD, 14);
		pointInses = new JLabel[4];
		pointLabels = new JLabel[4];
		for (int i = 0; i < 4; i++) {
			pointIns = new JLabel(snakePlayerStrs[i]);
			pointIns.setFont(fontPlain);
			pointInses[i] = pointIns;

			JLabel point = new JLabel("0");
			point.setFont(fontBold);
			pointLabels[i] = point;

			jPanel.add(pointIns);
			jPanel.add(point);
		}

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
		if (waitDialog == null) {
			String actionCommand = arg.getActionCommand();

			if (actionCommand.equals("START")) {
				clientThread.startGame();
			} else if (actionCommand.equals("STOP")) {
				clientThread.stopGame();
			} else if (actionCommand.equals("LEAVE")) {
				clientThread.leaveRoom();

				this.setVisible(false);
				previousJFrame.setVisible(true);
				ConnectedScreen connectedScreen = (ConnectedScreen) previousJFrame;
				this.clientThread.addDoneListener(connectedScreen);
				this.clientThread.removeDoneListener(this);

				this.dispose();
			}
		}
	}

	@Override
	public synchronized void changeHost() {
		clientThread.setHost(true);
		newGameBtn.setEnabled(clientThread.isHost());
		stopGameBtn.setEnabled(clientThread.isHost());

		Dialog.showInform(this, "Now, you is host of this room", "Notice...");
	}

	@Override
	public synchronized void updateRoom(int numberOfPlayers) {
		if (amountInRoomLabel != null) {
			amountInRoomLabel.setText("Now room have " + numberOfPlayers + "/4 player.");
		}

		for (int i = 0; i < numberOfPlayers; i++) {
			pointInses[i].setVisible(true);
			pointLabels[i].setVisible(true);
		}

		for (int i = numberOfPlayers; i < 4; i++) {
			pointInses[i].setVisible(false);
			pointLabels[i].setVisible(false);
		}
	}

	@Override
	public synchronized void updateBoard(String boardStr) {
		if (waitDialog != null) {
			waitDialog.dispose();
			clientThread.removeCountDownListener(waitDialog);
			waitDialog = null;
		}
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					char[] tmp = boardStr.toCharArray();

					for (int i = 0; i < tmp.length; i++) {

						StateCell cell = StateCell.values()[tmp[i] - '0'];

						int t = i % 60;
						int k = i / 60;
						boardGame[k][t] = cell;
					}
					gameCanvas.repaint();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	@Override
	public synchronized void beginGame() {
		waitDialog = new WaitDialog(this);
		clientThread.addCountDownListener(waitDialog);
	}

	@Override
	public synchronized void endGame(EndGameState endGameState, int point) {
		// System.out.println("TEST");
		if (endGameDialog == null) {
			String message = "";

			switch (endGameState) {
			case FullMap:
				message = "Game end because full board.";
				break;
			case PlayerLeave:
				message = "Game end because one player leave room.";
				break;
			case HostEnd:
				message = "Game end because room's host end game.";
				break;
			case Collision:
				message = "Game end because your snake have a collision.";
				break;
			}

			endGameDialog = new EndGameDialog(this, message, point);
			endGameDialog.addWindowListener(this);
		}
		
		clientThread.sendEndCheck();
	}

	@Override
	public synchronized void updatePoint(String pointStr) {
		int numberOfPlayers = pointStr.length() / 5;

		for (int i = 0; i < numberOfPlayers; i++) {
			char isAlive = pointStr.charAt(0 + i * 5);
			int point = Integer.parseInt(pointStr.substring(1 + i * 5, 5 + i * 5));

			String alive = "";
			if (isAlive == '0') {
				alive = " (is dead)";
			}
			pointLabels[i].setText(point + alive);
			// System.out.println(i + " - " + isAlive + " - " + point);
		}
	}

	public class ArrowAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 633147212795548277L;
		private Direction direction;

		public ArrowAction(Direction direction) {
			this.direction = direction;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			clientThread.changeDirection(direction);
		}
	}

	@Override
	public void ConnectionDone(ClientThread clientThread) {
		Dialog.showErrorMessage(this, "Server is disconnect", "Error...");

		this.setVisible(false);

		ConnectedScreen connectedScreen = (ConnectedScreen) previousJFrame;

		connectedScreen.getPreviousJFrame().setVisible(true);

		connectedScreen.dispose();
		this.dispose();
	}

	@Override
	public void windowActivated(WindowEvent e) {		
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (endGameDialog != null && e.getSource().equals(endGameDialog)) {
			endGameDialog = null;
		}				
		
		if (endGameDialog != null && e.getSource().equals(this)) {
			endGameDialog.dispose();
		}		
		
		if (waitDialog != null && e.getSource().equals(this)) {
			waitDialog.dispose();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}

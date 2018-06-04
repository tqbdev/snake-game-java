package com.tqbdev.game_core;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tqbdev.server_core.ClientThread;
import com.tqbdev.snake_core.Cell;
import com.tqbdev.snake_core.Snake;
import com.tqbdev.snake_core.SnakeListener;
import com.tqbdev.snake_core.Snake_Player;
import com.tqbdev.snake_core.StateCell;

public class Game extends Thread implements SnakeListener {
	private Cell[][] boardGame = null;
	private int height;
	private int width;

	private Snake[] snakes = { null, null, null, null };
	private Snake_Player currentSnakePlayer = Snake_Player.Snake_One;

	private ClientThread[] clientThreads = null;

	private boolean isPlaying;

	private int totalPoint;
	private int maxPoint;

	private boolean endGame = false;

	// Game Listener
	private final Set<GameListener> listeners = new CopyOnWriteArraySet<GameListener>();

	public final void addListener(final GameListener listener) {
		listeners.add(listener);
	}

	public final void removeListener(final GameListener listener) {
		listeners.remove(listener);
	}

	private final void endGame() {
		endGame = true;
		for (GameListener listener : listeners) {
			listener.endGame();
		}
	}
	//

	public Game(int height, int width) {
		this.height = height;
		this.width = width;

		init();
	}

	private void init() {
		isPlaying = false;
		maxPoint = height * width;
		totalPoint = 0;
		this.boardGame = new Cell[height][width];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				Cell cell = boardGame[i][j] = new Cell();
				cell.setRow(i);
				cell.setCol(j);
			}
		}
	}

	public void setPlayerThreads(ClientThread[] clientThreads) {
		this.clientThreads = clientThreads;
	}

	public void newGame() {
		isPlaying = false;
		init();
		initSnake();
		generateFood();
	}

	public void initSnake() {
		for (int i = 0; i < clientThreads.length; i++) {
			ClientThread clientThread = clientThreads[i];

			if (clientThread != null) {
				Snake snake = new Snake(boardGame, currentSnakePlayer);
				currentSnakePlayer = currentSnakePlayer.next();
				snake.addListener(this);
				snakes[i] = snake;

				clientThread.addPlayerListener(snake);
			}
		}
	}

	public void beginGame() {
		isPlaying = true;
	}

	public void hostEndGame() {
		System.out.println("STOP GAME");
		isPlaying = false;
		endGame = true;
		// Send message to client
	}

	public boolean removeSnake(Snake_Player player) {
		return true;
	}

	public void move() {
		for (int i = 0; i < snakes.length; i++) {
			Snake snake = snakes[i];

			if (snake != null) {
				snake.move();
			}
		}
	}

	public Cell[][] getBoardGame() {
		return this.boardGame;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	private void generateFood() {
		Random rand = new Random();
		Cell cell = null;
		do {
			int row = rand.nextInt(height);
			int col = rand.nextInt(width);
			cell = boardGame[row][col];
		} while (cell == null || !cell.getState().equals(StateCell.EMPTY));

		cell.changeState(StateCell.FOOD);
	}

	@Override
	public void eatFood() {
		totalPoint++;
		if (totalPoint >= maxPoint) {
			isPlaying = false;
			endGame();

			// send message Done
			return;
		}

		generateFood();
	}

	@Override
	public void collision(Snake_Player snakePlayer) {
		Snake snake = snakes[snakePlayer.ordinal()];
		if (snake != null) {
			snake.clearSnake();
		}

		clientThreads[snakePlayer.ordinal()] = null;
		// TODO Delete snake and wait or leave room if have others playing
		// else new game or leave room

		// send message Done

	}

	private long lastTime = 0;
	private long nowTime = 0;

	private int fps = 15;

	private void sendBoard() {
		String send = "BOA";

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				send += boardGame[i][j].getState().ordinal();
			}
		}

		send += "\r\n";

		for (ClientThread clientThread : clientThreads) {
			if (clientThread != null) {
				clientThread.send(send);
			}
		}
	}

	public void run() {
		while (endGame == false) {
			if (isPlaying) {
				nowTime = System.currentTimeMillis();

				if (nowTime - lastTime > (1000 / (double) fps)) {
					lastTime = nowTime;
					move();

					// send information to client
					sendBoard();
				}
			}
		}
	}
}

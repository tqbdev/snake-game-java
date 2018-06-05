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

public class Game implements SnakeListener {
	private Cell[][] boardGame = null;
	private int height;
	private int width;

	private Snake[] snakes = { null, null, null, null };
	private Snake_Player currentSnakePlayer = Snake_Player.Snake_One;

	private ClientThread[] clientThreads = null;

	private int totalPoint;
	private int maxPoint;

	// Game Listener
	private final Set<GameListener> listeners = new CopyOnWriteArraySet<GameListener>();

	public final void addListener(final GameListener listener) {
		listeners.add(listener);
	}

	public final void removeListener(final GameListener listener) {
		listeners.remove(listener);
	}

	private final void endGame() {
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

		for (ClientThread clientThread : clientThreads) {
			if (clientThread != null) {
				clientThread.setSnakeDead(false);
			}
		}
	}

	public void newGame() {
		init();
		initSnake();
		generateFood();
	}

	public void initSnake() {
		currentSnakePlayer = Snake_Player.Snake_One;

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

	public void hostEndGame() {
		for (int i = 0; i < clientThreads.length; i++) {
			ClientThread clientThread = clientThreads[i];

			if (clientThread != null) {
				String end = "END2";

				Snake snake = snakes[i];
				if (snake != null) {
					end += snake.getPoint();
				} else {
					end += '0';
				}

				end += "\r\n";

				clientThread.setEndStr(end);
				clientThread.sendEndSignal();
				//clientThread.send(end);
			}
		}
	}

	public void playerLeaveGame() {
		for (int i = 0; i < clientThreads.length; i++) {
			ClientThread clientThread = clientThreads[i];

			if (clientThread != null) {
				String end = "END1";

				Snake snake = snakes[i];
				if (snake != null) {
					end += snake.getPoint();
				} else {
					end += '0';
				}
				
				end += "\r\n";

				clientThread.setEndStr(end);
				clientThread.sendEndSignal();
				//clientThread.send(end);
			}
		}
	}

	public boolean removeSnake(Snake_Player player) {
		return true;
	}

	public void move() {
		for (int i = 0; i < snakes.length; i++) {
			Snake snake = snakes[i];

			if (snake != null && snake.isDead() == false) {
				snake.move();
			}
		}
	}

	public Cell[][] getBoardGame() {
		return this.boardGame;
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
		sendPoint();
		totalPoint++;
		if (totalPoint >= maxPoint) {
			for (int i = 0; i < clientThreads.length; i++) {
				ClientThread clientThread = clientThreads[i];

				if (clientThread != null) {
					String end = "END0";

					Snake snake = snakes[i];
					if (snake != null) {
						end += snake.getPoint();
					} else {
						end += '0';
					}

					end += "\r\n";
					
					clientThread.setEndStr(end);
					clientThread.sendEndSignal();
					//clientThread.send(end);
				}
			}
			endGame();
			return;
		}

		generateFood();
	}

	@Override
	public void collision(Snake_Player snakePlayer) {
		Snake snake = snakes[snakePlayer.ordinal()];

		ClientThread clientThread = clientThreads[snakePlayer.ordinal()];
		clientThread.setSnakeDead(true);
		// clientThreads[snakePlayer.ordinal()] = null;

		// Send end signal
		String end = "END" + 3 + snake.getPoint() + "\r\n";
		
		clientThread.setEndStr(end);
		clientThread.sendEndSignal();
		//clientThread.send(end);
	}

	private int fps = 20;
	private long delay = 1000 / fps;

	public int getFPS() {
		return fps;
	}

	public void setFPS(int fps) {
		this.fps = fps;
	}

	public long getDelay() {
		return delay;
	}

	public void sendBoard() {
		String send = "BOA";

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				send += boardGame[i][j].getState().ordinal();
			}
		}

		send += "\r\n";

		for (ClientThread clientThread : clientThreads) {
			if (clientThread != null && clientThread.isSnakeDead() == false) {
				clientThread.send(send);
			}
		}
	}

	public void sendPoint() {
		String send = "POI";

		for (int i = 0; i < snakes.length; i++) {
			Snake snake = snakes[i];

			if (snake != null) {
				if (snake.isDead()) {
					send += "0";
				} else {
					send += "1";
				}

				int point = snake.getPoint();

				int paddingPoint = 3;
				if (point != 0) {
					paddingPoint = 3 - (int) (Math.log10(point));
				}

				while (paddingPoint > 0) {
					paddingPoint--;

					send += "0";
				}

				send += point;
			}
		}

		send += "\r\n";
		// System.out.println(send);

		for (ClientThread clientThread : clientThreads) {
			if (clientThread != null && clientThread.isSnakeDead() == false) {
				clientThread.send(send);
			}
		}
	}
}

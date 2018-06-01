package com.tqbdev.game_core;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tqbdev.snake_core.Cell;
import com.tqbdev.snake_core.Snake;
import com.tqbdev.snake_core.SnakeListener;
import com.tqbdev.snake_core.Snake_Player;
import com.tqbdev.snake_core.State;

public class Game implements SnakeListener {
	private Cell[][] boardGame = null;
	private int height;
	private int width;

	private Snake[] snakes = { null, null, null, null };
	private Snake_Player currentSnakePlayer = Snake_Player.Snake_One;
	
	private boolean isPlaying;
	
	private int totalPoint;
	private int maxPoint;
	
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
	
	private final void collisionInGame(Snake_Player snakePlayer) {
		for (GameListener listener : listeners) {
			listener.collisionInGame(snakePlayer);
		}
	}

	public Game(int height, int width) {
		this.height = height;
		this.width = width;

		isPlaying = false;
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

	public boolean addSnake() {
		switch (currentSnakePlayer) {
		case Snake_One:
		case Snake_Two:
		case Snake_Three:
		case Snake_Four:
			int index = currentSnakePlayer.ordinal();
			if (snakes[index] == null) {
				snakes[index] = new Snake(this.boardGame, currentSnakePlayer);
				snakes[index].addListener(this);
				currentSnakePlayer = currentSnakePlayer.next();
				return true;
			} else {
				return false;
			}
		default:
			return false;
		}
	}

	public boolean removeSnake(Snake_Player player) {
		return true;
	}

	public boolean move() {
		if (isPlaying == false) {
			return false;
		}

		for (int i = 0; i < snakes.length; i++) {
			Snake snake = snakes[i];

			if (snake != null) {
				snake.move();
			}
		}

		return true;
	}

	public Cell[][] getBoardGame() {
		return this.boardGame;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	private void generateFood() {
		Random rand = new Random();
		Cell cell = null;
		do {
			int row = rand.nextInt(height);
			int col = rand.nextInt(width);
			cell = boardGame[row][col];
		} while (cell == null || !cell.getState().equals(State.EMPTY));
		
		cell.changeState(State.FOOD);
	}

	@Override
	public void eatFood() {
		totalPoint++;
		if (totalPoint >= maxPoint) {
			isPlaying = false;
			endGame();
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
		// TODO Delete snake and wait or leave room if have others playing
		// else new game or leave room
		collisionInGame(snakePlayer);
	}
}

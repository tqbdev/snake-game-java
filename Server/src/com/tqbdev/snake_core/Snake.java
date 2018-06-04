package com.tqbdev.snake_core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tqbdev.server_core.PlayerListener;

public class Snake implements PlayerListener {
	private Cell head;
	private List<Cell> tail;

	private Direction currentDir;
	private Direction nextDir;

	private int boundWidth;
	private int boundHeight;

	private Cell[][] boardGame;

	private Snake_Player snakePlayer;
	private StateCell stateTail;
	private StateCell stateHead;

	private int point;
	
	private boolean isDead = false;

	private final Set<SnakeListener> listeners = new CopyOnWriteArraySet<SnakeListener>();

	public final void addListener(final SnakeListener listener) {
		listeners.add(listener);
	}

	public final void removeListener(final SnakeListener listener) {
		listeners.remove(listener);
	}

	private final void eatFood() {
		for (SnakeListener listener : listeners) {
			listener.eatFood();
		}
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	private final void collision() {
		isDead = true;
		clearSnake();
		
		for (SnakeListener listener : listeners) {
			listener.collision(snakePlayer);
		}
	}

	public Snake(Cell[][] boardGame, Snake_Player snakePlayer) {
		this.boardGame = boardGame;
		this.snakePlayer = snakePlayer;

		boundHeight = boardGame.length;
		boundWidth = boardGame[0].length;

		this.tail = new ArrayList<>();

		init();
	}

	public void clearSnake() {
		head.changeState(StateCell.EMPTY);
		
		for (int i = 0; i < tail.size(); i++) {
			Cell cell = tail.get(i);
			cell.changeState(StateCell.EMPTY);
		}
	}
	
	public void init() {		
		setState();
		
		switch (this.snakePlayer) {
		case Snake_One:
			setHead(boardGame[10][10]);
			currentDir = nextDir = Direction.LEFT;
			break;
		case Snake_Two:
			setHead(boardGame[50][10]);
			currentDir = nextDir = Direction.UP;
			break;
		case Snake_Three:
			setHead(boardGame[50][50]);
			currentDir = nextDir = Direction.RIGHT;
			break;
		case Snake_Four:
			setHead(boardGame[10][50]);
			currentDir = nextDir = Direction.DOWN;
			break;
		}

		point = 0;
	}

	private void setState() {
		switch (this.snakePlayer) {
		case Snake_One:
			stateTail = StateCell.TAIL1;
			stateHead = StateCell.HEAD1;
			break;
		case Snake_Two:
			stateTail = StateCell.TAIL2;
			stateHead = StateCell.HEAD2;
			break;
		case Snake_Three:
			stateTail = StateCell.TAIL3;
			stateHead = StateCell.HEAD3;
			break;
		case Snake_Four:
			stateTail = StateCell.TAIL4;
			stateHead = StateCell.HEAD4;
			break;
		}
	}

	public int getPoint() {
		return point;
	}

	public void move() {
		currentDir = nextDir;

		final Cell newHead = getFromDirection(head, currentDir);

		if (!newHead.getState().equals(StateCell.EMPTY) && !newHead.getState().equals(StateCell.FOOD)) {
			collision();
			return;
		}

		boolean snakeWillGrow = false;
		if (newHead.getState().equals(StateCell.FOOD)) {
			snakeWillGrow = true;
		}

		Cell lastField = head;

		for (int i = 0; i < tail.size(); i++) {
			final Cell f = tail.get(i);

			lastField.changeState(stateTail);
			tail.set(i, lastField);

			lastField = f;
		}

		if (snakeWillGrow) {
			grow(lastField);
			point++;
			eatFood();
		} else {
			lastField.changeState(StateCell.EMPTY);
		}

		setHead(newHead);
	}

	@Override
	public void changeDirection(final Direction newDir) {
		if (!newDir.isSameOrientation(currentDir)) {
			nextDir = newDir;
		}
	}

	private void setHead(final Cell head) {
		this.head = head;
		head.changeState(stateHead);
	}

	private void grow(final Cell field) {
		field.changeState(stateTail);
		tail.add(field);
	}

	private Cell getFromDirection(Cell cell, Direction direction) {
		int column = cell.getCol();
		int row = cell.getRow();

		switch (direction) {
		case UP:
			row -= 1;
			break;
		case DOWN:
			row += 1;
			break;
		case LEFT:
			column -= 1;
			break;
		case RIGHT:
			column += 1;
			break;
		}

		column += boundWidth;
		column = column % boundWidth;

		row += boundHeight;
		row = row % boundHeight;

		return boardGame[row][column];
	}
}

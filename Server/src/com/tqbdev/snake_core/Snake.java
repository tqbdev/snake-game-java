package com.tqbdev.snake_core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Snake {
	private Cell head;
	private List<Cell> tail;

	private Direction currentDir;
	private Direction nextDir;

	private int boundWidth;
	private int boundHeight;

	private Cell[][] boardGame;

	private Snake_Player snakePlayer;
	private State stateTail;
	private State stateHead;

	private int point;

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
	
	private final void collision() {
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
		head.changeState(State.EMPTY);
		
		for (int i = 0; i < tail.size(); i++) {
			Cell cell = tail.get(i);
			cell.changeState(State.EMPTY);
		}
	}
	
	public void init() {
		setHead(boardGame[10][10]);
		setState();

		currentDir = nextDir = Direction.LEFT;
		point = 0;
	}

	private void setState() {
		switch (this.snakePlayer) {
		case Snake_One:
			stateTail = State.TAIL1;
			stateHead = State.HEAD1;
			break;
		case Snake_Two:
			stateTail = State.TAIL2;
			stateHead = State.HEAD2;
			break;
		case Snake_Three:
			stateTail = State.TAIL3;
			stateHead = State.HEAD3;
			break;
		case Snake_Four:
			stateTail = State.TAIL4;
			stateHead = State.HEAD4;
			break;
		}
	}

	public int getPoint() {
		return point;
	}

	public void move() {
		currentDir = nextDir;

		final Cell newHead = getFromDirection(head, currentDir);

		if (!newHead.getState().equals(State.EMPTY) && !newHead.getState().equals(State.FOOD)) {
			collision();
			return;
		}

		boolean snakeWillGrow = false;
		if (newHead.getState().equals(State.FOOD)) {
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
			lastField.changeState(State.EMPTY);
		}

		setHead(newHead);

		System.out.println(
				"Snake: " + snakePlayer.ordinal() + " - row: " + newHead.getRow() + " - col: " + newHead.getCol());
	}

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

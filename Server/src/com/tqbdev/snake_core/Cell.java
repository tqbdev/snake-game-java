package com.tqbdev.snake_core;

public class Cell {
	private State state;
	private int row;
	private int col;
	
	public Cell() {
		state = State.EMPTY;
	}
	
	public void changeState(State state) {
		this.state = state;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public State getState() {
		return state;
	}
}

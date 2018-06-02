package com.tqbdev.snake_core;

public class Cell {
	private StateCell state;
	private int row;
	private int col;
	
	public Cell() {
		state = StateCell.EMPTY;
	}
	
	public void changeState(StateCell state) {
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

	public StateCell getState() {
		return state;
	}
}

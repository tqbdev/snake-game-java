package com.tqbdev.snake_core;

import java.awt.Color;

public enum StateCell {
	EMPTY(Color.DARK_GRAY),
	FOOD(Color.RED),
	HEAD1(Color.CYAN),
	TAIL1(Color.CYAN),
	HEAD2(Color.GREEN),
	TAIL2(Color.GREEN),
	HEAD3(Color.WHITE),
	TAIL3(Color.WHITE),
	HEAD4(Color.YELLOW),
	TAIL4(Color.YELLOW);
	
	private Color colorCell;
	
	private StateCell(Color colorCell) {
		this.colorCell = colorCell;
	}
	
	public Color getColorCell() {
		return this.colorCell;
	}
}

package com.tqbdev.gui.components;

import java.awt.Graphics;

import javax.swing.JPanel;

import com.tqbdev.snake_core.StateCell;

public class GameCanvas extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5310247801273502178L;
	
	private StateCell[][] boardGame;

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		for (int i = 0; i < boardGame.length; i++) {
			for (int j = 0; j < boardGame[i].length; j++) {
				StateCell cell = boardGame[i][j];
				graphics.setColor(cell.getColorCell());
				graphics.fillRect(j*10, i*10, 10, 10);
			}
		}
	}
	
	public GameCanvas(StateCell[][] boardGame) {
		this.boardGame = boardGame;
	}
}

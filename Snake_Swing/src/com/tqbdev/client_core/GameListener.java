package com.tqbdev.client_core;

import com.tqbdev.snake_core.EndGameState;

public interface GameListener {
	public void updateBoard(String boardStr);
	public void beginGame();
	public void endGame(EndGameState endGameState, int point);
	public void updatePoint(String pointStr);
}

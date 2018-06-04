package com.tqbdev.client_core;

public interface GameListener {
	public void updateBoard(String boardStr);
	public void beginGame();
	public void endGame(String response);
}

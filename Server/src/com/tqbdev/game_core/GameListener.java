package com.tqbdev.game_core;

import com.tqbdev.snake_core.Snake_Player;

public interface GameListener {
	public void endGame();
	public void collisionInGame(Snake_Player snakePlayer);
}

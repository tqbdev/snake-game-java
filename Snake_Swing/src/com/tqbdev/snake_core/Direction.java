package com.tqbdev.snake_core;

public enum Direction {
	UP (true),
	DOWN (true),
	LEFT (false),
	RIGHT (false);
	
	private boolean isVertical;
	
	private Direction(final boolean isVertical) {
		this.isVertical = isVertical;
	}
	
	public boolean isSameOrientation(final Direction otherDir) {
		if (otherDir == null) {
			return false;
		}
		
		return isVertical == otherDir.isVertical;
	}
}

package com.tqbdev.snake_core;

public enum Snake_Player {
	Snake_One,
	Snake_Two,
	Snake_Three,
	Snake_Four {
		@Override
		public Snake_Player next() {
			return null;
		};
	};
	
	public Snake_Player next() {
		return values()[ordinal() + 1];
	}
}

package com.tqbdev.server_core;

public interface DoneListener {
	void leaveRoom(final ClientThread thread);
	void threadComplete(final ClientThread thread);
}

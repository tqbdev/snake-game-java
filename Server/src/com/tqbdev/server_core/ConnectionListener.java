package com.tqbdev.server_core;

public interface ConnectionListener {
	void createRoom(final ClientThread thread);
	void joinRoom(final ClientThread thread, final String codeRoom);
}

package com.tqbdev.server_core;
import java.net.Socket;

public interface ClientThreadListener {
	void notifyOfThreadComplete(final Thread thread);
	void createRoom(Socket clientSocket);
	void joinRoom(Socket clientSocket, String codeRoom);
}

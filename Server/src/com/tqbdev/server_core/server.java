package com.tqbdev.server_core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.tqbdev.utils.RandomString;

public class Server implements ConnectionListener, RoomListener, DoneListener {
	private int PORT = 5000;

	private ServerSocket serverSocket = null;

	private static HashMap<String, Thread> RoomThreads = null;
	private static Set<Thread> ClientThreads = null;

	private static final RandomString ranRoomCode = new RandomString(4);

	public Server() {
		RoomThreads = new HashMap<>();
		ClientThreads = new HashSet<>();
	}

	public void run() throws IOException {
		serverSocket = new ServerSocket(PORT);

		Socket socket = null;

		while (true) {
			socket = serverSocket.accept();

			ClientThread clientThread = new ClientThread(socket);
			clientThread.addConnectionListener(this);
			clientThread.addDoneListener(this);
			clientThread.start();
			ClientThreads.add(clientThread);
		}
	}

	private void sendMessage(ClientThread clientThread, String control, String mess) {
		clientThread.send(control + mess + "\r\n");
	}

	@Override
	public void threadComplete(ClientThread thread) {
		System.out.println("thread Complete");
		ClientThreads.remove(thread);
	}

	@Override
	public void createRoom(ClientThread thread) {
		String roomCode = null;

		do {
			roomCode = ranRoomCode.nextString();
		} while (RoomThreads.containsKey(roomCode));

		if (!thread.isInRoom()) {
			// SEND OK + Roomcode
			sendMessage(thread, "CREA1", roomCode);
			
			Room room = new Room(thread);
			thread.setInRoom(true);
			room.addListener(this);
			room.setRoomCode(roomCode);
			RoomThreads.put(roomCode, room);
			room.start();

			System.out.println("CREATE room code: " + roomCode);
		} else {
			// SEND ER + Message
			sendMessage(thread, "CREA0", "You are in room.");
		}
	}

	@Override
	public void joinRoom(ClientThread thread, String codeRoom) {
		if (!thread.isInRoom()) {
			if (RoomThreads.containsKey(codeRoom)) {
				Room room = (Room) RoomThreads.get(codeRoom);

				if (room.isPlaying()) {
					// ER
					sendMessage(thread, "JOIN0", "This room is playing.");
				} else {
					// OK
					sendMessage(thread, "JOIN1", codeRoom);
					room.addPlayer(thread);
					thread.setInRoom(true);
				}
			} else {
				// ER
				sendMessage(thread, "JOIN0", "Room code is invalid.");
			}
		}
	}

	@Override
	public void destroyRoom(Thread thread) {
		Room room = (Room) thread;
		RoomThreads.remove(room.getRoomCode());

		System.out.println("DESTROY ROOM: " + room.getRoomCode());
	}

	@Override
	public void leaveRoom(ClientThread thread) {
//		if (thread.isInRoom()) {
//			thread.setInRoom(false);
//			thread.setHost(false);
//
//			// Notify to Room
//			// Same threadComplete
//		}
	}
}

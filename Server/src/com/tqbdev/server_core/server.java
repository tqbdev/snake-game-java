package com.tqbdev.server_core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class server {

	static final int PORT = 5000;

	private static HashMap<String, Runnable> Rooms = null;
	private static Set<Thread> PlayerThreads = null;
	private static Set<Socket> PlayerInRoom = null;
	private static final RandomString ran = new RandomString(4);

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;

		Rooms = new HashMap<>();
		PlayerThreads = new HashSet<>();
		PlayerInRoom = new HashSet<>();

		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				socket = serverSocket.accept();

				ClientThread c = new ClientThread(socket);
				c.addListener(new ClientThreadListener() {

					@Override
					public void notifyOfThreadComplete(Thread thread) {
						System.out.println(PlayerThreads.size());
						PlayerThreads.remove(thread);
						System.out.println(PlayerThreads.size());
					}

					@Override
					public void joinRoom(Socket clientSocket, String codeRoom) {
						if (PlayerInRoom.contains(clientSocket)) {

						} else {
							// Check codeRoom exist?
							if (Rooms.containsKey(codeRoom)) {
								Room room = (Room) Rooms.get(codeRoom);

								if (room.isPlaying()) {

								} else {
									room.addPlayer(clientSocket);
									room.addListener(new RoomThreadListener() {

										@Override
										public void playerLeaveRoom(Socket clientSocket) {
											PlayerInRoom.remove(clientSocket);
										}

										@Override
										public void destroyRoom(Room room) {
											Rooms.remove((Object) room);
										}
									});
									PlayerInRoom.add(clientSocket);
								}
							} else {
								// Thong bao cho user
								try {
									clientSocket.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}

					@Override
					public void createRoom(Socket clientSocket) {
						String roomCode = null;

						do {
							roomCode = ran.nextString();
						} while (Rooms.containsKey(roomCode));

						if (PlayerInRoom.contains(clientSocket)) {

						} else {
							Room client = new Room(clientSocket);
							client.setRoomCode(roomCode);
							Rooms.put(roomCode, client);
							Thread t = new Thread(client);
							t.start();
							PlayerInRoom.add(clientSocket);

							System.out.println("CREATE room code: " + roomCode);
						}
					}
				});
				c.start();
				PlayerThreads.add(c);
			} catch (Exception e) {
				System.out.println("I/O error: " + e.getMessage());
			}
		}
	}
}

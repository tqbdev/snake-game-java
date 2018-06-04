package com.tqbdev.client_core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tqbdev.snake_core.Direction;

public class ClientThread extends Thread { // Add inRoomListener, inGameListener
	private boolean isInRoom;
	private boolean isHost;

	private Socket socket;

	// Connect Listener
	private final Set<ConnectListener> connectListeners = new CopyOnWriteArraySet<ConnectListener>();

	public final void addConnectListener(final ConnectListener listener) {
		connectListeners.add(listener);
	}

	public final void removeConnectListener(final ConnectListener listener) {
		connectListeners.remove(listener);
	}

	private final void CreateRoomRespone(String respone, boolean isOK) {
		isInRoom = isOK;
		isHost = isOK;

		for (ConnectListener connectListener : connectListeners) {
			connectListener.CreateRoomRespone(respone, isOK);
		}
	}

	private final void JoinRoomRespone(String respone, boolean isOK) {
		isInRoom = isOK;

		for (ConnectListener connectListener : connectListeners) {
			connectListener.JoinRoomRespone(respone, isOK);
		}
	}
	//

	// Room Listener
	private final Set<RoomListener> roomListeners = new CopyOnWriteArraySet<RoomListener>();

	public final void addRoomListener(final RoomListener listener) {
		roomListeners.add(listener);
	}

	public final void removeRoomListener(final RoomListener listener) {
		roomListeners.remove(listener);
	}

	public final void changeHost() {
		for (RoomListener roomListener : roomListeners) {
			roomListener.changeHost();
		}
	}

	public final void updateRoom(int numberOfPlayers) {
		for (RoomListener roomListener : roomListeners) {
			roomListener.updateRoom(numberOfPlayers);
		}
	}
	//
	
	// Game Listener
	private final Set<GameListener> gameListeners = new CopyOnWriteArraySet<GameListener>();
	
	public final void addGameListener(final GameListener listener) {
		gameListeners.add(listener);
	}
	
	public final void removeGameListener(final GameListener listener) {
		gameListeners.remove(listener);
	}
	
	public final void updateBoard(String boardStr) {
		for (GameListener gameListener : gameListeners) {
			gameListener.updateBoard(boardStr);
		}
	}
	
	public final void endGame(String response) {
		for (GameListener gameListener : gameListeners) {
			gameListener.endGame(response);
		}
	}
	//

	public ClientThread(Socket socket) {
		this.socket = socket;
		isInRoom = false;
		isHost = false;
	}

	private void send(String message) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(message);
			out.flush();
		} catch (IOException e) {
			return;
		}
	}

	public void createRoom() {
		send("CREA\r\n");
	}

	public void joinRoom(String roomCode) {
		send("JOIN" + roomCode + "\r\n");
	}

	public void quit() {
		send("QUIT\r\n");
		try {
			socket.close();
		} catch (IOException e) {
			// END THREAD
		}
	}

	public void leaveRoom() {
		send("LEA\r\n");
	}

	public void startGame() {
		send("STA\r\n");
	}

	public void stopGame() {
		send("STO\r\n");
	}

	public void changeDirection(Direction direction) {
		String dir = "";
		switch (direction) {
		case DOWN:
			dir = "D";
			break;
		case LEFT:
			dir = "L";
			break;
		case RIGHT:
			dir = "R";
			break;
		case UP:
			dir = "U";
			break;
		}
		
		send("DIR" + dir + "\r\n");
	}

	public boolean isInRoom() {
		return isInRoom;
	}

	public void setInRoom(boolean isInRoom) {
		this.isInRoom = isInRoom;
	}

	public boolean isHost() {
		return isHost;
	}

	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

	public void run() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return;
		}

		String line = null;
		while (true) {
			if (socket.isClosed()) {
				break;
			}
			
			try {
				// if not playing
				Thread.sleep(20);
				// else sleep 10
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				line = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				line = null;
			}

			if (line != null && line.length() > 0) {
				if (isInRoom) {
					String control = line.substring(0, 3);

					if (control.equalsIgnoreCase("INF")) {
						String numStr = line.substring(3);

						int numberOfPlayers;
						try {
							numberOfPlayers = Integer.parseInt(numStr);
						} catch (NumberFormatException e) {
							numberOfPlayers = 0;
						}
						
						updateRoom(numberOfPlayers);
					} else if (control.equalsIgnoreCase("CHA")) {
						changeHost();
					} else if (control.equalsIgnoreCase("BOA")) {
						String boardStr = line.substring(3);
						
						updateBoard(boardStr);
					}
				} else {
					String control = line.substring(0, 4);

					if (control.equalsIgnoreCase("CREA")) {
						String check = line.substring(4, 5);
						boolean isOK = false;
						if (check.equals("1")) {
							isOK = true;
						}

						String respone = line.substring(5);

						CreateRoomRespone(respone, isOK);
					} else if (control.equalsIgnoreCase("JOIN")) {
						String check = line.substring(4, 5);
						boolean isOK = false;
						if (check.equals("1")) {
							isOK = true;
						}

						String respone = line.substring(5);

						JoinRoomRespone(respone, isOK);
					}
				}
			}
		}
	}
}

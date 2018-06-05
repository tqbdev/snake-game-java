package com.tqbdev.server_core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tqbdev.snake_core.Direction;

public class ClientThread extends Thread {
	private Socket socket;
	private boolean isHost;
	private boolean isInRoom;
	private boolean isSnakeDead;
	
	private boolean isChecking = false;
	
	// Listener Player
	private final Set<PlayerListener> playerListeners = new CopyOnWriteArraySet<PlayerListener>();

	public final void addPlayerListener(final PlayerListener listener) {
		playerListeners.add(listener);
	}

	public final void removePlayerListener(final PlayerListener listener) {
		playerListeners.remove(listener);
	}

	private void changeDirection(Direction direction) {
		for (PlayerListener playerInputListener : playerListeners) {
			playerInputListener.changeDirection(direction);
		}
	}
	//
	
	// Listener Host
	private final Set<HostListener> hostListeners = new CopyOnWriteArraySet<HostListener>();

	public final void addHostListener(final HostListener listener) {
		hostListeners.add(listener);
	}

	public final void removeHostListener(final HostListener listener) {
		hostListeners.remove(listener);
	}

	private void startGame() {
		if (isHost) {
			for (HostListener hostInputListener : hostListeners) {
				hostInputListener.startGame();
			}
		}
	}

	private void stopGame() {
		if (isHost) {
			for (HostListener hostInputListener : hostListeners) {
				hostInputListener.stopGame();
			}
		}
	}
	//
	
	// Listener Connection
	private final Set<ConnectionListener> connectionListeners = new CopyOnWriteArraySet<ConnectionListener>();

	public final void addConnectionListener(final ConnectionListener listener) {
		connectionListeners.add(listener);
	}

	public final void removeConnectionListener(final ConnectionListener listener) {
		connectionListeners.remove(listener);
	}
	
	private void createRoom() {
		for (ConnectionListener connectionListener : connectionListeners) {
			connectionListener.createRoom(this);
		}
	}
	
	private void joinRoom(final String codeRoom) {
		for (ConnectionListener connectionListener : connectionListeners) {
			connectionListener.joinRoom(this, codeRoom);
		}
	}
	//
	
	// Done Listener
	private final Set<DoneListener> doneListeners = new CopyOnWriteArraySet<DoneListener>();
	
	public final void addDoneListener(final DoneListener listener) {
		doneListeners.add(listener);
	}

	public final void removeDoneListener(final DoneListener listener) {
		doneListeners.remove(listener);
	}
	
	private void threadComplete() {
		for (DoneListener doneListener : doneListeners) {
			doneListener.threadComplete(this);
		}
	}
	
	private void leaveRoom() {
		for (DoneListener doneListener : doneListeners) {
			doneListener.leaveRoom(this);
		}
	}
	//

	public ClientThread(Socket socket) {
		isHost = false;
		isInRoom = false;
		isSnakeDead = false;
		this.socket = socket;
	}
	
	// Getter and Setter
	public boolean isHost() {
		return isHost;
	}

	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

	public boolean isInRoom() {
		return isInRoom;
	}

	public void setInRoom(boolean isInRoom) {
		this.isInRoom = isInRoom;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public boolean isSnakeDead() {
		return isSnakeDead;
	}

	public void setSnakeDead(boolean isSnakeDead) {
		this.isSnakeDead = isSnakeDead;
	}
	//

	// Send to Client
	public void send(String str) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(str);
			out.flush();
		} catch (IOException e) {
			threadComplete();
			this.interrupt();
			return;
		}
	}
	
	public void send(int number) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			out.writeInt(number);
			out.flush();
		} catch (IOException e) {
			threadComplete();
			this.interrupt();
			return;
		}
	}
	
	public void send(byte[] arrayByte) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			out.write(arrayByte);
			out.flush();
		} catch (IOException e) {
			threadComplete();
			this.interrupt();
			return;
		}
	}
	
	String endStr = null;
	
	public void setEndStr(String endStr) {
		this.endStr = endStr;
	}
	
	public void sendEndSignal() {
		isChecking = true;
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(endStr);
			out.flush();
		} catch (IOException e) {
			threadComplete();
			this.interrupt();
			return;
		}
	}
	//

	// Run when start thread
	public void run() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			try {
				socket.close();
			} catch (IOException e1) {
				//e1.printStackTrace();
			}
			
			threadComplete();
			this.interrupt();
			return;
		}

		String line;
		while (true) {
			try {
				line = in.readLine();

				if (socket.isClosed()) {
					break;
				}
				
				try {
					// if not playing
					Thread.sleep(30);
					// else sleep 10
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}

				String control = null;
				if (isInRoom) {
					control = line.substring(0, 3);

					if (control.equalsIgnoreCase("STA")) { // START GAME
						startGame();
					} else if (control.equalsIgnoreCase("STO")) { // STOP GAME
						stopGame();
					} else if (control.equalsIgnoreCase("DIR")) { // CHANGE DIRECTION
						char dir = line.charAt(3);
						
						Direction direction = Direction.DOWN;
						switch (dir) {
						case 'D':
							direction = Direction.DOWN;
							break;
						case 'U':
							direction = Direction.UP;
							break;
						case 'L':
							direction = Direction.LEFT;
							break;
						case 'R':
							direction = Direction.RIGHT;
							break;
						}
						
						System.out.println(direction);
						changeDirection(direction);
					} else if (control.equalsIgnoreCase("LEA")) { // LEAVE ROOM
						System.out.println("LEAVE ROOM");
						leaveRoom();
					} else if (control.equalsIgnoreCase("END")) {
						isChecking = false;
					}
					
					if (isChecking) {					
						sendEndSignal();
					}
				} else {
					control = line.substring(0, 4);

					if ((line == null) || control.equalsIgnoreCase("QUIT")) {						
						socket.close();
						break;
					} else if (control.equalsIgnoreCase("CREA")) { // CREATE ROOM
						createRoom();
					} else if (control.equalsIgnoreCase("JOIN")) { // JOIN ROOM
						String codeRoom = line.substring(4);
						System.out.println(codeRoom);
						joinRoom(codeRoom);
					}
				}
			} catch (IOException e) {
				//e.printStackTrace();
				break;
			}
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		System.out.println("Done client thread");
		
		threadComplete();
		this.interrupt();
	}
}

package com.tqbdev.server_core;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tqbdev.game_core.Game;
import com.tqbdev.game_core.GameListener;

public class Room extends Thread implements HostListener, DoneListener, GameListener {
	private ClientThread[] clientThreads = { null, null, null, null };
	private ClientThread hostThread = null;
	private String roomCode = null;

	private Game game = null;
	private boolean isPlaying = false;
	private boolean endRoom = false;

	// Room Listener
	private final Set<RoomListener> listeners = new CopyOnWriteArraySet<RoomListener>();

	public final void addListener(final RoomListener listener) {
		listeners.add(listener);
	}

	public final void removeListener(final RoomListener listener) {
		listeners.remove(listener);
	}

	private final void destroyRoom() {
		endRoom = true;
		
		for (RoomListener listener : listeners) {
			listener.destroyRoom(this);
		}
	}
	//

	// Getter and Setter
	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public String getRoomCode() {
		return this.roomCode;
	}

	public boolean isPlaying() {
		return isPlaying;
	}
	//

	public Room(ClientThread hostThread) {
		this.hostThread = hostThread;
		this.hostThread.addDoneListener(this);
		this.hostThread.addHostListener(this);
		this.clientThreads[0] = hostThread;
		this.hostThread.setHost(true);
	}

	public boolean addPlayer(ClientThread playerThread) {
		for (int i = 0; i < this.clientThreads.length; i++) {
			if (this.clientThreads[i] == null) {
				this.clientThreads[i] = playerThread;
				playerThread.addDoneListener(this);
				return true;
			} else {
				if (this.clientThreads[i].equals(playerThread)) {
					return false;
				}
			}
		}

		return false;
	}
	
	private void sendInformationRoom() {
		int numOfClient = 0;
		
		while (clientThreads[numOfClient] != null && numOfClient != clientThreads.length) {
			numOfClient++;
		}
		
		System.out.println(numOfClient);
		byte[] arrayByte = new byte[2];
		arrayByte[0] = 0;
		arrayByte[1] = (byte) numOfClient;
		
		int len = arrayByte.length;
		for (int i = 0; i < numOfClient; i++) {
			clientThreads[i].send("INF" + len);
			clientThreads[i].send(arrayByte);
		}
	}

	public void run() {
		while (endRoom == false) {
			if (!isPlaying()) {
				sendInformationRoom();
			}
		}
	}

	@Override
	public void startGame() {
		game = new Game(20, 20);
		game.addListener(this);
		game.setPlayerThreads(clientThreads);
		game.newGame();
		game.beginGame();
		game.start();
		isPlaying = true;
	}

	@Override
	public void stopGame() {
		game.hostEndGame();
		isPlaying = false;
	}

	@Override
	public void leaveRoom(ClientThread thread) {
		thread.removeHostListener(this);
		thread.removeDoneListener(this);
		removePlayer(thread);		
	}

	@Override
	public void threadComplete(ClientThread thread) {
		removePlayer(thread);		
	}
	
	private void removePlayer(ClientThread thread) {
		System.out.println("Remove Player");
		if (isPlaying()) {
			// TODO end game
		} else {
			// TODO remove
		}
		
		if (thread.isHost()) {
			clientThreads[0] = null;
			
			for (int i = 0; i < clientThreads.length - 1; i++) {
				clientThreads[i] = clientThreads[i + 1];
			}

			clientThreads[clientThreads.length - 1] = null;
		}
		
		if (clientThreads[0] == null) {
			destroyRoom();
		} else {
			clientThreads[0].setHost(true);
			clientThreads[0].addHostListener(this);
			// TODO Notify to client to change host
		}
	}

	@Override
	public void endGame() {
		isPlaying = false;		
	}
}

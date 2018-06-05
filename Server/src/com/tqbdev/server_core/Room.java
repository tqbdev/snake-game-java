package com.tqbdev.server_core;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tqbdev.game_core.Game;
import com.tqbdev.game_core.GameListener;

public class Room extends Thread implements HostListener, DoneListener, GameListener {
	private ClientThread[] clientThreads = { null, null, null, null };
	private String roomCode = null;

	private Game game = null;
	private boolean isPlaying = false;
	private boolean isBeginPlaying = false;
	private int countDown = 10;
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
		hostThread.addDoneListener(this);
		hostThread.addHostListener(this);
		hostThread.setHost(true);
		this.clientThreads[0] = hostThread;

		game = new Game(60, 60);
		game.addListener(this);
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

		while (numOfClient != clientThreads.length && clientThreads[numOfClient] != null) {
			numOfClient++;
		}

		// System.out.println(numOfClient);

		for (int i = 0; i < numOfClient; i++) {
			clientThreads[i].send("INF" + numOfClient + "\r\n");
		}
	}

	private void sendBeginSignal() {
		for (int i = 0; i < clientThreads.length; i++) {
			ClientThread clientThread = clientThreads[i];
			String send = "BEG";
			send += i;
			send += "\r\n";
			if (clientThread != null) {
				clientThread.send(send);
			}
		}
	}

	private void sendCountDown() {
		String send = "COU";
		send += countDown;
		send += "\r\n";

		for (int i = 0; i < clientThreads.length; i++) {
			ClientThread clientThread = clientThreads[i];

			if (clientThread != null) {
				clientThread.send(send);
			}
		}
	}

	public void run() {
		while (endRoom == false) {
			if (isPlaying == false) {
				sendInformationRoom();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			} else {
				if (isBeginPlaying) {
					isBeginPlaying = false;

					sendBeginSignal();
				}

				if (countDown > 0) {
					sendCountDown();
					countDown--;

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				} else {
					game.move();
					game.sendBoard();

					try {
						Thread.sleep(game.getDelay());
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void startGame() {
		if (isPlaying == false) {
			game.setPlayerThreads(clientThreads);
			game.newGame();
			isPlaying = true;
			isBeginPlaying = true;
			countDown = 10;
			this.interrupt();
		}
	}

	@Override
	public void stopGame() {
		if (isPlaying) {
			isPlaying = false;
			isBeginPlaying = false;
			// this.interrupt();
			game.hostEndGame();
		}
	}

	@Override
	public void leaveRoom(ClientThread thread) {
		thread.removeHostListener(this);
		thread.removeDoneListener(this);
		removePlayer(thread);
	}

	@Override
	public synchronized void threadComplete(ClientThread thread) {
		//System.out.println("TEST");
		removePlayer(thread);
	}

	public boolean isFull() {
		for (int i = 0; i < clientThreads.length; i++) {
			if (clientThreads[i] == null) {
				return false;
			}
		}

		return true;
	}

	private void removePlayer(ClientThread thread) {
		if (isPlaying && thread.isSnakeDead() == false) {
			isPlaying = false;
			isBeginPlaying = false;

			this.interrupt();
			game.playerLeaveGame();
		}

		if (thread.isHost()) {
			clientThreads[0] = null;

			for (int i = 0; i < clientThreads.length - 1; i++) {
				clientThreads[i] = clientThreads[i + 1];
			}

			clientThreads[clientThreads.length - 1] = null;
		} else {
			// Find thread
			int i = 0;
			for (; i < clientThreads.length; i++) {
				if (clientThreads[i] != null && clientThreads[i].equals(thread)) {
					break;
				}
			}
			if (i < 4) {

				clientThreads[i] = null;

				for (; i < clientThreads.length - 1; i++) {
					clientThreads[i] = clientThreads[i + 1];
				}

				clientThreads[clientThreads.length - 1] = null;
			}
		}

		if (clientThreads[0] == null) {
			destroyRoom();
		} else {
			if (!clientThreads[0].isHost()) {
				clientThreads[0].setHost(true);
				clientThreads[0].addHostListener(this);
				clientThreads[0].send("CHA\r\n");
			}
		}

		thread.setInRoom(false);
		thread.setHost(false);
		thread.setSnakeDead(false);
	}

	@Override
	public void endGame() {
		isPlaying = false;
		this.interrupt();
	}
}

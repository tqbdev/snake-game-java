import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

public class Room implements Runnable {
	private Socket[] clientSockets = { null, null, null, null };
	private String roomCode = null;
	
	private long lastTime = 0;
	private long nowTime = 0;
	
	private final Set<RoomThreadListener> listeners = new CopyOnWriteArraySet<RoomThreadListener>();

	public Room(Socket hostSocket) {
		this.clientSockets[0] = hostSocket;
	}
	
	public final void addListener(final RoomThreadListener listener) {
		listeners.add(listener);
	}

	public final void removeListener(final RoomThreadListener listener) {
		listeners.remove(listener);
	}

	private final void destroyRoom() {
		for (RoomThreadListener listener : listeners) {
			listener.destroyRoom(this);
		}
	}
	
	private void playerLeaveRoom(final Socket clientSocket) {
		for (RoomThreadListener listener : listeners) {
			listener.playerLeaveRoom(clientSocket);
		}
	}
	
	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public boolean addPlayer(Socket playerSocket) {
		for (int i = 0; i < this.clientSockets.length; i++) {
			if (this.clientSockets[i] == null) {
				this.clientSockets[i] = playerSocket;
				return true;
			} else {
				if (this.clientSockets[i].equals(playerSocket)) {
					return false;
				}
			}
		}

		return false;
	}

	@Override
	public void run() {
		while (true) {
			nowTime = System.currentTimeMillis();

			if (nowTime - lastTime > 1000) {
				lastTime = nowTime;

				int index = -1;
				try {
					for (index = 0; index < clientSockets.length; index++) {
						Socket socket = clientSockets[index];
						if (socket != null) {
							DataOutputStream out = new DataOutputStream(socket.getOutputStream());
							out.writeBytes("Hello " + index + "\n\r");
							out.flush();
						}
					}
				} catch (IOException e) {
					try {
						if (index > -1 && index < clientSockets.length) {
							Socket socket = clientSockets[index];
							socket.close();	
							clientSockets[index] = null;
							playerLeaveRoom(socket);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					if (clientSockets[0] == null) {
						for (int i = 0; i < clientSockets.length - 1; i++) {
							clientSockets[i] = clientSockets[i + 1];
						}

						clientSockets[clientSockets.length - 1] = null;
					}

					if (clientSockets[0] == null) {
						System.out.println("No host. Delete room");
						destroyRoom();
						for (int i = 0; i < clientSockets.length; i++) {
							Socket socket = clientSockets[i];
							if (socket != null) {
								try {
									socket.close();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
						
						return;
					}

					continue;
				}
			}
		}
	}
}

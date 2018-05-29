import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ClientThread extends Thread {
	private Socket socket;

	private final Set<ClientThreadListener> listeners = new CopyOnWriteArraySet<ClientThreadListener>();

	public final void addListener(final ClientThreadListener listener) {
		listeners.add(listener);
	}

	public final void removeListener(final ClientThreadListener listener) {
		listeners.remove(listener);
	}

	private final void notifyListeners() {
		for (ClientThreadListener listener : listeners) {
			listener.notifyOfThreadComplete(this);
		}
	}
	
	private final void createRoom() {
		for (ClientThreadListener listener : listeners) {
			listener.createRoom(socket);
		}
	}
	
	private final void joinRoom(String codeRoom) {
		for (ClientThreadListener listener : listeners) {
			listener.joinRoom(socket, codeRoom);
		}
	}

	public ClientThread(Socket clientSocket) {
		this.socket = clientSocket;
	}

	public void run() {
		BufferedReader in = null;
		DataOutputStream out = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			notifyListeners();
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
				
				String control = line.substring(0, 4);

				if ((line == null) || control.equalsIgnoreCase("QUIT")) {
					out.writeBytes(line + "\n\r");
					System.out.println(line);
					out.flush();
					socket.close();
					break;
				} else if (control.equalsIgnoreCase("CREA")) {
					out.writeBytes(line + "\n\r");
					System.out.println(line);
					out.flush();
					createRoom();
					break;
				} else if (control.equalsIgnoreCase("JOIN")) {
					out.writeBytes("JOIN\n\r");
					System.out.println(line);
					out.flush();
					String codeRoom = line.substring(4);
					System.out.println(codeRoom);
					joinRoom(codeRoom);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		
		notifyListeners();
		this.interrupt();
	}
}

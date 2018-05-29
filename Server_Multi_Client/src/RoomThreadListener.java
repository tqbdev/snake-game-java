import java.net.Socket;

public interface RoomThreadListener {
	void destroyRoom(final Room room);
	void playerLeaveRoom(final Socket clientSocket);
}
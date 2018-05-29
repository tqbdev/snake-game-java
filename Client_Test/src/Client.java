import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		String hostName = "localhost";
		int portNumber = 5000;
		try {
			Socket kkSocket = new Socket(hostName, portNumber);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(kkSocket.getOutputStream()));
			BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));

			Scanner reader = new Scanner(System.in);
			String responseLine;
			String sendLine;

			boolean check = true;
			while (true) {
				if (check) {
					System.out.print("Client to server: ");
					sendLine = reader.nextLine();
					if (sendLine == null || sendLine.equalsIgnoreCase("")) {
						break;
					}

					out.write(sendLine);
					out.newLine();
					out.flush();
				}

				System.out.print("Server to client: ");
				responseLine = in.readLine();
				if (responseLine.equalsIgnoreCase("QUIT")) {
					break;
				}

				if (responseLine != null && !responseLine.equalsIgnoreCase("")) {
					System.out.println(responseLine);
					
					if (responseLine.equalsIgnoreCase("CREA")) {
						check = false;
					} else if (responseLine.equalsIgnoreCase("JOIN")) {
						check = false;
					}
				}
			}

			reader.close();
			out.close();
			in.close();
			kkSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

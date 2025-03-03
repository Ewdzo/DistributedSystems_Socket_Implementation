import java.net.ServerSocket;
import java.net.Socket;

import handlers.ClientHandler;

public class Server {
	static int port = 5000;
	static String pathToFiles = "./src/files/";

	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Listening at " + port);

			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println(clientSocket.getInetAddress().getHostAddress() + " connected.");

				ClientHandler clientSock = new ClientHandler(clientSocket);
				new Thread(clientSock).start();
			}

			// receiveFile(pathToFiles + "Bokuto2.png");

			// dataOutputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// public static void receivingRequest(ServerSocket serverSocketObj) throws
	// Exception {
	// Socket serverSideSocket = serverSocketObj.accept();
	// ObjectOutputStream outputStream = new
	// ObjectOutputStream(serverSideSocket.getOutputStream());
	// ObjectInputStream inputStream = new
	// ObjectInputStream(serverSideSocket.getInputStream());

	// System.out.println(inputStream.readObject());
	// }

	private static void receiveFile(String fileName) throws Exception {
		// int bytes = 0;
		// FileOutputStream fileOutputStream = new FileOutputStream(fileName);

		// long size = dataInputStream.readLong();
		// byte[] buffer = new byte[4 * 1024];
		// while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)
		// Math.min(buffer.length, size))) != -1) {
		// fileOutputStream.write(buffer, 0, bytes);
		// size -= bytes;
		// }
		// fileOutputStream.close();
	}
}
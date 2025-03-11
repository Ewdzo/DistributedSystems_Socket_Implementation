package socket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import socket.handlers.ClientHandler;

public class Server {
	static int port = 5000;
	private static String pathToFiles = "./src/files/";
    private static final Map<Integer, String> options;

    static {
        options = new HashMap<>();
        options.put(1, "List Available Files");
        options.put(2, "Download File");
        options.put(4, "Close Connection");
    }

	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Listening at " + port);

			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println(clientSocket.getInetAddress().getHostAddress() + " connected.");

				ClientHandler clientSock = new ClientHandler(clientSocket);
				new Thread(clientSock).start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getOptionsString() {
		String opt = "";

		for (Integer key : options.keySet()) {
			opt = opt.concat(key + " - " + options.get(key) + "\n");
		};

		return opt;
	}

	public static Map<Integer, String> getOptions() {
		return Server.options;
	}

	public static String getPathToFiles() {
		return pathToFiles;
	}

}
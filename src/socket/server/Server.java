package socket.server;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import socket.server.handlers.ClientHandler;

public class Server {
	static int port = 5000;
	private static String pathToFiles = "./src/files/";
    private static final Map<String, String> options;

    static {
        options = new HashMap<>();
        options.put("1", "List Available Files");
        options.put("2", "Download File");
        options.put("0", "Close Connection");
    }

	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Listening at " + port);

			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " connected.");

				ClientHandler clientSock = new ClientHandler(clientSocket);
				new Thread(clientSock).start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getMenu() {
		String opt = "== File Manager ==\n\n";

		for (String key : options.keySet()) {
			opt = opt.concat(key + " - " + options.get(key) + "\n");
		};

		opt = opt.concat("Option:");
		
		return opt;
	};

	public static Map<String, String> getOptions() {
		return Server.options;
	}

	public static String getPathToFiles() {
		return pathToFiles;
	}

}
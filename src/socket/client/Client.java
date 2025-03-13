package socket.client;

import socket.client.handlers.ServerHandler;

public class Client {
    private static int port = 5000;
    private static String ip = "localhost";
    private static String pathToDownloads = "./src/downloads/";

    public static void main(String[] args) {
        ServerHandler serverSock = new ServerHandler(ip, port);
        new Thread(serverSock).start();
    }
    
	public static String getPathToFiles() {
		return pathToDownloads;
	}

    public static String getIp() {
        return ip;
    }

    public static int getPort() {
        return port;
    }
}
package socket.client.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import socket.client.Client;
import socket.client.controller.ClientController;

public class DownloadHandler implements Runnable {
    Socket socket;
    BufferedReader in;
    ClientController controller;
    String path;

    public DownloadHandler(String ip, int port, String file) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            controller = new ClientController(socket);
            path = file;
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void run() {
        try {
            controller.getService().sendResponse("File Path: " + path);
            controller.getService().downloadFile(Client.getPathToFiles() + path);
        } catch (Exception e) {
            e.printStackTrace();
        };

        return;
    }
};

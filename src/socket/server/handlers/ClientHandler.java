package socket.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

import socket.server.controller.ServerController;

public class ClientHandler implements Runnable {
    static Socket socket;
    ServerController controller;
    BufferedReader in;
    
    public ClientHandler(Socket s) {
        try {
            socket = s;
            controller = new ServerController(socket);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void run() {
        try {
            while (true) {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.printf(this.hashCode() + " - " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " sent: %s\n", line);
                    controller.handleRes(line);

                    if (!in.ready())
                        break;
                }
            }

        } catch (SocketException e) {
            // e.printStackTrace();
            System.out.println(socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " connection terminated.");
        } catch (Exception e) {
            e.printStackTrace();
        };
    }
};
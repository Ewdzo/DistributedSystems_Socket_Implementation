package socket.client.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;

import socket.client.controller.ClientController;

public class ServerHandler implements Runnable {
    Socket socket;
    BufferedReader in;
    ClientController controller;
    public boolean ready = true;

    public ServerHandler(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            controller = new ClientController(socket);
        } catch (ConnectException e) {
            System.out.println("Failed connection to " + ip + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void run() {
        try {
            while (true && socket != null) {
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println(response);

                    if (!in.ready()) {
                        controller.handleRes(response);
                        break;
                    }
                }

                if (socket.isClosed())
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;
    }
};

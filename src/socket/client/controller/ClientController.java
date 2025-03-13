package socket.client.controller;

import java.net.Socket;

import socket.client.service.ClientService;

public class ClientController {
    Socket socket;
    ClientService service;

    public ClientController(Socket s) {
        socket = s;
        service = new ClientService(socket);
    };

    public void handleRes(String res) {
        if (res.contains("Insert File Index:")) service.sendFileIndex();
        else if (res.contains("Option:")) service.sendOption();
        else if (res.contains("File Path: ")) service.receiveFile(res.replace("File Path: ", ""));
        else if(res.equals("200: Success - Closing Socket")) service.close();
    };

    public ClientService getService() {
        return service;
    }
}

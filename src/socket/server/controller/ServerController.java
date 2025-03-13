package socket.server.controller;

import java.net.Socket;

import socket.server.Server;
import socket.server.service.ServerService;

public class ServerController {
    Socket socket;
    ServerService service;
    String lastSent;
    
    public ServerController(Socket s) {
        socket = s;
        service = new ServerService(socket);
    };

    public void handleRes(String res) {
        lastSent = res;
        if(res.contains("Option: ")) handleOption(res.replace("Option: ", ""));
        else if(res.contains("File Index: ")) service.sendFilePath(res.replace("File Index: ", ""));
        else if(res.contains("File Path: ")) service.sendFile(res.replace("File Path: ", ""));
        else if(res.length() > 0) service.sendInvalid();
    }

    public void handleOption(String res) {
        if(!res.equals("-1") && !Server.getOptions().containsKey(res)) service.sendInvalid();

        if(res.equals("-1")) service.sendMenu();
        else if(res.equals("0")) service.close();
        else if(res.equals("1")) service.sendFilesList();
        else if(res.equals("2")) service.getFileIndex();
    };

    public String getLastSent() {
        return lastSent;
    }
}

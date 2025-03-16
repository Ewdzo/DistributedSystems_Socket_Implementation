package socket.server.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import socket.server.Server;

public class ServerService {
    Socket socket;
    PrintWriter out;

    public ServerService(Socket s) {
        socket = s;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("500: Internal Server Error - ServerService Failure");
            e.printStackTrace();
        }
    };

    public void close() {
        try {
            sendResponse("200: Success - Closing Socket");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void sendInvalid() {
        sendResponse("400: Bad Request - Invalid Input");
        close();
    }

    public void sendResponse(String res) {
        out.println(res);
    }

    public void sendMenu() {
        sendResponse(Server.getMenu());
    }

    public void sendFilesList() {
        String[] pathnames;
        String files = "\n == Files ==\n\n";
        File f = new File(Server.getPathToFiles());
        pathnames = f.list();

        for (int i = 0; i < pathnames.length; i++) {
            files += (i) + " - " + pathnames[i] + "\n";
        }

        sendResponse(files);
        close();
    }

    public void getFileIndex() {
        String[] pathnames;
        String res = "\n\n == Files ==\n\n";
        File f = new File(Server.getPathToFiles());
        pathnames = f.list();

        for (int i = 0; i < pathnames.length; i++) {
            res += (i) + " - " + pathnames[i] + "\n";
        }

        res = res.concat("\nInsert File Index:");
        
        sendResponse(res);
    }

    public void sendFilePath(String res) {
        try {
            File f = new File(Server.getPathToFiles());
            String[] pathnames = f.list();
            int index = Integer.parseInt(res);
            String path = pathnames[index];
            sendResponse("File Path: " + path); // Send name of file so client know how to store.
        } catch (ArrayIndexOutOfBoundsException e) {
            sendResponse("404: Not Found - Index Not Found");
            close();
        }
    }
    
    public void sendFile(String path) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            int bytes = 0;

            while (true) {
                File file = new File(Server.getPathToFiles() + path);
                FileInputStream fileInputStream = new FileInputStream(file);

                dataOutputStream.writeLong(file.length());
                byte[] buffer = new byte[4 * 1024];
                while ((bytes = fileInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytes);
                    dataOutputStream.flush();
                }

                fileInputStream.close();
                sendResponse("200: " + path + " successfully sent.");
            }
        } catch (Exception e) {
            sendResponse("500: Internal Server Error - Something went wrong.");
            e.printStackTrace();
        };
        close();
    };
}

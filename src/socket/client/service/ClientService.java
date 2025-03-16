package socket.client.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import socket.client.Client;
import socket.helper.Input;

public class ClientService {
    private Socket socket;
    private PrintWriter out;

    public ClientService(Socket s) {
        try {
            socket = s;
            out = new PrintWriter(socket.getOutputStream(), true);
            sendResponse("Option: -1");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("500: Internal Server Error - ClientService Failure");
        }
    };

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void sendResponse(String res) {
        out.println(res);
    }

    public void sendOption() {
        String option = Input.getIntegerInput().toString();

        sendResponse("Option: " + option);
    }

    public void sendFileIndex() {
        String option = Input.getIntegerInput().toString();

        sendResponse("File Index: " + option);
    }

    public void receiveFile(String fileName) {
        try {
            String path = Client.getPathToFiles() + fileName;
            File f = new File(path);
            if (f.exists() || f.isDirectory()) {
                System.out.println("File already exists or is directory");
                close();
                return;
            }

            sendResponse("File Path: " + fileName);
            downloadFile(Client.getPathToFiles() + fileName);
            close();
        } catch (Exception e) {
            sendResponse("500: Internal Server Error - Failure Receiving File");
        }
        sendResponse("Option: -1");
    }

    public void downloadFile(String path) {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(path);
    
            long size = dataInputStream.readLong();
            byte[] buffer = new byte[4 * 1024];
            int bytes = 0;
            while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }
            fileOutputStream.close();
    
            String fileName = path.replace(Client.getPathToFiles(), "");
            System.out.println("200: Success - " + fileName + " downloaded.");
            sendResponse("200: Success - " + fileName + " downloaded.");
        } catch (Exception e) {
            sendResponse("500: Internal Server Error - Failure Receiving File");
        }
    }
}

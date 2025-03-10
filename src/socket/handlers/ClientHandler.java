package socket.handlers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import socket.Server;

public class ClientHandler implements Runnable {
    static Socket socket;
    static DataOutputStream dataOutputStream = null;
    static DataInputStream dataInputStream = null;

    public ClientHandler(Socket s) {
        socket = s;
        try {
            dataOutputStream = new DataOutputStream(s.getOutputStream());
            dataInputStream = new DataInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                out.println("\n== File Manager ==\n\n" + Server.getOptionsString() + "Insert:");

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.printf("Sent from " + socket.getInetAddress().getHostAddress() + ": %s\n", line);
                    handleReq(line, out);
                    if (!in.ready())
                        break;
                }
            }

        } catch (SocketException e) {

            System.out.println(socket.getInetAddress().getHostAddress() + " connection terminated.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void handleReq(String option, PrintWriter out) throws Exception {
        Integer opt = Integer.parseInt(option);
        String response = "Invalid Option";

        if (!Server.getOptions().containsKey(opt))
            throw new Error("Invalid Option");

        if (opt == 1) {
            response = listFiles();
        } else if (opt == 2) {
            response = sendFile();
        }
        ;

        out.println(response);
    };

    public static String listFiles() {
        String[] pathnames;
        String files = "Files: ";
        File f = new File(Server.getPathToFiles());
        pathnames = f.list();

        for (String pathname : pathnames) {
            files += pathname + " ";
        }

        return files;
    }

    private static String sendFile() throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                out.println("\nInsert Path to File:");

                String line;
                while ((line = in.readLine()) != null) {
                    String path = line;
                    int bytes = 0;
                    File file = new File(Server.getPathToFiles() + path);
                    FileInputStream fileInputStream = new FileInputStream(file);

                    dataOutputStream.writeLong(file.length());
                    byte[] buffer = new byte[4 * 1024];
                    while ((bytes = fileInputStream.read(buffer)) != -1) {
                        dataOutputStream.write(buffer, 0, bytes);
                        dataOutputStream.flush();
                    }
                    fileInputStream.close();
                    
                    
                    if (!in.ready())
                    break;

                    return "Sucessfully downloaded " + path;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception is Caught");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        return "Failed";
    }

    private static void receiveFile(String fileName) throws Exception {
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        long size = dataInputStream.readLong();
        byte[] buffer = new byte[4 * 1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes;
        }
        fileOutputStream.close();
    }
};
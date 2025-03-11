package socket.handlers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import socket.Server;
import socket.exceptions.FileCheckException;

public class ClientHandler implements Runnable {
    static Socket socket;
    static DataOutputStream dataOutputStream = null;

    public ClientHandler(Socket s) {
        socket = s;
        try {
            dataOutputStream = new DataOutputStream(s.getOutputStream());
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
        String response = "ERROR - Invalid Option";

        if (isPositiveInteger(option)) {
            Integer opt = Integer.parseInt(option);

            if (opt == 1) {
                response = listFiles();
            } else if (opt == 2) {
                response = sendFile();
            }
            ;
        }

        out.println(response);
    };

    public static String listFiles() {
        String[] pathnames;
        String files = "\n\n == Files ==\n\n";
        File f = new File(Server.getPathToFiles());
        pathnames = f.list();

        for (int i = 0; i < pathnames.length; i++) {
            files += (i) + " - " + pathnames[i] + "\n";
        }

        return files;
    }

    private static String sendFile() throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        FileInputStream fileInputStream = null;
        File file = null;
        File f = new File(Server.getPathToFiles());
        String[] pathnames = f.list();

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(listFiles());
            out.println("\nInsert File Index:");
            String line = in.readLine();

            if (!isPositiveInteger(line))
                throw new FileCheckException("Invalid Index");

            int fileIndex = Integer.parseInt(line);
            int bytes = 0;
            String path = pathnames[fileIndex];
            out.println(path); // Send name of file so client know how to store.
            out.flush();

            while (true) {
                file = new File(Server.getPathToFiles() + path);
                fileInputStream = new FileInputStream(file);

                dataOutputStream.writeLong(file.length());
                byte[] buffer = new byte[4 * 1024];
                while ((bytes = fileInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytes);
                    dataOutputStream.flush();
                }

                return "Sucessfully downloaded " + pathnames[fileIndex];
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new FileCheckException("File Not Identified");
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (fileInputStream != null)
                fileInputStream.close();
            dataOutputStream.close();
        }
        ;

        return "Failed";
    }

    public static boolean isPositiveInteger(String input) {
        try {
            int value = Integer.parseInt(input);
            if (value < 0)
                throw new NumberFormatException();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
};
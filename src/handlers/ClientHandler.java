package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    Socket socket;

    public ClientHandler(Socket s) {
        socket = s;
    };

    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.printf("Sent from the client: %s\n", line);
                handleReq(line, socket, out);
            }

            // DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            // String option = dataInputStream.readUTF();
            // dataInputStream.close();

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

    }

    public static void handleReq(String option, Socket socket, PrintWriter out) throws IOException {
        Map<Integer, String> options = new HashMap<>();
        options.put(1, "List Available Files");
        options.put(2, "Download File From Path");
        options.put(3, "Upload File From Path");
        options.put(4, "Close Connection");

        if (!options.containsValue(option))
            throw new Error("Invalid Option");

        if (option.equals(options.get(1))) {
            String response = listFiles();
            out.println(response);
        };
    };

    public static String listFiles() {
        String[] pathnames;
        String files = "Files: ";
        File f = new File("./src/files");
        pathnames = f.list();

        for (String pathname : pathnames) {
            files += pathname + " ";
        }

        return files;
    }
}

// // Main Class
// class Multithread {
// public static void main(String[] args) {
// int n = 8; // Number of threads
// for (int i = 0; i < n; i++) {
// Thread object = new Thread(new Handler());
// object.start();
// }
// }
// }
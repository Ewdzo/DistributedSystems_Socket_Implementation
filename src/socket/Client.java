package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static int port = 5000;
    private static String ip = "localhost";

    public static void main(String[] args) {
        try (Socket socket = new Socket(ip, port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner terminalInput = new Scanner(System.in);
            dataInputStream = new DataInputStream(socket.getInputStream());

            while (true) {
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                    if(response.equals("Insert Path to File:")) {
                        String input = terminalInput.nextLine();
                        out.println(input);
                        out.flush();
                        receiveFile("./src/downloads/" + input);
                    }
                    if(!in.ready()) break;

                }
                
                String input = terminalInput.nextLine();
                if(input.equals("4")) {
                    socket.close();
                    terminalInput.close();
                    return;
                };
                
                out.println(input);
                out.flush();
            }
        } catch (ConnectException e) {
            System.out.println("Couldn't connect to " + ip + ":" + port);
        } catch (Exception e) {

            e.printStackTrace();
        }
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
}
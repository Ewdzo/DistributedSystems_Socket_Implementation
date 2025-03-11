package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

import socket.exceptions.FileCheckException;

public class Client {
    static FileOutputStream fileOutputStream;
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
                    if (response.equals("Insert File Index:")) {
                        String input = terminalInput.nextLine();
                        out.println(input);
                        String filePath = in.readLine();

                        if (filePath != null)
                            receiveFile("./src/downloads/" + filePath);
                        else {
                            socket.close();
                            terminalInput.close();
                            if (fileOutputStream != null)
                                fileOutputStream.close();
                            throw new FileCheckException("File Not Identified");
                        }
                    }

                    if (!in.ready())
                        break;
                }

                String input = terminalInput.nextLine();
                if (input.equals("4")) {
                    socket.close();
                    terminalInput.close();
                    if (fileOutputStream != null)
                        fileOutputStream.close();
                    return;
                }
                ;

                out.println(input);

            }
        } catch (ConnectException e) {
            System.out.println("Couldn't connect to " + ip + ":" + port);
        } catch (FileCheckException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void receiveFile(String fileName) throws Exception {
        if (fileName.equals("./src/downloads/") || fileName.equals("./src/downloads/null"))
            throw new FileCheckException("File Not Valid");
        File f = new File(fileName);
        if (f.exists() && !f.isDirectory())
            throw new FileCheckException("File already exists");

        int bytes = 0;
        fileOutputStream = new FileOutputStream(fileName);
        long size = dataInputStream.readLong();
        byte[] buffer = new byte[4 * 1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes;
        }
        fileOutputStream.close();
    }
}
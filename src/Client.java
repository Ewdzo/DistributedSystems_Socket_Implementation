import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private static DataOutputStream dataOutputStream = null;
    static int port = 5000;
    static String ip = "localhost";
    static Map<Integer, String> options = new HashMap<>();

    public static void main(String[] args) {
        options.put(1, "List Available Files");
        options.put(2, "Download File From Path");
        options.put(3, "Upload File From Path");
        options.put(4, "Close Connection");

        try (Socket socket = new Socket("localhost", port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Scanner terminalInput = new Scanner(System.in);
            String line = null;
            while (true) {
                System.out.println("\n\n == File Manager ==\n");
                for (Integer key : options.keySet()) {
                    System.out.println(key + " - " + options.get(key));
                }

                String input = terminalInput.nextLine();

                line = handleOption(input);
                if (line.equals("Close Connection")) {
                    socket.close();
                    return;
                }

                out.println(line);
                out.flush();
                System.out.println("Server replied " + in.readLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String handleOption(String str) {
        Integer option = Integer.parseInt(str);

        if (!options.containsKey(option))
            throw new Error("Invalid Option - Pick From List");

        return options.get(option);

    }

    private static void sendFile(String path) throws Exception {
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeLong(file.length());
        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
}
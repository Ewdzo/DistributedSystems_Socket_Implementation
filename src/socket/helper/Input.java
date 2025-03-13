package socket.helper;

import java.util.Scanner;

public abstract class Input {
    public static String getInput() {
        Scanner terminalInput = new Scanner(System.in);

        String input = terminalInput.nextLine();

        // terminalInput.close();

        return input;
    }

    public static Integer getIntegerInput() {
        while (true) {
            try {
                String rawInput = getInput();
                Integer input = Integer.parseInt(rawInput);

                return input;
            } catch (Exception e) {
                System.out.println("400: Bad Request - Entrada Inválida");
            }
        }
    };
}

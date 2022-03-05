package listener;

import data.DataHandler;

import java.util.Locale;
import java.util.Scanner;

public class ConsoleListener extends Thread {

    final DataHandler dataHandler;

    public ConsoleListener(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        Thread.currentThread().setName("consoleListener");
    }

    @Override
    public void run() {
        listener();
    }

    public void listener() {
        while (dataHandler.getRUN()) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            input = input.toLowerCase(Locale.ROOT);

            //TODO Error Correction
            if (input.charAt(0) == ' ') {
                System.out.println("Fehler. Befehle staren niemals mit einem Leerzeichen");
            }

            String command = input.substring(0, input.indexOf(' '));
            String arguments = input.substring(input.indexOf(' ') + 1);

            switch (command) {
                //TODO
                case "shot": {

                }
                case "placeship", "ship", "place": {

                }
                case "help", "?": {

                }
            }
        }
    }
}

package main.listener;

import main.data.DataHandler;
import main.ressources.Exceptions;
import main.ressources.protocol.MessageEndpoint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Scanner;

import static main.ressources.DebugOut.debugOut;

public class ConsoleListener extends Thread {

    final DataHandler dataHandler;

    public ConsoleListener(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("ConsoleListener");
        listener();
    }

    public void listener() {
        try {
            while (dataHandler.getRUN()) {
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                input = input.toLowerCase(Locale.ROOT);

                //TODO Error Correction

                if (!input.isEmpty()) {

                    //Error Correction
                    input = input.toLowerCase();

                    while (input.contains("  ")) {
                        int pos = input.indexOf("  ");
                        input = input.substring(0, pos).concat(input.substring(pos + 1));
                    }
                    if (input.charAt(0) == ' ') {
                        input = input.substring(1);
                    }

                    //Splits Line at each Space and stores each Substring in an ArrayList
                    ArrayList<String> inputStrings = new ArrayList<>();
                    while (input.contains(" ")) {
                        inputStrings.add(input.substring(0, input.indexOf(" ")));
                        input = input.substring(input.indexOf(" ") + 1);
                    }
                    inputStrings.add(input);

                    //TODO Check if the Argumets are actually there
                    switch (inputStrings.get(0)) {
                        //TODO
                        case "shot": {
                            int x;
                            int y;
                            try {
                                x = Integer.parseInt(inputStrings.get(1));
                                y = Integer.parseInt(inputStrings.get(2));
                                if (x < 1 | x > 10 | y < 1 | y > 10) {
                                    throw new NumberFormatException();
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Eingabe Fehlerhaft. Koordinaten müssen .");
                                break;
                            }

                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("x", String.valueOf(x));
                            body.put("y", String.valueOf(y));
                            MessageEndpoint.sent("Shot-Request", body, dataHandler.getClient().socket());

                            break;
                        }
                        case "placeship", "ship", "place": {
                            int x;
                            int y;
                            int size;
                            String orientation;
                            try {
                                size = Integer.parseInt(inputStrings.get(1));
                                x = Integer.parseInt(inputStrings.get(2));
                                y = Integer.parseInt(inputStrings.get(3));
                                orientation = inputStrings.get(4);
                            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                System.out.println("Eingabe Fehlerhaft. Koordinaten müssen .");
                                break;
                            }

                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("size", String.valueOf(size));
                            body.put("x", String.valueOf(x));
                            body.put("y", String.valueOf(y));
                            body.put("orientation", orientation);
                            MessageEndpoint.sent("PlaceShip-Request", body, dataHandler.getClient().socket());

                            break;
                        }
                        case "debug": {
                            boolean bool = Boolean.parseBoolean(inputStrings.get(1));
                            dataHandler.setDebug(bool);
                            System.out.println("Debug Mode: " + bool);
                        }
                        case "help", "?": {

                            //TODO Help
                            break;
                        }
                        //TODO Default Error
                    }
                }
            }
        } catch (Exceptions.ConnectionResetByPeerException e) {
            debugOut("[Request] Lost Connection to the Server, disconnected");
            System.out.println("Verbindung zum Server verloren");
            dataHandler.setRUN(false);
        }
    }
}

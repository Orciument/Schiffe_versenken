package main.listener;

import main.data.Client;
import main.data.DataHandler;
import main.ressources.Display;
import main.ressources.Exceptions;
import main.ressources.Exceptions.MessageMissingArgumentsException;
import main.ressources.Exceptions.MessageProtocolVersionIncompatible;
import main.ressources.protocol.Message;
import main.ressources.protocol.MessageEndpoint;

import java.io.IOException;
import java.util.LinkedHashMap;

public class RequestListener extends Thread {

    final DataHandler dataHandler;
    final Client client;

    public RequestListener(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.client = dataHandler.getClient();
    }

    @Override
    public void run() {
        listener();
    }

    private void listener() {
        Thread.currentThread().setName("RequestListener");
        while (dataHandler.getRUN()) {
            Message message;
            try {
                message = MessageEndpoint.receive(client.socket().getInputStream());
            } catch (IOException | ClassNotFoundException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable");
                MessageEndpoint.sent("error", body, client.socket());
                e.printStackTrace();
                break;
            } catch (MessageProtocolVersionIncompatible e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "Message Protocol Version incompatible");
                MessageEndpoint.sent("error", body, client.socket());
                e.printStackTrace();
                break;
            }

            try {
                //Checking for required Data
                if (message.type() == null || message.body() == null || message.version() == null) {
                    throw new MessageMissingArgumentsException();
                }

                //Message Type switch
                switch (message.type()) {
                    case "error" -> {
                        //Checking for required Data
                        if (!message.body().containsKey("error")) {
                            throw new MessageMissingArgumentsException();
                        }

                        //Process Data
                        System.out.println("Error: " + message.body().get("error"));
                    }
                    case "Identification-Request" -> {
                        //Answer to Client
                        LinkedHashMap<String, String> body = new LinkedHashMap<>();
                        body.put("name", client.name());
                        MessageEndpoint.sent("Identification-Answer", body, client.socket());

                        Display.update();
                    }
                    case "PlaceShip-Answer" -> {
                        //Checking for required Data
                        if (!message.body().containsKey("success") || !message.body().containsKey("message")) {
                            throw new MessageMissingArgumentsException();
                        }

                        //Process Data
                        if (Boolean.parseBoolean(message.body().get("success"))) {
                            //Checking for required Data
                            if (!message.body().containsKey("size") || !message.body().containsKey("x") || !message.body().containsKey("y") || !message.body().containsKey("orientation")) {
                                throw new MessageMissingArgumentsException();
                            }
                            try {
                                client.addShip(Integer.parseInt(message.body().get("size")), Integer.parseInt(message.body().get("x")), Integer.parseInt(message.body().get("y")), message.body().get("orientation"));
                            } catch (Exceptions.ShipAlreadyThereException e) {
                                e.printStackTrace();
                            }

                            System.out.println("Schiff erfolgreich platziert!");
                        } else {
                            System.out.println("Schiff konnte nicht platter werden: " + message.body().get("message"));
                        }

                        Display.update();
                    }
                    case "Match-Start" -> {
                        //Process Data
                        dataHandler.setGameState(2);
                        System.out.println("Match beginnt...");

                        Display.update();
                    }
                    case "Shot-Answer" -> {
                        //Checking for required Data
                        if (!message.body().containsKey("success")) {
                            throw new MessageMissingArgumentsException();
                        }

                        //Process Data
                        if (Boolean.parseBoolean(message.body().get("success"))) {
                            //Checking for required Data
                            if (!message.body().containsKey("x") | !message.body().containsKey("y")) {
                                throw new MessageMissingArgumentsException();
                            }
                            client.addHit(Integer.parseInt(message.body().get("x")), Integer.parseInt(message.body().get("y")));
                            System.out.println("Schiff getroffen!");
                        } else {
                            System.out.println("Nichts getroffen :(");
                        }

                        Display.update();
                    }
                    case "Update-Display" -> {
                        //Checking for required Data
                        //TODO Change Data
                        if (!message.body().containsKey("type") || !message.body().containsKey("x") || !message.body().containsKey("y")) {
                            throw new MessageMissingArgumentsException();
                        }


                        //Prepare needed Data
                        //b ≠ y -> b==0 is at the top
                        int a = dataHandler.getClient().shipField()[0].length - Integer.parseInt(message.body().get("y"));
                        int b = Integer.parseInt(message.body().get("x")) - 1;

                        //Process Data
                        dataHandler.getClient().shipField()[a][b] = 'w';

                        Display.update();
                    }
                    case "Game-End" -> {
                        //Checking for required Data
                        if (!message.body().containsKey("winner")) {
                            throw new MessageMissingArgumentsException();
                        }

                        //Process Data
                        dataHandler.setGameState(3);
                        System.out.println("Spiel ist vorbei...");
                        System.out.println("\"" + message.body().get("winner") + "\" hat gewonnen!");
                    }
                }

            } catch (MessageMissingArgumentsException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable, or missing key Arguments");
                MessageEndpoint.sent("error", body, client.socket());
            }

        }
        try {
            client.socket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
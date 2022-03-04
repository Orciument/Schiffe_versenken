package listener;

import data.client;
import data.dataHandler;
import ressources.exceptions.MessageMissingArgumentsException;
import ressources.exceptions.MessageProtocolVersionIncompatible;
import ressources.protocol.Message;
import ressources.protocol.messageEndpoint;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

public class requestListener extends Thread {

    final dataHandler dataHandler;
    final client client;

    public requestListener(dataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.client = dataHandler.getClient();
    }

    @Override
    public void run() {
        listener();
    }

    private void listener() {
        Thread.currentThread().setName("Starting RequestListener");
        while (dataHandler.getRUN()) {
            DataInputStream dataInputStream;
            Message message;
            try {
                dataInputStream = new DataInputStream(client.socket().getInputStream());
                message = messageEndpoint.receive(dataInputStream);
            } catch (IOException | ClassNotFoundException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable");
                messageEndpoint.sent("error", body, client.socket());
                break;
            } catch (MessageProtocolVersionIncompatible e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "Message Protocol Version incompatible");
                messageEndpoint.sent("error", body, client.socket());
                break;
            }

            try {
                if (message.type() == null || message.body() == null || message.version() == null) {
                    throw new MessageMissingArgumentsException();
                }

                //Message Type switch
                switch (message.type()) {

                    case "error": {
                        //Checking for required Data
                        if (!message.body().containsKey("error")) {
                            throw new MessageMissingArgumentsException();
                        }

                        //Process Data
                        System.out.println("Error: " + message.body().get("error"));
                    }
                    case "Identification-Request": {
                        //Answer to Client
                        LinkedHashMap<String, String> body = new LinkedHashMap<>();
                        body.put("name", client.name());
                        messageEndpoint.sent("Identification-Answer", body, client.socket());
                    }
                    case "PlaceShip-Answer": {
                        //Checking for required Data
                        if (message.body().containsKey("success") || message.body().containsKey("message")) {
                            throw new MessageMissingArgumentsException();
                        }

                        //Process Data
                        if (Boolean.parseBoolean(message.body().get("success"))) {
                            System.out.println("Schiff erfolgreich Passiert");
                        } else {
                            System.out.println("Schiff konnte nicht platter werden: " + message.body().get("message"));
                        }
                    }
                    case "Match-Start": {
                        //Process Data
                        dataHandler.setGameState(2);
                        System.out.println("Match beginnt...");
                    }
                    case "Shot-Answer": {
                        //Checking for required Data
                        if (message.body().containsKey("success")) {
                            throw new MessageMissingArgumentsException();
                        }

                        //Process Data
                        if (Boolean.parseBoolean(message.body().get("success"))) {
                            System.out.println("Schiff getroffen!");
                        }
                        else {
                            System.out.println("Nichts getroffen :(");
                        }
                    }
                    case "Update-Display": {
                        //TODO Print booth fields
                    }
                    case "Game-End": {
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
                messageEndpoint.sent("error", body, client.socket());
            }

        }
        try {
            client.socket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
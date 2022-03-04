package listener;

import data.Client;
import data.DataHandler;
import ressources.Exceptions.MessageMissingArgumentsException;
import ressources.Exceptions.MessageProtocolVersionIncompatible;
import ressources.protocol.Message;
import ressources.protocol.MessageEndpoint;

import java.io.DataInputStream;
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
        Thread.currentThread().setName("Starting RequestListener");
        while (dataHandler.getRUN()) {
            DataInputStream dataInputStream;
            Message message;
            try {
                dataInputStream = new DataInputStream(client.socket().getInputStream());
                message = MessageEndpoint.receive(dataInputStream);
            } catch (IOException | ClassNotFoundException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable");
                MessageEndpoint.sent("error", body, client.socket());
                break;
            } catch (MessageProtocolVersionIncompatible e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "Message Protocol Version incompatible");
                MessageEndpoint.sent("error", body, client.socket());
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
                        break;
                    }

                    case "Identification-Request": {
                        //Answer to Client
                        LinkedHashMap<String, String> body = new LinkedHashMap<>();
                        body.put("name", client.name());
                        MessageEndpoint.sent("Identification-Answer", body, client.socket());
                        break;
                    }

                    case "PlaceShip-Answer": {
                        //Checking for required Data
                        if (!message.body().containsKey("success") || !message.body().containsKey("message")) {
                            throw new MessageMissingArgumentsException();
                        }

                        //Process Data
                        if (Boolean.parseBoolean(message.body().get("success"))) {
                            System.out.println("Schiff erfolgreich Passiert");
                        } else {
                            System.out.println("Schiff konnte nicht platter werden: " + message.body().get("message"));
                        }
                        break;
                    }

                    case "Match-Start": {
                        //Process Data
                        dataHandler.setGameState(2);
                        System.out.println("Match beginnt...");
                        break;
                    }

                    case "Shot-Answer": {
                        //Checking for required Data
                        if (!message.body().containsKey("success")) {
                            throw new MessageMissingArgumentsException();
                        }

                        //Process Data
                        if (Boolean.parseBoolean(message.body().get("success"))) {
                            System.out.println("Schiff getroffen!");
                        }
                        else {
                            System.out.println("Nichts getroffen :(");
                        }
                        break;
                    }

                    case "Update-Display": {
                        //TODO Print booth fields
                        break;
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
                        break;
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
package listener;

import data.client;
import data.dataHandler;
import ressources.exceptions.*;
import ressources.protocol.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

public class requestListener extends Thread {

    final dataHandler dataHandler;
    final client client;

    public requestListener(dataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.client = dataHandler.getClient();
        Thread.currentThread().setName("Starting RequestListener");
    }

    @Override
    public void run() {
        listener();
    }

    private void listener() {
        while (dataHandler.getRUN()) {
            DataInputStream dataInputStream;
            message message;
            try {
                dataInputStream = (DataInputStream) client.socket().getInputStream();
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

                    case "Error": {
                        if (!message.body().containsKey("error")) {
                            throw new MessageMissingArgumentsException();
                        }
                        System.out.println("Error: " + message.body().get("error"));
                    }
                    //TODO Add all message Types

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
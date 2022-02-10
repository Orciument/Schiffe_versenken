package listener;

import data.client;
import data.dataHandler;
import protocol.message;
import protocol.messageEndpoint;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.RejectedExecutionException;

public class requestListener extends Thread {

    final dataHandler dataHandler;
    final client client;

    public requestListener(dataHandler dataHandler, data.client client) {
        this.dataHandler = dataHandler;
        this.client = client;
        Thread.currentThread().setName("requestListener for Client: " + client.name());
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
                dataInputStream = (DataInputStream) client.Socket().getInputStream();
                message = messageEndpoint.receive(dataInputStream);
            } catch (IOException | ClassNotFoundException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable");
                messageEndpoint.sent("error", body, client.Socket());
                return;
            }


            System.out.println("Validate Metadata");
            System.out.println("Message Destination Address: " + message.destinationAddress());
            System.out.println("Server Address: " + dataHandler.getServerSocket().getLocalSocketAddress());
            try {
                switch (message.type()) {

                    //TODO Error
                    case "Error": {
                        System.out.println("Error: " + message.body().get("error"));
                    }

                    //TODO PlaceShip-Request
                    case "PlaceShip-Request": {
                        if (!message.body().containsKey("size") || !message.body().containsKey("x") || !message.body().containsKey("y") || !message.body().containsKey("direction")) {
                            throw new IllegalArgumentException();
                        }

                        if (dataHandler.getGamestate() == 1) {
                            int size = Integer.parseInt(message.body().get("size"));
                            if (client.currentShips()[size] + 1 < client.maxShips()[size]) {
                                //TODO Test if there is another ship in the way
                                try {
                                    client.addShip(size, Integer.parseInt(message.body().get("x")), Integer.parseInt(message.body().get("y")), message.body().get("direction"));
                                } catch (IllegalArgumentException e)
                                {
                                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                                    messageEndpoint.sent("error", body, client.Socket());
                                } catch (RejectedExecutionException e)
                                {
                                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                                    messageEndpoint.sent("error", body, client.Socket());
                                }


                            } else {
                                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                                body.put("error", "you are not allowed to place more ships of this type");
                                messageEndpoint.sent("error", body, client.Socket());
                            }
                        } else {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("error", "action not allowed at this moment");
                            messageEndpoint.sent("error", body, client.Socket());
                        }
                    }

                    //TODO messageReceived
                    case "messageReceived": {

                    }

                    //TODO Shot-Request
                    case "Shot-Request": {

                    }

                    //TODO UpdateDisplay-Answer
                    case "UpdateDisplay-Answer": {

                    }
                }
            } catch (IllegalArgumentException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable");
                messageEndpoint.sent("error", body, client.Socket());
                return;
            }

        }
    }
}

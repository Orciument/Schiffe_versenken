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

                    case "PlaceShip-Request": {
                        if (!message.body().containsKey("size") || !message.body().containsKey("x") || !message.body().containsKey("y") || !message.body().containsKey("direction")) {
                            throw new IllegalArgumentException();
                        }
                        if (dataHandler.getGamestate() != 1) {
                            throw new RejectedExecutionException();
                        }

                        int size = Integer.parseInt(message.body().get("size"));
                        if (client.currentShips()[size] >= client.maxShips()[size]) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("error", "you are not allowed to place more ships of this type");
                            messageEndpoint.sent("error", body, client.Socket());
                            break;
                        }

                        try {
                            client.addShip(size, Integer.parseInt(message.body().get("x")), Integer.parseInt(message.body().get("y")), message.body().get("direction"));
                        } catch (IllegalArgumentException e) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("error", "coordinates to place ship are wrongly defined");
                            messageEndpoint.sent("error", body, client.Socket());
                        } catch (RejectedExecutionException e) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("error", "cant place ship there, because there is already another ship there");
                            messageEndpoint.sent("error", body, client.Socket());
                        }
                    }

                    case "Shot-Request": {
                        if (!message.body().containsKey("x") || !message.body().containsKey("y")) {
                            throw new IllegalArgumentException();
                        }
                        if (dataHandler.getGamestate() != 2) {
                            throw new RejectedExecutionException();
                        }

                        client adversary = dataHandler.getOtherClient(client);
                        int[][] adversaryShipField = adversary.shipField();
                        //b ≠ y -> b==0 is at the top
                        int a = adversaryShipField[0].length - Integer.parseInt(message.body().get("y"));
                        int b = Integer.parseInt(message.body().get("x"));
                        b--;

                        if (adversaryShipField[a][b] == 0) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "false");
                            messageEndpoint.sent("Shot-Answer", body, client.Socket());
                        }
                        if (adversaryShipField[a][b] > 0 || adversaryShipField[a][b] <= 4) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "true");
                            messageEndpoint.sent("Shot-Answer", body, client.Socket());
                        }

                        //TODO Update-Displays
                        LinkedHashMap<String, String> body = new LinkedHashMap<>();

                        messageEndpoint.sent("Update-Display", body, adversary.clientSocket());

                    }
                }

            } catch (IllegalArgumentException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable");
                messageEndpoint.sent("error", body, client.Socket());
                return;
            } catch (RejectedExecutionException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "action not allowed at this moment");
                messageEndpoint.sent("error", body, client.Socket());
            }

        }
    }
}
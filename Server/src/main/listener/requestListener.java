package listener;

import data.client;
import data.dataHandler;
import resources.protocol.message;
import resources.protocol.messageEndpoint;
import resources.exceptions.*;

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
                break;
            } catch (MessageProtocolVersionIncompatible e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "Message Protocol Version incompatible");
                messageEndpoint.sent("error", body, client.Socket());
                break;
            }

            try {
                if (message.type() == null || message.body() == null || message.version() == null) {
                    throw new MessageMissingArgumentsException();
                }

                switch (message.type()) {

                    case "Error": {
                        if (!message.body().containsKey("error")) {
                            throw new MessageMissingArgumentsException();
                        }
                        System.out.println("Error: " + message.body().get("error"));
                    }

                    case "PlaceShip-Request": {
                        //Checking for required Data
                        if (!message.body().containsKey("size") || !message.body().containsKey("x") || !message.body().containsKey("y") || !message.body().containsKey("orientation")) {
                            throw new MessageMissingArgumentsException();
                        }
                        //Prepare needed Data
                        int size = Integer.parseInt(message.body().get("size"));

                        //Process Information and Answer to Client
                        if (dataHandler.getGamePhase() != 1) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "false");
                            body.put("message", "you are not allowed to place a ships in this state of the game");
                            messageEndpoint.sent("PlaceShip-Answer", body, client.Socket());
                            break;
                        }

                        if (client.currentShips()[size] >= client.maxShips()[size]) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "false");
                            body.put("message", "you are not allowed to place more ships of this type");
                            messageEndpoint.sent("PlaceShip-Answer", body, client.Socket());
                            break;
                        }

                        try {
                            client.addShip(size, Integer.parseInt(message.body().get("x")), Integer.parseInt(message.body().get("y")), message.body().get("orientation"));
                        } catch (IllegalArgumentException e) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("error", "coordinates to place ship are wrongly defined");
                            messageEndpoint.sent("error", body, client.Socket());
                        } catch (ShipAlreadyThereException e) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("error", "cant place ship there, because there is already another ship there");
                            messageEndpoint.sent("error", body, client.Socket());
                        }

                        //Check if the next phase of the game can start
                        if (dataHandler.allShipsPlaced())
                        {
                            dataHandler.setGamePhase(2);
                            messageEndpoint.sent("Match-Start", new LinkedHashMap<>(), client.clientSocket());
                            messageEndpoint.sent("Match-Start", new LinkedHashMap<>(), dataHandler.getOtherClient(client).clientSocket());
                        }
                    }

                    case "Shot-Request": {
                        //Checking for required Data
                        if (!message.body().containsKey("x") || !message.body().containsKey("y")) {
                            throw new MessageMissingArgumentsException();
                        }
                        if (dataHandler.getGamePhase() != 2) {
                            throw new ActionNotAllowedNow("Can't shot in this GamePhase");
                        }
                        if (!dataHandler.getIfClientHasTurn(client)) {
                            throw new RejectedExecutionException("Error: You are not on turn");
                        }

                        //Prepare needed Data
                        client adversary = dataHandler.getOtherClient(client);
                        char[][] adversaryShipField = adversary.shipField();
                        //b ≠ y -> b==0 is at the top
                        int a = adversaryShipField[0].length - Integer.parseInt(message.body().get("y"));
                        int b = Integer.parseInt(message.body().get("x"));
                        b--;

                        //Process Information and Answer to Client
                        if (adversaryShipField[a][b] != 'S') {
                            //Message to original Sender
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "false");
                            messageEndpoint.sent("Shot-Answer", body, client.Socket());

                        }
                        if (adversaryShipField[a][b] == 'S') {
                            client.setLives(client.lives()-1);
                            dataHandler.changeClientIndexHasTurn();

                            //Message to original Sender
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "true");
                            messageEndpoint.sent("Shot-Answer", body, client.Socket());

                            //Message to adversary
                            body = new LinkedHashMap<>();
                            body.put("type", "hit");
                            body.put("x", message.body().get("x"));
                            body.put("y", message.body().get("y"));
                            messageEndpoint.sent("Update-Display", body, adversary.clientSocket());
                        }

                        if (adversary.lives() <= 0) {
                            dataHandler.setGamePhase(3);

                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("winner", adversary.name());
                            messageEndpoint.sent("Game-End", body, client.Socket());
                            messageEndpoint.sent("Game-End", body, adversary.Socket());

                            System.out.println("Game Ended");
                            System.out.println("The winner is: "+ adversary.name());
                            dataHandler.setRUN(false);
                        }
                    }
                }

            } catch (MessageMissingArgumentsException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable, or missing key Arguments");
                messageEndpoint.sent("error", body, client.Socket());
            } catch (ActionNotAllowedNow e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "action not allowed at this moment");
                messageEndpoint.sent("error", body, client.Socket());
            }

        }
        try {
            client.clientSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
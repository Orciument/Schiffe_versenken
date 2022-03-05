package listener;

import data.Client;
import data.DataHandler;
import ressources.Exceptions.ActionNotAllowedNow;
import ressources.Exceptions.MessageMissingArgumentsException;
import ressources.Exceptions.MessageProtocolVersionIncompatible;
import ressources.Exceptions.ShipAlreadyThereException;
import ressources.protocol.Message;
import ressources.protocol.MessageEndpoint;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.RejectedExecutionException;

public class RequestListener extends Thread {

    final DataHandler dataHandler;
    final Client client;

    public RequestListener(DataHandler dataHandler, Client client) {
        this.dataHandler = dataHandler;
        this.client = client;
    }

    @Override
    public void run() {
        listener();
    }

    private void listener() {
        Thread.currentThread().setName("requestListener for Client: " + client.name());
        while (dataHandler.getRUN()) {
            DataInputStream dataInputStream;
            Message message;
            try {
                dataInputStream = new DataInputStream(client.clientSocket().getInputStream());
                message = MessageEndpoint.receive(dataInputStream);
            } catch (IOException | ClassNotFoundException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable");
                MessageEndpoint.sent("error", body, client.clientSocket());
                break;
            } catch (MessageProtocolVersionIncompatible e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "Message Protocol Version incompatible");
                MessageEndpoint.sent("error", body, client.clientSocket());
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
                            MessageEndpoint.sent("PlaceShip-Answer", body, client.clientSocket());
                            break;
                        }

                        if (client.currentShips()[size] >= client.maxShips()[size]) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "false");
                            body.put("message", "you are not allowed to place more ships of this type");
                            MessageEndpoint.sent("PlaceShip-Answer", body, client.clientSocket());
                            break;
                        }

                        try {
                            client.addShip(size, Integer.parseInt(message.body().get("x")), Integer.parseInt(message.body().get("y")), message.body().get("orientation"));
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "true");
                            body.put("message", "");
                            MessageEndpoint.sent("PlaceShip-Answer", body, client.clientSocket());
                        } catch (IllegalArgumentException e) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("error", "coordinates to place ship are wrongly defined");
                            MessageEndpoint.sent("error", body, client.clientSocket());
                        } catch (ShipAlreadyThereException e) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("error", "cant place ship there, because there is already another ship there");
                            MessageEndpoint.sent("error", body, client.clientSocket());
                        }

                        //Check if the next phase of the game can start
                        if (dataHandler.allShipsPlaced()) {
                            dataHandler.setGamePhase(2);
                            MessageEndpoint.sent("Match-Start", new LinkedHashMap<>(), client.clientSocket());
                            MessageEndpoint.sent("Match-Start", new LinkedHashMap<>(), dataHandler.getOtherClient(client).clientSocket());
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
                        Client adversary = dataHandler.getOtherClient(client);
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
                            MessageEndpoint.sent("Shot-Answer", body, client.clientSocket());

                        }
                        if (adversaryShipField[a][b] == 'S') {
                            client.setLives(client.lives() - 1);
                            dataHandler.changeClientIndexHasTurn();
                            adversaryShipField[a][b] = 'w';

                            //Message to original Sender
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "true");
                            MessageEndpoint.sent("Shot-Answer", body, client.clientSocket());

                            //Message to adversary
                            body = new LinkedHashMap<>();
                            body.put("type", "hit");
                            body.put("x", message.body().get("x"));
                            body.put("y", message.body().get("y"));
                            MessageEndpoint.sent("Update-Display", body, adversary.clientSocket());
                        }

                        if (adversary.lives() <= 0) {
                            dataHandler.setGamePhase(3);

                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("winner", adversary.name());
                            MessageEndpoint.sent("Game-End", body, client.clientSocket());
                            MessageEndpoint.sent("Game-End", body, adversary.clientSocket());

                            System.out.println("Game Ended");
                            System.out.println("The winner is: " + adversary.name());
                            dataHandler.setRUN(false);
                        }
                    }
                }

            } catch (MessageMissingArgumentsException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable, or missing key Arguments");
                MessageEndpoint.sent("error", body, client.clientSocket());
            } catch (ActionNotAllowedNow e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "action not allowed at this moment");
                MessageEndpoint.sent("error", body, client.clientSocket());
            }

        }
        try {
            client.clientSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
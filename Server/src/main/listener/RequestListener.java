package main.listener;

import main.data.Client;
import main.data.DataHandler;
import main.ressources.Exceptions.ActionNotAllowedNow;
import main.ressources.Exceptions.MessageMissingArgumentsException;
import main.ressources.Exceptions.MessageProtocolVersionIncompatible;
import main.ressources.Exceptions.ShipAlreadyThereException;
import main.ressources.protocol.Message;
import main.ressources.protocol.MessageEndpoint;

import java.io.IOException;
import java.util.HashMap;
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
        Thread.currentThread().setName("RequestListener for Client: " + client.name());
        while (dataHandler.getRUN()) {
            Message message;
            try {
                message = MessageEndpoint.receive(client.clientSocket().getInputStream());
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
                //Checking for required Data
                if (message.type() == null || message.body() == null || message.version() == null) {
                    throw new MessageMissingArgumentsException();
                }

                switch (message.type()) {
                    case "Error" -> {
                        //Checking for required Data
                        if (!message.body().containsKey("error")) {
                            throw new MessageMissingArgumentsException();
                        }
                        System.out.println("Error: " + message.body().get("error"));
                    }
                    case "PlaceShip-Request" -> {
                        //Checking for required Data
                        if (!message.body().containsKey("size") || !message.body().containsKey("x") || !message.body().containsKey("y") || !message.body().containsKey("orientation")) {
                            throw new MessageMissingArgumentsException();
                        }
                        //TODO Validate that all arguments are valid. No size = 5, no x = 11

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
                        //TODO Führt zu fehler wenn man 5 als size angibt, weil hier vor kein error checking ist
                        if (client.currentShips()[size - 1] >= client.maxShips()[size - 1]) {
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "false");
                            body.put("message", "you are not allowed to place more ships of this type");
                            MessageEndpoint.sent("PlaceShip-Answer", body, client.clientSocket());
                            break;
                        }

                        try {
                            client.addShip(size, Integer.parseInt(message.body().get("x")), Integer.parseInt(message.body().get("y")), message.body().get("orientation"));

                            //Reuse the old body, because the client doesn't save where to place to ship
                            HashMap<String, String> body = message.body();
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
                        if (dataHandler.getClientCount() == 2 && dataHandler.allShipsPlaced()) {
                            dataHandler.setGamePhase(2);
                            MessageEndpoint.sent("Match-Start", new LinkedHashMap<>(), client.clientSocket());
                            MessageEndpoint.sent("Match-Start", new LinkedHashMap<>(), dataHandler.getOtherClient(client).clientSocket());
                        }
                    }
                    case "Shot-Request" -> {
                        //Checking for required Data
                        if (!message.body().containsKey("x") || !message.body().containsKey("y")) {
                            throw new MessageMissingArgumentsException();
                        }
                        if (dataHandler.getGamePhase() != 2) {
                            throw new ActionNotAllowedNow("Can't shot in this GamePhase");
                        }
                        if (!dataHandler.getIfClientHasTurn(client)) {
                            throw new RejectedExecutionException("Error: You are not on turn");
                            //TODO Catch this exception
                        }

                        //Prepare needed Data
                        Client adversary = dataHandler.getOtherClient(client);
                        char[][] adversaryShipField = adversary.shipField();
                        //b ≠ y -> b==0 is at the top
                        int a = adversaryShipField[0].length - Integer.parseInt(message.body().get("y"));
                        int b = Integer.parseInt(message.body().get("x"));
                        b--;

                        //Process Information and Answer to Client
                        //Shot is valid but missed
                        if (adversaryShipField[a][b] != 'S') {
                            //Message to original Sender
                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("success", "false");
                            MessageEndpoint.sent("Shot-Answer", body, client.clientSocket());
                            dataHandler.changeClientIndexHasTurn();

                        }
                        //Shot is valid and hit
                        if (adversaryShipField[a][b] == 'S') {
                            System.out.println("vorher" + client.lives());
                            adversary.setLives(adversary.lives() - 1);
                            System.out.println("nachher" + client.lives());
                            dataHandler.changeClientIndexHasTurn();
                            adversaryShipField[a][b] = 'w';

                            //Message to original Sender
                            //Reuse the old body, because the client doesn't save where it has shot
                            HashMap<String, String> body = message.body();
                            body.put("success", "true");
                            MessageEndpoint.sent("Shot-Answer", body, client.clientSocket());

                            //Message to adversary
                            body = new LinkedHashMap<>();
                            body.put("type", "hit");
                            body.put("x", message.body().get("x"));
                            body.put("y", message.body().get("y"));
                            MessageEndpoint.sent("Update-Display", body, adversary.clientSocket());
                        }

                        //Game should be over now, because all ships are completely destroyed
                        if (adversary.lives() <= 0) {
                            dataHandler.setGamePhase(3);

                            LinkedHashMap<String, String> body = new LinkedHashMap<>();
                            body.put("winner", client.name().toString());
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
            } catch(RejectedExecutionException e) {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", e.getLocalizedMessage());
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
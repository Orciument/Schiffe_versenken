package main.listener;

import main.data.Client;
import main.data.DataHandler;
import main.ressources.Display;
import main.ressources.Exceptions.*;
import main.ressources.protocol.Message;
import main.ressources.protocol.MessageEndpoint;

import java.io.IOException;
import java.util.LinkedHashMap;

import static main.ressources.DebugOut.debugOut;

public class RequestListener extends Thread {

    final DataHandler dataHandler;
    final Client client;
    boolean stop = false;

    public RequestListener(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.client = dataHandler.getClient();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("RequestListener");
        listener();
    }

    private void listener() {
        try {
            while (dataHandler.getRUN() && !stop) {
                Message message;
                try {
                    //Parsing of the Message once something has arrives through the Socket InputStream
                    message = MessageEndpoint.receive(client.socket().getInputStream());


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

                            if (message.body().get("error").equals("no more than 2 Clients allowed")) {
                                dataHandler.setRUN(false);
                            }
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
                                } catch (ShipAlreadyThereException e) {
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
                            dataHandler.setGamePhase(2);
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
                            if (!message.body().containsKey("type") || !message.body().containsKey("x") || !message.body().containsKey("y")) {
                                throw new MessageMissingArgumentsException();
                            }

                            //Process Data
                            client.addWreck(Integer.parseInt(message.body().get("x")), Integer.parseInt(message.body().get("y")));

                            Display.update();
                        }

                        case "Game-End" -> {
                            //Checking for required Data
                            if (!message.body().containsKey("winner")) {
                                throw new MessageMissingArgumentsException();
                            }

                            //Process Data
                            dataHandler.setGamePhase(3);
                            System.out.println("Spiel ist vorbei...");
                            System.out.println("\"" + message.body().get("winner") + "\" hat gewonnen!");
                        }
                    }

                } catch (MessageMissingArgumentsException e) {
                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                    body.put("error", "message unreadable, or missing key Arguments");
                    MessageEndpoint.sent("error", body, client.socket());
                } catch (IOException | ClassNotFoundException e) {
                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                    body.put("error", "message unreadable");
                    MessageEndpoint.sent("error", body, client.socket());
                    debugOut("Verwerfe Nachricht von \"" + client.name() + "\" da die Nachricht unlesbar ist");
                } catch (MessageProtocolVersionIncompatible e) {
                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                    body.put("error", "Message Protocol Version incompatible");
                    MessageEndpoint.sent("error", body, client.socket());
                    debugOut("Schließe Verbindung zu \"" + client.name() + "\" da die Versionen nicht kompatibel sind");
                    break;
                }

            }

            client.socket().close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConnectionResetByPeerException e) {
            debugOut("[Request] Lost Connection to Client :" + client.name());
            dataHandler.setRUN(false);
            stop = true;
        }
    }
}
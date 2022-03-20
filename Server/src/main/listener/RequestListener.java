package main.listener;

import main.data.Client;
import main.data.DataHandler;
import main.ressources.Exceptions.*;
import main.ressources.protocol.Message;
import main.ressources.protocol.MessageEndpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.RejectedExecutionException;

import static main.ressources.DebugOut.debugOut;

public class RequestListener extends Thread {

    final DataHandler dataHandler;
    //Client für den der RequestListener verantwortlich ist
    final Client client;
    boolean stop = false; //True wenn Thread gestoppt werden soll

    public RequestListener(DataHandler dataHandler, Client client) {
        this.dataHandler = dataHandler;
        this.client = client;
    }

    //Started einen neuen Thread
    @Override
    public void run() {
        Thread.currentThread().setName("RequestListener for Client: " + client.name());
        listener();
    }

    private void listener() {
        try {
            //Genutzt, um den Thread zu beenden. dataHandler.getRUN ist global um alle Threads zu beenden und stop um diesen einzelnen Thread zu beenden
            while (dataHandler.getRUN() && !stop) {
                Message message;
                try {
                    //Eine neue Nachricht aus dem InputStream des Sockets lesen
                    message = MessageEndpoint.receive(client.clientSocket().getInputStream());
                } catch (IOException | ClassNotFoundException e) {
                    //Wenn die Nachricht nicht aus dem InputStream gelesen werden kann, oder ein anderer Fehler auftritt wird ein Fehler an den Client gesendet
                    HashMap<String, String> body = new HashMap<>();
                    body.put("error", "message unreadable");
                    MessageEndpoint.sent("error", body, client.clientSocket());
                    //Started die Funktion erneut im gleichen Thread damit nur die eine fehlerhafte Nachricht verworfen wird
                    listener();
                    return;
                } catch (MessageProtocolVersionIncompatible e) {
                    //Wenn die Protokollversionen inkompatibel sind, wird wieder ein Fehler gesendet und die Verbindung getrennt, indem die Schleife verlassen wird
                    HashMap<String, String> body = new HashMap<>();
                    body.put("error", "Message Protocol version incompatible");
                    MessageEndpoint.sent("error", body, client.clientSocket());
                    break;
                }

                try {
                    //Teste, ob die Nachricht grundlegend Valide ist, um den Inhalt prüfen zu können
                    if (message.type() == null || message.body() == null || message.version() == null) {
                        throw new MessageMissingArgumentsException();
                    }

                    //Switch prüft um was für eine Nachricht es sich handelt, da sich die benötigten Informationen je nach Typ unterscheiden
                    switch (message.type()) {
                        //Wenn die Nachricht vom Typ Error ist, wird der Inhalt über den Standard Output Stream ausgegeben
                        case "Error" -> {
                            //Tested, ob die body Hashmap alle benötigten Key-Value Pairs besitzt
                            if (!message.body().containsKey("error")) {
                                throw new MessageMissingArgumentsException();
                            }
                            System.out.println("Error: " + message.body().get("error"));
                        }

                        //Behandelt anfragen vom Client ein Schiff zu platzieren
                        case "PlaceShip-Request" -> {
                            //Tested, ob die body Hashmap alle benötigten Key-Value Pairs besitzt
                            if (!message.body().containsKey("size") || !message.body().containsKey("x") || !message.body().containsKey("y") || !message.body().containsKey("orientation")) {
                                throw new MessageMissingArgumentsException();
                            }

                            //Sammel Daten aus der Hashmap und speichere sie in Variablen als ihre Datentypen
                            int size = Integer.parseInt(message.body().get("size"));
                            int x = Integer.parseInt(message.body().get("x"));
                            int y = Integer.parseInt(message.body().get("y"));
                            String orientation = message.body().get("orientation");

                            //Teste ob Daten in den erwarteten Werte Bereich fallen
                            //X, Y und die Blickrichtung müssen nicht Validiert werden, da sie nur in einer Methode gebraucht werden und diese selber die Daten auf Fehler testet
                            if (size < 1 || size > 4) {
                                throw new RejectedExecutionException("Ship size musst be between 1-4");
                            }

                            //Verarbeitung der Daten

                            //Schiffe dürfen nur in der ersten Phase des Spiels platziert werden, wenn die Phase also eine andere ist, wird dem Client geantwortet das er kein Schiff platzieren darf und der Switch verlassen und somit die Nachricht verworfen
                            if (dataHandler.getGamePhase() != 1) {
                                HashMap<String, String> body = new HashMap<>();
                                body.put("success", "false");
                                body.put("message", "you are not allowed to place a ships in this state of the game");
                                MessageEndpoint.sent("PlaceShip-Answer", body, client.clientSocket());
                                break;
                            }

                            //Wenn der Client alle Schiffe der ausgewählten größe schon platziert hat, wird ihm wieder seine Anfrage verneint und die Nachricht verworfen
                            if (client.currentShips()[size - 1] >= client.maxShips()[size - 1]) {
                                HashMap<String, String> body = new HashMap<>();
                                body.put("success", "false");
                                body.put("message", "you are not allowed to place more ships of this type");
                                MessageEndpoint.sent("PlaceShip-Answer", body, client.clientSocket());
                                break;
                            }

                            try {
                                //Wenn die vorherigen Bedingungen nicht erfüllt wurden, ist das Platzieren eines Schiffes erlaubt
                                client.addShip(size, x, y, orientation);

                                //Der alte body wird wiederverwendet da das Protokoll keine Sessions unterstützt und somit die Informationen wo das Schiff platziert werden muss braucht
                                HashMap<String, String> body = message.body();
                                body.put("success", "true");
                                body.put("message", "");
                                MessageEndpoint.sent("PlaceShip-Answer", body, client.clientSocket());
                            } catch (IllegalArgumentException e) {
                                //Wird von der addShip Methode im Client geworfen, wenn die angegebenen Parameter nicht Valide sind
                                HashMap<String, String> body = new HashMap<>();
                                body.put("error", "coordinates, or direction to place ship are wrongly defined");
                                MessageEndpoint.sent("error", body, client.clientSocket());
                            } catch (ShipAlreadyThereException e) {
                                //Wird von der addShip Methode im Client geworfen, wenn an einer Stelle wo das Schiff hin soll, schon ein anderes Schiff ist
                                HashMap<String, String> body = new HashMap<>();
                                body.put("error", "cant place ship there, because there is already another ship there");
                                MessageEndpoint.sent("error", body, client.clientSocket());
                            }

                            //Testet, ob bereits alle Schiffe platziert werden, und zwei Clients in der Runde sind. Wenn das Wahr ist, wird die nächste Phase der Runde eingeleitet
                            if (dataHandler.getClientCount() == 2 && dataHandler.allShipsPlaced()) {
                                dataHandler.setGamePhase(2);
                                MessageEndpoint.sent("Match-Start", new HashMap<>(), client.clientSocket());
                                MessageEndpoint.sent("Match-Start", new HashMap<>(), dataHandler.getOtherClient(client).clientSocket());
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
                                HashMap<String, String> body = new HashMap<>();
                                body.put("success", "false");
                                MessageEndpoint.sent("Shot-Answer", body, client.clientSocket());
                                dataHandler.changeClientIndexHasTurn();

                            }
                            //Shot is valid and hit
                            if (adversaryShipField[a][b] == 'S') {
                                adversary.setLives(adversary.lives() - 1);
                                dataHandler.changeClientIndexHasTurn();
                                adversaryShipField[a][b] = 'w';

                                //Message to original Sender
                                //Reuse the old body, because the client doesn't save where it has shot
                                HashMap<String, String> body = message.body();
                                body.put("success", "true");
                                MessageEndpoint.sent("Shot-Answer", body, client.clientSocket());

                                //Message to adversary
                                body = new HashMap<>();
                                body.put("type", "hit");
                                body.put("x", message.body().get("x"));
                                body.put("y", message.body().get("y"));
                                MessageEndpoint.sent("Update-Display", body, adversary.clientSocket());
                            }

                            //Game should be over now, because all ships are completely destroyed
                            if (adversary.lives() <= 0) {
                                dataHandler.setGamePhase(3);

                                HashMap<String, String> body = new HashMap<>();
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
                    //Wird geworfen, wenn die body Hashmap nicht alle benötigten Key-Value Pairs besitzt. Sendet dann einen Fehler an den Client
                    HashMap<String, String> body = new HashMap<>();
                    body.put("error", "message unreadable, or missing key Arguments");
                    MessageEndpoint.sent("error", body, client.clientSocket());
                } catch (ActionNotAllowedNow e) {
                    //Wird geworfen, wenn die vom Client angeforderte Acton an der stelle in der Runde nicht erlaubt ist
                    HashMap<String, String> body = new HashMap<>();
                    body.put("error", "action not allowed at this moment");
                    MessageEndpoint.sent("error", body, client.clientSocket());
                } catch (RejectedExecutionException e) {
                    //Genereller Fehler, Nachricht in der Exception wird weiter an den Client gesendet
                    HashMap<String, String> body = new HashMap<>();
                    body.put("error", e.getLocalizedMessage());
                    MessageEndpoint.sent("error", body, client.clientSocket());
                }

            }
            //Wenn der Thread gestoppt werden soll, wird vom beenden noch das Socket geschlossen
            client.clientSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Falls irgendwann die Verbindung zum Client verloren wird, wird diese Exception, beim Empfangen oder Senden einer Nachricht geworfen
        catch (ConnectionResetByPeerException e) {
            debugOut("[Request] Lost Connection to Client, disconnected");
            //Wenn das Spiel noch in der anfangs Phase ist, wird der Client aus der Client Liste entfernt und der eigene Thread gestoppt, ansonsten wird das ganze Programm beendet, da das Spiel nicht mehr weiter gehen kann
            if (dataHandler.getGamePhase() <= 1) {
                dataHandler.removeClient(client);
                stop = true;
            } else {
                debugOut("Game has already started, stopping the game");
                dataHandler.setRUN(false);
            }
        }
    }
}
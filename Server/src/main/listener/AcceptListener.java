package main.listener;

import main.data.*;
import main.ressources.Exceptions.ConnectionResetByPeerException;
import main.ressources.Exceptions.MessageMissingArgumentsException;
import main.ressources.Exceptions.MessageProtocolVersionIncompatible;
import main.ressources.protocol.*;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;

import static main.ressources.DebugOut.debugOut;

public class AcceptListener extends Thread {
    private final DataHandler dataHandler;

    public AcceptListener(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        Thread.currentThread().setName("acceptListener");
    }

    @Override
    public void run() {
        listener();
    }

    private void listener() {
        while (dataHandler.getRUN()) {
            try {
                Socket newClientSocket = dataHandler.getServerSocket().accept();
                debugOut("[ACCEPT] Client wants to Join, sent Identification Request now");
                MessageEndpoint.sent("Identification-Request", new LinkedHashMap<>(), newClientSocket);
                waitForIdentificationAnswer(newClientSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForIdentificationAnswer(Socket socket) {
        new Thread(() -> {

            Message message;
            try {

                try {
                    //Get the Message from the newly connected client Socket
                    message = MessageEndpoint.receive(socket.getInputStream());

                    //If the game has already startet, the client isn't allowed to join the game and the connection is abandoned
                    if (dataHandler.getGamePhase() != 1) {
                        LinkedHashMap<String, String> body = new LinkedHashMap<>();
                        body.put("error", "action not allowed at this moment");
                        MessageEndpoint.sent("error", body, socket);
                        debugOut("[Accept] Rejected Client because game has already startet: " + socket.getRemoteSocketAddress());
                        return;
                    } else if (dataHandler.getClientCount() >= 2) {
                        LinkedHashMap<String, String> body = new LinkedHashMap<>();
                        body.put("error", "no more than 2 Clients allowed");
                        MessageEndpoint.sent("error", body, socket);
                        debugOut("[Accept] Rejected Client because game has already 2 Clients: " + socket.getRemoteSocketAddress());
                        return;
                    }

                    //Check if the required Data is in the message
                    if (!message.body().containsKey("name")) {
                        throw new MessageMissingArgumentsException();
                    }

                    //Otherwise, a new client is added to the database
                    Client newClient = new Client(socket, message.body().get("name"));
                    dataHandler.addClient(newClient);
                    new RequestListener(dataHandler, newClient).start();

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                    body.put("error", "message unreadable");
                    MessageEndpoint.sent("error", body, socket);
                } catch (MessageProtocolVersionIncompatible e) {
                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                    body.put("error", "Message Protocol Version incompatible");
                    MessageEndpoint.sent("error", body, socket);
                } catch (MessageMissingArgumentsException e) {
                    //When the new Client sends Invalid information that cannot be decoded the connection is abandoned
                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                    body.put("error", "message unreadable, or missing key Arguments");
                    MessageEndpoint.sent("error", body, socket);
                    debugOut("[Accept] Failed to complete the connection for: " + socket.getRemoteSocketAddress());
                }
            } catch (ConnectionResetByPeerException e) {
                debugOut("[Accept] Lost Connection to new Client, ");
            }
        }, "AcceptListener: waitForIdentificationAnswer: " + socket.getRemoteSocketAddress()).start();
    }
}
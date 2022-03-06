package listener;

import data.Client;
import data.DataHandler;
import ressources.protocol.Message;
import ressources.protocol.MessageEndpoint;
import ressources.Exceptions.MessageMissingArgumentsException;
import ressources.Exceptions.MessageProtocolVersionIncompatible;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;

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
                System.out.println("[ACCEPT] Client wants to Join, sent Identification Request now");
                MessageEndpoint.sent("Identification-Request", new LinkedHashMap<>(), newClientSocket);
                waitForIdentificationAnswer(newClientSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForIdentificationAnswer(Socket socket) {
        new Thread(() -> {

            DataInputStream dataInputStream;
            Message message;
            try {
                //Get the Message from the newly connected client Socket
                dataInputStream = new DataInputStream(socket.getInputStream());
                message = MessageEndpoint.receive(dataInputStream);

                //If the game has already startet, the client isn't allowed to join the game and the connection is abandoned
                if (dataHandler.getGamePhase() != 1) {
                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                    body.put("error", "action not allowed at this moment");
                    MessageEndpoint.sent("error", body, socket);
                    System.out.println("Rejected Client because game has already startet: " + socket.getRemoteSocketAddress());
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
                body.put("error", "cant read message, fatal error");
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
                System.out.println("Failed to complete the connection for: " + socket.getRemoteSocketAddress());
            }
        }, "Thread: waitForIdentificationAnswer: " + socket.getRemoteSocketAddress()).start();
    }
}
package listener;

import data.client;
import data.dataHandler;
import protocol.message;
import protocol.messageEndpoint;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;

public class acceptListener extends Thread {
    private final dataHandler dataHandler;

    public acceptListener(dataHandler dataHandler) {
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
                System.out.println("[ACCEPT] Client wants to Join, sent Identification Request");
                messageEndpoint.sent("Identification-Request", new LinkedHashMap<>(), newClientSocket);
                waitForIdentificationAnswer(newClientSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForIdentificationAnswer(Socket socket) {
        new Thread(() -> {

            DataInputStream dataInputStream;
            message message;
            try {
                //Get the Message from the newly connected client Socket
                dataInputStream = new DataInputStream(socket.getInputStream());
                message = messageEndpoint.receive(dataInputStream);

                //If the game has already startet, the client isn't allowed to join the game and the connection is abandoned
                if (dataHandler.getGamePhase() != 1) {
                    LinkedHashMap<String, String> body = new LinkedHashMap<>();
                    body.put("error", "action not allowed at this moment");
                    messageEndpoint.sent("error", body, socket);
                    System.out.println("Rejected Client because game has already startet: " + socket.getRemoteSocketAddress());
                    return;
                }

                //Check if the required Data is in the message
                if (!message.body().containsKey("name")) {
                    throw new IllegalArgumentException();
                }

                //Otherwise, a new client is added to the database
                client newClient = new client(socket, message.body().get("name"));
                dataHandler.addClient(newClient);
                new requestListener(dataHandler, newClient);

            } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {

                //When the new Client sends Invalid information that cannot be decoded the connection is abandoned
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                body.put("error", "message unreadable, or missing key Arguments");
                messageEndpoint.sent("error", body, socket);
                System.out.println("Failed to complete the connection for: " + socket.getRemoteSocketAddress());
                return;
            }
        }, "Thread: waitForIdentificationAnswer: " + socket.getRemoteSocketAddress()).start();
    }
}
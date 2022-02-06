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
    final dataHandler dataHandler;

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
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                messageEndpoint.sent("Identification Request", body, newClientSocket);
                waitForIdentificationAnswer(newClientSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForIdentificationAnswer(Socket socket) {
        new Thread(() -> {

            DataInputStream dataInputStream;
            message message = null;
            try {
                //Get the Message from the newly connected client Socket
                dataInputStream = (DataInputStream) socket.getInputStream();
                message = messageEndpoint.receive(dataInputStream);

            } catch (IOException | ClassNotFoundException e) {

                //When the new Client sends Invalid information that cannot be decoded the connection is abandoned
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                //TODO Add Error Code to Body - message unreadable
                body.put("answerTo", String.valueOf(message.hashCode()));

                messageEndpoint.sent("Error", body, socket);
                System.out.println("Failed to complete the connection for: "+ socket.getRemoteSocketAddress());
                return;
            }

            if (dataHandler.getGamestate() != 1)
            {
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                //TODO Add Error Code to Body - not allowed to join at this stage
                body.put("answerTo", String.valueOf(message.hashCode()));

                messageEndpoint.sent("Error", body, socket);
                System.out.println("Rejected Client because game has already startet: "+ socket.getRemoteSocketAddress());
                return;
            }

            //Otherwise, a new client is added to the database
            client newClient = new client(socket, message.body().get("name"));
            dataHandler.addClient(newClient);
            new requestListener(dataHandler, newClient);
            messageEndpoint.sent("messageReceived", new LinkedHashMap<>(), newClient.Socket());

        }, "Thread: waitForIdentificationAnswer: " + socket.getRemoteSocketAddress()).start();
    }
}
package listener;

import data.client;
import data.dataHandler;
import protocol.message;
import protocol.messageEndpoint;

import java.io.DataInputStream;
import java.io.IOException;

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
                return;
            }

            System.out.println("Validate Metadata");
            System.out.println("Message Destination Address: " + message.destinationAddress());
            System.out.println("Server Address: " + dataHandler.getServerSocket().getLocalSocketAddress());
            switch (message.type()) {
                case "Error": {
                    //TODO Error
                }
                case "PlaceShip-Request": {
                    //TODO PlaceShip-Request
                }
                case "messageReceived": {
                    //TODO messageReceived
                }
                case "Shot-Request": {
                    //TODO Shot-Request
                }
                case "UpdateDisplay-Answer": {
                    //TODO UpdateDisplay-Answer
                }
            }
        }
    }
}

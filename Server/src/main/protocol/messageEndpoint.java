package protocol;

import data.dataHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class messageEndpoint {
    static final ArrayList<messagePackage> messageList = new ArrayList<>();
    static dataHandler dataHandler;
    static final String version = "0.1";

    public messageEndpoint(dataHandler dataHandler) {
        messageEndpoint.dataHandler = dataHandler;

        new Thread(() -> {

            while (dataHandler.getRUN()) {
                if (messageList.size() > 0) {
                    for (messagePackage messagePackage : messageList) {
                        //Wenn das Senden der Nachricht über 10 Sekunden her ist, wird die nachricht erneut gesendet
                        if (messagePackage.lastRetry + 10000 >= System.currentTimeMillis()) {
                            if (messagePackage.retriesCounter < 3) {
                                retry(messagePackage.message, messagePackage.socket);
                            } else {
                                //Wenn das erneut Senden 3-mal fehlgeschlagen hat, wird die Verbindung gekappt, und das Spiel wird geschlossen
                                dataHandler.setRUN(false);
                                try {
                                    messagePackage.socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                }
            }

        }).start();
    }

    public static void sent(String type, LinkedHashMap<String, String> body, Socket socket) {

        InetSocketAddress sourceAddress = getOwnInet4Address(dataHandler.getServerSocket().getLocalPort());
        InetSocketAddress destinationAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

        message message = new message(version, type, new Date(System.currentTimeMillis()), sourceAddress, destinationAddress, body);

        //Add to messageList, for keeping track of send messages
        messageList.add(new messagePackage(message, message.hashCode(), 0, System.currentTimeMillis(), socket));
        //Sent message to the Client
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void retry(message message, Socket socket) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static message receive(DataInputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (protocol.message) objectInputStream.readObject();
    }

    private static InetSocketAddress getOwnInet4Address(int port) {
        try {
            String hostname = String.valueOf(Inet4Address.getLocalHost());
            hostname = hostname.substring(hostname.indexOf("/"));
            return new InetSocketAddress(hostname.substring(1), port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static class messagePackage {
        private final protocol.message message;
        private final int hashcode;
        private final Socket socket;
        private int retriesCounter;
        private long lastRetry;

        public messagePackage(protocol.message message, int hashcode, int retriesCounter, long lastRetry, Socket socket) {
            this.message = message;
            this.hashcode = hashcode;
            this.retriesCounter = retriesCounter;
            this.lastRetry = lastRetry;
            this.socket = socket;
        }

        public protocol.message getMessage() {
            return message;
        }

        public int getHashcode() {
            return hashcode;
        }

        public int getRetriesCounter() {
            return retriesCounter;
        }

        public long getLastRetry() {
            return lastRetry;
        }

        public Socket getSocket() {
            return socket;
        }

        public void setRetriesCounter(int retriesCounter) {
            this.retriesCounter = retriesCounter;
        }

        public void setLastRetry(long lastRetry) {
            this.lastRetry = lastRetry;
        }
    }
}
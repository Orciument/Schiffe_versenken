package protocol;

import data.dataHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class messageEndpoint {
    //TODO Should save messages and handels Retries
    static final ArrayList<messagePackage> messageList = new ArrayList<>();
    static dataHandler dataHandler;
    static final String version = "0.1";

    public messageEndpoint(dataHandler dataHandler) {
        messageEndpoint.dataHandler = dataHandler;

        new Thread(() -> {
            while (dataHandler.getRUN()) {
                if (messageList.size() > 0) {
                    //TODO Loop throw the einträge
                }
            }
        }).start();
    }

    public static void sent(String type, LinkedHashMap<String, String> body, Socket socket) throws IOException {

        InetSocketAddress sourceAddress = getOwnInet4Address(dataHandler.getServerSocket().getLocalPort());
        InetSocketAddress destinationAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

        message message = new message(version, type, new Date(System.currentTimeMillis()), sourceAddress, destinationAddress, body);

        //Add to messageList, for keeping track of send messages
        messageList.add(new messagePackage(message, message.hashCode(), 0, System.currentTimeMillis()));
        //Sent message to the Client
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(message);
    }

    @SuppressWarnings("SameReturnValue")
    public static message receive(DataInputStream inputStream) {
        return null;
    }

    static InetSocketAddress getOwnInet4Address(int port) {
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
        private int retriesCounter;
        private long lastRetry;

        public messagePackage(protocol.message message, int hashcode, int retriesCounter, long lastRetry) {
            this.message = message;
            this.hashcode = hashcode;
            this.retriesCounter = retriesCounter;
            this.lastRetry = lastRetry;
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

        public void setRetriesCounter(int retriesCounter) {
            this.retriesCounter = retriesCounter;
        }

        public void setLastRetry(long lastRetry) {
            this.lastRetry = lastRetry;
        }
    }
}
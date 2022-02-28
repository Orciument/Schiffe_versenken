package resources.protocol;

import data.dataHandler;
import resources.exceptions.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedHashMap;

public class messageEndpoint {
    static dataHandler dataHandler;
    static final String version = "0.1";

    public messageEndpoint(dataHandler dataHandler) {
        messageEndpoint.dataHandler = dataHandler;
    }

    public static void sent(String type, LinkedHashMap<String, String> body, Socket socket) {

        InetSocketAddress sourceAddress = getOwnInet4Address(dataHandler.getServerSocket().getLocalPort());
        InetSocketAddress destinationAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

        message message = new message(version, type, new Date(System.currentTimeMillis()), sourceAddress, destinationAddress, body);

        //Sent message to the Client
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static message receive(DataInputStream inputStream) throws IOException, ClassNotFoundException, MessageProtocolVersionIncompatible {

        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        //TODO Catch Casting Error
        message message = (message) objectInputStream.readObject();
        //TODO Throw version Error
        if (!message.version().equals(version))
        {
            throw new MessageProtocolVersionIncompatible();
        }
        return message;
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
}
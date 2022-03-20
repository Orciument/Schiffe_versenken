package main.ressources.protocol;

import main.ressources.Exceptions.ConnectionResetByPeerException;
import main.ressources.Exceptions.MessageProtocolVersionIncompatible;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import static main.ressources.DebugOut.debugOut;

public class MessageEndpoint {
    static final String version = "1.0";

    public static void sent(String type, HashMap<String, String> body, Socket socket) throws ConnectionResetByPeerException {
        Message message = new Message(version, type, body);

        //Sent message to the Client
        try {
            if (!socket.isConnected() || socket.isInputShutdown() || !socket.isBound() || socket.isClosed()) {
                throw new ConnectionResetByPeerException();
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            debugOut("[E.S] " + message);
            objectOutputStream.writeObject(message);
            //Maybe with a String and message.toString system
        } catch (Exception e) {
            throw new ConnectionResetByPeerException();
        }
    }


    public static Message receive(InputStream inputStream) throws IOException, ClassNotFoundException, MessageProtocolVersionIncompatible, ConnectionResetByPeerException {

        if (inputStream == null) {
            throw new ConnectionResetByPeerException();
        }
        Message message;


        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Object o = objectInputStream.readObject();
            message = (Message) o;
        } catch (SocketException e) {
            throw new ConnectionResetByPeerException();
        } catch (ClassCastException | InvalidClassException e) {
            throw new ClassNotFoundException();
        }


        if (!message.version().equals(version)) {
            throw new MessageProtocolVersionIncompatible();
        }
        debugOut("[E.R] " + message);
        return message;
    }
}
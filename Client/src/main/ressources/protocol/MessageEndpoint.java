package main.ressources.protocol;

import main.ressources.Exceptions.MessageProtocolVersionIncompatible;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static main.ressources.DebugOut.debugOut;

public class MessageEndpoint {
    static final String version = "1.0";

    public static void sent(String type, HashMap<String, String> body, Socket socket) {
        Message message = new Message(version, type, body);

        //Sent message to the Client
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            debugOut("[Endpoint Sent] " + message);
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Message receive(InputStream inputStream) throws IOException, ClassNotFoundException, MessageProtocolVersionIncompatible {

        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        //TODO Catch Casting Error
        Message message = (Message) objectInputStream.readObject();
        //TODO Throw version Error
        if (!message.version().equals(version)) {
            throw new MessageProtocolVersionIncompatible();
        }
        debugOut("[Endpoint Received] " + message);
        return message;
    }
}
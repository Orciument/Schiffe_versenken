package main.ressources.protocol;

import main.data.*;
import main.ressources.Exceptions.*;

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
            //TODO Unable to send the message, please mix
            //Maybe with a String and message.toString system
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Message receive(InputStream inputStream) throws IOException, ClassNotFoundException, MessageProtocolVersionIncompatible {

        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        //TODO Catch Casting Error
        Object o = objectInputStream.readObject();
        Message message = (Message) o;
        //TODO Throw version Error
        if (!message.version().equals(version)) {
            throw new MessageProtocolVersionIncompatible();
        }
        debugOut("[Endpoint Received] " + message);
        return message;
    }


}
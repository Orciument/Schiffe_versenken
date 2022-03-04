package ressources.protocol;

import data.DataHandler;
import ressources.Exceptions.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;

public class MessageEndpoint {
    static DataHandler dataHandler;
    static final String version = "0.1";

    public MessageEndpoint(DataHandler dataHandler) {
        MessageEndpoint.dataHandler = dataHandler;
    }

    public static void sent(String type, LinkedHashMap<String, String> body, Socket socket) {
        Message message = new Message(version, type, body);

        //Sent message to the Client
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sent Message: " + message);
            objectOutputStream.writeObject(message);
            //TODO Unable to send the message, please mix
            //Maybe with a String and message.toString system
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Message receive(DataInputStream inputStream) throws IOException, ClassNotFoundException, MessageProtocolVersionIncompatible {

        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        //TODO Catch Casting Error
        Object o = objectInputStream.readObject();
        System.out.println(o == null);
        System.out.println(o.getClass());
        Message message = (Message) objectInputStream.readObject();
        //TODO Throw version Error
        if (!message.version().equals(version))
        {
            throw new MessageProtocolVersionIncompatible();
        }
        System.out.println("received Message: " + message);
        return message;
    }
}
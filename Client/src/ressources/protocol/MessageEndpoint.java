package ressources.protocol;

import data.DataHandler;
import ressources.Exceptions.MessageProtocolVersionIncompatible;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;

import static ressources.DebugOut.debugOut;

public class MessageEndpoint {
    static DataHandler dataHandler;
    static final String version = "1.0";

    public MessageEndpoint(DataHandler dataHandler) {
        MessageEndpoint.dataHandler = dataHandler;
    }

    public static void sent(String type, LinkedHashMap<String, String> body, Socket socket) {
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


    public static Message receive(DataInputStream inputStream) throws IOException, ClassNotFoundException, MessageProtocolVersionIncompatible {

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
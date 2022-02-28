package ressources.protocol;

import data.dataHandler;
import ressources.exceptions.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;

public class messageEndpoint {
    static dataHandler dataHandler;
    static final String version = "0.1";

    public messageEndpoint(dataHandler dataHandler) {
        messageEndpoint.dataHandler = dataHandler;
    }

    public static void sent(String type, LinkedHashMap<String, String> body, Socket socket) {
        message message = new message(version, type, body);

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
}
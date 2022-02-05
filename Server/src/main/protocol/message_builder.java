package protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.LinkedHashMap;

public class message_builder {

    //Protokoll Version
    static String version = "0.1";
/*
    public static void buildMessage(DataOutputStream dataOutputStream, String type, SocketAddress sourceAddress, InetSocketAddress destinationAddress, LinkedHashMap<String, String> body) throws IOException {
        message message = new message(version,type,new Date(System.currentTimeMillis()), (InetSocketAddress) sourceAddress,destinationAddress,body);
        messageTimeoutCache messageTimeoutCache = new messageTimeoutCache(dataHandler);
        messageTimeoutCache.sent(message);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(dataOutputStream);
        objectOutputStream.writeObject(message);
    }


    public static String buildMessage(String type, SocketAddress sourceAddress, InetSocketAddress destinationAddress, LinkedHashMap<String, String> body) throws IllegalArgumentException {
        //Check for Illegal Arguments that could result in parsing Errors
        checkForIllegalArguments(type, "type");
        for (String key : body.keySet()) {
            checkForIllegalArguments(key, "body");
        }
        for (String value : body.values()) {
            checkForIllegalArguments(value, "body");
        }


        LinkedHashMap<String, Object> header = new LinkedHashMap<>();
        header.put("version", version);
        header.put("type", type);
        header.put("time", System.currentTimeMillis());

        String sourceAddressString = sourceAddress.toString();
        sourceAddressString = sourceAddressString.substring(sourceAddressString.indexOf("/"));
        header.put("sourceAddress", sourceAddressString);

        String destinationAddressString = destinationAddress.toString();
        destinationAddressString = destinationAddressString.substring(destinationAddressString.indexOf("/"));
        header.put("destinationAddress", destinationAddressString);

        String message = header.toString();
        message = message.concat(" body=");
        message = message.concat(body.toString());
        return message;
    }

    private static void checkForIllegalArguments(String string, String from) {
        if (string.contains("{")) {
            throw new IllegalArgumentException("Invalid Charakter: { in " + from);
        }
        if (string.contains("}")) {
            throw new IllegalArgumentException("Invalid Charakter: } in "  + from);
        }
        if (string.contains(",")) {
            throw new IllegalArgumentException("Invalid Charakter: , in " + from);
        }
        if (string.contains("=")) {
            throw new IllegalArgumentException("Invalid Charakter: = in " + from);
        }
        if (string.contains("body")) {
            throw new IllegalArgumentException("Invalid Charakter: body in " + from);
        }
    }

    public static message parseToEvent(String input) throws IllegalArgumentException {

        InetSocketAddress sourceAddress;
        InetSocketAddress destinationAddress;
        Date time;
        String type;
        LinkedHashMap<String, String> body;

        if (!input.contains("sourceAddress")) {
            throw new IllegalArgumentException("Input String does not Contain a sourceAddress");
        }
        String sourceAddressString = fetchValue(input, "sourceAddress");
        String hostname = sourceAddressString.substring(1, sourceAddressString.indexOf(":"));
        int port = Integer.parseInt(sourceAddressString.substring(sourceAddressString.indexOf(":") + 1));
        sourceAddress = new InetSocketAddress(hostname, port);
        if (!input.contains("{") || !input.contains("}")) {
            throw new IllegalArgumentException("Message Formatting Error");
        }
        String header = input.substring(input.indexOf("{"), input.indexOf("}") + 1);


        if (!header.contains("destinationAddress")) {
            throw new IllegalArgumentException("Message Formatting Error");
        }
        String destinationAddressString = fetchValue(header, "destinationAddress");
        destinationAddress = new InetSocketAddress(destinationAddressString.substring(1, destinationAddressString.indexOf(":")), Integer.parseInt(destinationAddressString.substring(destinationAddressString.indexOf(":") + 1)));

        if (!header.contains("time")) {
            throw new IllegalArgumentException("Message Formatting Error");
        }
        time = new Date(Long.parseLong(fetchValue(header, "time")));

        if (!header.contains("version")) {
            throw new IllegalArgumentException("Message Formatting Error");
        }
        version = fetchValue(header, "version");

        if (!header.contains("type")) {
            throw new IllegalArgumentException("Message Formatting Error");
        }
        type = fetchValue(header, "type");

        if (!input.contains("body")) {
            throw new IllegalArgumentException("Message Formatting Error");
        }
        body = parseToKeyValuePair(input.substring(input.indexOf("body") + 6, input.length()-1));


        return new message(version, type, time, sourceAddress, destinationAddress, body);
    }

    public static String buildErrorMessage(InetSocketAddress destinationAddress, InetSocketAddress sourceAddress, String errorType) {
        //TODO Protokoll für Error nicht fertig - Concept
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("ErrorType", errorType);
        return buildMessage("Error", destinationAddress, sourceAddress, body);
    }

    private static String fetchValue(String header, String query) {
        int valueStart = (header.indexOf("=", header.indexOf(query)) + 1);
        if (header.substring(valueStart).contains(",")) {
            return header.substring(valueStart, header.indexOf(",", valueStart));
        } else {
            return header.substring(valueStart, header.indexOf("}", valueStart));
        }
    }

    private static LinkedHashMap<String, String> parseToKeyValuePair(String body) {

        LinkedHashMap<String, String> bodyHashMap = new LinkedHashMap<>();
        while (!body.isBlank()) {
            String key;
            if (body.charAt(0) == ' ')
            {
                key = body.substring(1, body.indexOf("="));
            }
            else
            {
                key = body.substring(0, body.indexOf("="));
            }

            if (body.contains(",")) {
                String value = body.substring(body.indexOf("=") + 1, body.indexOf(","));
                bodyHashMap.put(key, value);
                body = body.substring(body.indexOf(",") + 1);
            } else {
                String value = body.substring(body.indexOf("=") + 1);
                bodyHashMap.put(key, value);
                body = "";
            }
        }
        return bodyHashMap;
    }

    */
}
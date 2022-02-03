package main.protocol;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.LinkedHashMap;

public class message_builder {

    //Protokoll Version
    static String version = "0.1";

    public static String buildMessage(String type, InetSocketAddress sourceAddress, InetSocketAddress destinationAddress, LinkedHashMap<String, String> body) {
        //TODO Throw exception when Parameters Contains "{", "}", ",", "=", "body"
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

    //TODO Change all Error functions with custom Exceptions and send the Error throw a catch Block in der Class that wanted to parse
    public static message parseToEvent(String input) throws Exception {

        InetSocketAddress sourceAddress;
        InetSocketAddress destinationAddress;
        Date time;
        String type;
        LinkedHashMap<String, String> body;

        if (!input.contains("sourceAddress")) {
            System.out.println("GHER");
            return null;
        }
        String sourceAddressString = fetchValue(input, "sourceAddress");
        sourceAddress = new InetSocketAddress(sourceAddressString.substring(0, sourceAddressString.indexOf(":")), Integer.parseInt(sourceAddressString.substring(sourceAddressString.indexOf(":") + 1)));


        if (!input.contains("{") || !input.contains("}")) {
            throw new Exception("Message Formatting Error");
        }
        String header = input.substring(input.indexOf("{"), input.indexOf("}") + 1);


        if (!header.contains("destinationAddress")) {
            throw new Exception("Message Formatting Error");
        }
        String destinationAddressString = fetchValue(header, "destinationAddress");
        destinationAddress = new InetSocketAddress(destinationAddressString.substring(0, destinationAddressString.indexOf(":")), Integer.parseInt(destinationAddressString.substring(destinationAddressString.indexOf(":") + 1)));

        if (!header.contains("time")) {
            throw new Exception("Message Formatting Error");
        }
        time = new Date(Long.parseLong(fetchValue(header, "time")));

        if (!header.contains("version")) {
            throw new Exception("Message Formatting Error");
        }
        version = fetchValue(header, "version");

        if (!header.contains("type")) {
            throw new Exception("Message Formatting Error");
        }
        type = fetchValue(header, "type");

        if (!input.contains("body")) {
            throw new Exception("Message Formatting Error");
        }
        body = parseToKeyValuePair(input.substring(input.indexOf("body") + 5));

        return new message(version, type, time, sourceAddress, destinationAddress, body);
    }

    private static String buildErrorMessage(InetSocketAddress destinationAddress, InetSocketAddress sourceAddress, String errorType) {
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
            String key = body.substring(0, body.indexOf("="));
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
}
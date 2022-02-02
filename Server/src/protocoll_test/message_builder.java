package protocoll_test;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;

public class message_builder {

    //Protokoll Version
    static String version = "0.1";

    public static message disect(String input) {

        InetSocketAddress sourceAddress;
        InetSocketAddress destinationAddress;
        Date time;
        String type;
        HashMap<String, String> body;

        if (!input.contains("sourceAddress")) {
            System.out.println("GHER");
            return null;
        }
        String sourceAddressString = fetchValue(input, "sourceAddress");
        sourceAddress = new InetSocketAddress(sourceAddressString.substring(0, sourceAddressString.indexOf(":")), Integer.parseInt(sourceAddressString.substring(sourceAddressString.indexOf(":") + 1)));


        if (!input.contains("{") || !input.contains("}")) {

            error(sourceAddress);
            return null;
        }
        String header = input.substring(input.indexOf("{"), input.indexOf("}") + 1);


        if (!header.contains("destinationAddress")) {
            error(sourceAddress);
            return null;
        }
        String destinationAddressString = fetchValue(header, "destinationAddress");
        destinationAddress = new InetSocketAddress(destinationAddressString.substring(0, destinationAddressString.indexOf(":")), Integer.parseInt(destinationAddressString.substring(destinationAddressString.indexOf(":") + 1)));

        if (!header.contains("time")) {
            error(sourceAddress);
            return null;
        }
        time = new Date(Long.parseLong(fetchValue(header, "time")));

        if (!header.contains("version")) {
            error(sourceAddress);
            return null;
        }
        version = fetchValue(header, "version");

        if (!header.contains("type")) {
            error(sourceAddress);
            return null;
        }
        type = fetchValue(header, "type");

        if (!input.contains("body")) {
            error(sourceAddress);
            return null;
        }
        body = parseToKeyValuePair(input.substring(input.indexOf("body") + 5));


        return new message(version, type, time, sourceAddress, destinationAddress, body);
    }

    private static void error(InetSocketAddress address) {
        HashMap<String, String> body = new HashMap<>();
        body.put("ErrorType","Message Formatting Error");
        buildMessage("Error",address,body);
    }

    private static String fetchValue(String header, String query) {
        int valueStart = (header.indexOf("=", header.indexOf(query)) + 1);
        if (header.substring(valueStart).contains(",")) {
            return header.substring(valueStart, header.indexOf(",", valueStart));
        } else {
            return header.substring(valueStart, header.indexOf("}", valueStart));
        }
    }

    private static HashMap<String, String> parseToKeyValuePair(String body) {

        HashMap<String, String> bodyHashMap = new HashMap<>();
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

    private static void buildMessage(String type, InetSocketAddress destinationAddress, HashMap<String, String> body) {
        HashMap<String, Object> header = new HashMap<>();
        header.put("version", version);
        header.put("type", type);
        header.put("time", System.currentTimeMillis());
        //TODO Serversocket InetSocketaddress: Wie kommt die hier her?
        //header.put("sourceAddress", );
        header.put("destinationAddress", destinationAddress.toString());
        String message = header.toString();
        message = message.concat(" body=");
        message = message.concat(body.toString());
        System.out.println(message);
        //TODO Wie wird die Nachricht jetzt gesendet?
    }
}



    git init
    git add README.md
        git commit -m "first commit"
        git branch -M main
        git remote add origin https://github.com/Orciument/Schiffe_versenken.git
        git push -u origin main

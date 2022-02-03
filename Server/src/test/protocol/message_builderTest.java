package test.protocol;

import main.protocol.message;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;

import static main.protocol.message_builder.buildMessage;
import static main.protocol.message_builder.parseToEvent;


class message_builderTest {

    @Test
    void buildMessageCorrect() {
        //TODO Maybe wechseln von "localhost" zu einer wirklichen Addresse
        InetSocketAddress sourceAddress = new InetSocketAddress("localhost", 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress("localhost", 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302");
        body.put("test", "-1-2");

        String message = buildMessage("Test", sourceAddress, destinationAddress, body);
        message = message.replaceFirst("time=\\d+,", "time=,");
        assert message.equals("{version=0.1, type=Test, time=, sourceAddress=/127.0.0.1:8001, destinationAddress=/127.0.0.1:8000} body={name=paul, counter=302, test=-1-2}");
    }
    //TODO Unit Test with "{", "}", ",", "="

    @Test
    void parseToEventCorrect() {
        try {
            message message = parseToEvent("{sourceAddress=127.0.0.1:8001, destinationAddress=127.0.0.1:8000, time=1643842876928, type=Test, version=0.1} body={test=-1-2, name=paul, counter=302}");
            //TODO Exception handling
            //TODO Compare values
            System.out.println(message);
            System.out.println("destinationAddress: " + message.destinationAddress);
            System.out.println("sourceAddress: " + message.sourceAddress);
            System.out.println("type: " + message.type);
            System.out.println("version: " + message.version);
            System.out.println("time: " + message.time);
            System.out.println("body: " + message.body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
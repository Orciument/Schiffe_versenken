package test.protocol;

import main.protocol.message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;

import static main.protocol.message_builder.buildMessage;
import static main.protocol.message_builder.parseToEvent;


@SuppressWarnings({"NewClassNamingConvention", "SpellCheckingInspection"})
class message_builderTest {

    //buildMessage Tests
    @Test
    void buildMessage_Correct() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302");
        body.put("test", "-1-2");

        String message = buildMessage("Test", sourceAddress, destinationAddress, body);
        message = message.replaceFirst("time=\\d+,", "time=,");
        assert message.equals("{version=0.1, type=Test, time=, sourceAddress=" + sourceAddress + ", destinationAddress=" + destinationAddress + "} body={name=paul, counter=302, test=-1-2}");
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_OpenBracket() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("na{me", "paul");
        body.put("counter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: { in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Value_OpenBracket() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302");
        body.put("test", "-1-{2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: { in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_ClosedBracket() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("cou}nter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: } in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Value_ClosedBracket() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul}");
        body.put("counter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: } in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_Comma() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302");
        body.put(",test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: , in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Value_Comma() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul,");
        body.put("counter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: , in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_Equals() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("co=unter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: = in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Value_Equals() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302=");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: = in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_Body() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302");
        body.put("test", "-1-2");
        body.put("body", "239202");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: body in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Value_Body() {
        InetSocketAddress sourceAddress = new InetSocketAddress(getInet4AddressString(), 8001);
        InetSocketAddress destinationAddress = new InetSocketAddress(getInet4AddressString(), 8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "body");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: body in body", "Test Failed");
    }

    //Helper
    String getInet4AddressString() {
        try {
            String address = String.valueOf(Inet4Address.getLocalHost());
            address = address.substring(address.indexOf("/"));
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Parse Message Tests

    //TODO Unit Tests for the parser
    @Test
    void parseToEvent_Correct() {
        //TODO Test ist nicht richtig
        try {
            //TODO parseToEvent can result in an Exception
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
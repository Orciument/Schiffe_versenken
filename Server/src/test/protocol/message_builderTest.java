package protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;

import static protocol.message_builder.buildMessage;
import static protocol.message_builder.parseToEvent;


@SuppressWarnings({"NewClassNamingConvention", "SpellCheckingInspection"})
class message_builderTest {

    //buildMessage Tests
    @Test
    void buildMessage_Correct() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302");
        body.put("test", "-1-2");

        String message = buildMessage("Test", sourceAddress, destinationAddress, body);
        System.out.println(message);
        message = message.replaceFirst("time=\\d+,", "time=,");
        Assertions.assertEquals(("{version=0.1, type=Test, time=, sourceAddress=" + sourceAddress + ", destinationAddress=" + destinationAddress + "} body={name=paul, counter=302, test=-1-2}"), message);
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_OpenBracket() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("na{me", "paul");
        body.put("counter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: { in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Value_OpenBracket() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302");
        body.put("test", "-1-{2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: { in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_ClosedBracket() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("cou}nter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: } in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Value_ClosedBracket() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul}");
        body.put("counter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: } in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_Comma() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302");
        body.put(",test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: , in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Value_Comma() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul,");
        body.put("counter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: , in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_Equals() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("co=unter", "302");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: = in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Value_Equals() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302=");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: = in body", "Test Failed");
    }

    @Test
    void buildMessage_InvalidCharacterise_Key_Body() {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
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
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "body");
        body.put("test", "-1-2");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> buildMessage("Test", sourceAddress, destinationAddress, body), "Test failed");
        Assertions.assertEquals(thrown.toString(), "java.lang.IllegalArgumentException: Invalid Charakter: body in body", "Test Failed");
    }

    //Helper
    InetSocketAddress getOwnInet4Address(int port) {
        try {
            String hostname = String.valueOf(Inet4Address.getLocalHost());
            hostname = hostname.substring(hostname.indexOf("/"));
            return new InetSocketAddress(hostname.substring(1), port) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Parse Message Tests

    //TODO Unit Tests for the parser
    @Test
    void parseToEvent_Correct() {

        try {
            message message;
            try {
                message = parseToEvent("{sourceAddress=/127.0.0.1:8001, destinationAddress=/127.0.0.1:8000, time=1643842876928, type=Test, version=0.1} body={test=-1-2, name=paul, counter=302}");
                Assertions.assertEquals("/127.0.0.1:8001", message.sourceAddress().toString(), "sourceAddress" );
                Assertions.assertEquals("/127.0.0.1:8000", message.destinationAddress().toString(), "destinationAddress");
                Assertions.assertEquals("1643842876928", String.valueOf(message.time().getTime()), "time");
                Assertions.assertEquals("Test", message.type(), "type");
                Assertions.assertEquals("0.1", message.version(), "version");
                //TODO Body

            } catch (IllegalArgumentException e) {
                Assertions.fail("IllegalArgumentException should't be thrown");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void message_builder()
    {
        InetSocketAddress sourceAddress = getOwnInet4Address(8001);
        InetSocketAddress destinationAddress = getOwnInet4Address(8000);
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.put("name", "paul");
        body.put("counter", "302");
        body.put("test", "-1-2");
        System.out.println(sourceAddress);
        String messageString = buildMessage("Test", sourceAddress, destinationAddress, body);
        message message = parseToEvent(messageString);

        Assertions.assertEquals(sourceAddress, message.sourceAddress());
        Assertions.assertEquals(destinationAddress, message.destinationAddress());
        Assertions.assertEquals("Test", message.type());
        Assertions.assertEquals(body, message.body());
    }
}
package protocoll_test;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;

public class protocol {



    //TODO KLasse hat keinen Sinn, bzw. vergessen. Temporär
    public static void main(String[] args) {
        try {
            String message = bsp1();
            System.out.println(message);
            message message1 = message_builder.disect(message);
            print(message1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //-------------------------------------------------------------------------


    }

    //TODO Message Builder
    //TODO Message Deconstruktor

    public static String bsp1() throws UnknownHostException {
        String message = "";
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("version","0.1");
        header.put("type", "test");
        header.put("time", String.valueOf(System.currentTimeMillis()));

        int port = 8001;
        String temp = String.valueOf(Inet4Address.getLocalHost());
        temp = temp.substring(temp.indexOf("/")+1);
        temp = temp + ":"+ port;
        header.put("sourceAddress", temp);

        header.put("destinationAddress", "192.168.1.100:8000");
        String p = header.toString();
        message = message.concat(p);
        message = message.concat(" body=");
        message = message.concat("test=12829, Zahl=2919, name=f+ng");

        return message;
    }

    public static String bsp2()
    {
        return "sourceAddress=192.168.2.100:8001, destinationAddress=192.168.1.100:8000, time=1643713928697, type=test, version=0.1} body=Guten Tag, bla bla bla";
    }

    public static void print(message message)
    {
        System.out.println(message);
        System.out.println("destinationAddress: "+message.destinationAddress);
        System.out.println("sourceAddress: " +message.sourceAddress);
        System.out.println("type: " +message.type);
        System.out.println("version: "+message.version);
        System.out.println("time: "+message.time);
        System.out.println("body: "+message.body);

    }
}
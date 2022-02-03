package main.protocol;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;

public class message {

    public String version;
    public String type;
    public Date time;
    public InetSocketAddress sourceAddress;
    public InetSocketAddress destinationAddress;
    public HashMap<String, String> body;

    public message(String version, String type, Date time, InetSocketAddress sourceAddress, InetSocketAddress destinationAddress, HashMap<String, String> body) {
        this.version = version;
        this.type = type;
        this.time = time;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.body = body;
    }

}

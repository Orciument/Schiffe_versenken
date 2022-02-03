package main.protocol;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;

public class message {

    public final String version;
    public final String type;
    public final Date time;
    public final InetSocketAddress sourceAddress;
    public final InetSocketAddress destinationAddress;
    public final HashMap<String, String> body;

    public message(String version, String type, Date time, InetSocketAddress sourceAddress, InetSocketAddress destinationAddress, HashMap<String, String> body) {
        this.version = version;
        this.type = type;
        this.time = time;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.body = body;
    }

}

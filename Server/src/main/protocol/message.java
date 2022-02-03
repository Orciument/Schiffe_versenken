package protocol;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;

public record message(String version, String type, Date time, InetSocketAddress sourceAddress, InetSocketAddress destinationAddress, HashMap<String, String> body) {}

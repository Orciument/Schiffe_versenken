package ressources.protocol;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;

public record message(String version, String type, HashMap<String, String> body) implements Serializable {}
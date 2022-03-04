package ressources.protocol;

import java.io.Serializable;
import java.util.HashMap;

public record Message(String version, String type, HashMap<String, String> body) implements Serializable {}
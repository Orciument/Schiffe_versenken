package resources.protocol;

import java.io.Serializable;
import java.util.HashMap;

public record message(String version, String type, HashMap<String, String> body) implements Serializable {}
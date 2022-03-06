package ressources.protocol;

import java.io.Serializable;
import java.util.LinkedHashMap;
//Sollte eigentlich eine LinkedHashmap sein, aber das führt zu problemen
public record Message(String version, String type, LinkedHashMap<String , String> body) implements Serializable {}
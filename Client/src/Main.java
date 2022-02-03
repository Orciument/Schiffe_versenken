import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.net.URISyntaxException;

@SuppressWarnings("RedundantThrows")
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        Console console = System.console();
        if (console == null && !GraphicsEnvironment.isHeadless()) {
            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar \"" + filename + "\""});
        } else {
            client.main(new String[0]);
        }
    }
}
import data.DataHandler;
import listener.ConsoleListener;
import listener.RequestListener;
import ressources.DebugOut;
import ressources.Display;
import ressources.protocol.MessageEndpoint;

public class Main {

    public static void main(String[] args) {
        //Dialog Option, ob als Client, oder als Host gestartet werden soll
        DataHandler dataHandler = new DataHandler();
        //noinspection InstantiationOfUtilityClass
        new MessageEndpoint(dataHandler);
        new Display(dataHandler);
        new DebugOut(dataHandler);
        new RequestListener(dataHandler).start();
        new ConsoleListener(dataHandler).start();
    }
}

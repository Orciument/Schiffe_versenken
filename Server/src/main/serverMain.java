import data.DataHandler;
import listener.*;
import ressources.protocol.MessageEndpoint;


public class serverMain {

    public static void main(String[] args) {
        //Dialog Option, ob als Client, oder als Host gestartet werden soll
        DataHandler dataHandler = new DataHandler();
        //noinspection InstantiationOfUtilityClass
        new MessageEndpoint(dataHandler);
        new AcceptListener(dataHandler).start();
        new ConsoleListener(dataHandler).start();
    }
}
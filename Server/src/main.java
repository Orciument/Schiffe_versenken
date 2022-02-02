import data.dataHandler;
import listener.*;

public class main {

    //TODO Initialises the Data, Listener, and sender
    public static void main(String[] args) {
        dataHandler dataHandler = new dataHandler();
        new acceptListener(dataHandler).start();
        new requestListener(dataHandler).start();
        new consoleListener(dataHandler).start();
    }


}

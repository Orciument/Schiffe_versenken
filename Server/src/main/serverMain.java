
import data.dataHandler;
import listener.*;
import protocol.messageTimeoutCache;

public class serverMain {

    public static void main(String[] args) {
        //TODO Dialog Option, ob als Client, oder als Host gestartet werden soll
        dataHandler dataHandler = new dataHandler();
        messageTimeoutCache messageTimeoutCache = new messageTimeoutCache();
        new acceptListener(dataHandler).start();
        new requestListener(dataHandler).start();
        new consoleListener(dataHandler).start();
        //TODO Add Message sender class
    }


}
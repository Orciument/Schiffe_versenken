
import data.dataHandler;
import listener.*;
import protocol.messageEndpoint;

public class serverMain {

    public static void main(String[] args) {
        //TODO Dialog Option, ob als Client, oder als Host gestartet werden soll
        dataHandler dataHandler = new dataHandler();
        new messageEndpoint(dataHandler);
        new acceptListener(dataHandler).start();
        new requestListener(dataHandler).start();
        new consoleListener(dataHandler).start();
        //TODO Add Message sender class
    }


}
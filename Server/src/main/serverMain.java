import data.dataHandler;
import listener.*;
import protocol.messageEndpoint;


public class serverMain {

    public static void main(String[] args) {
        //Dialog Option, ob als Client, oder als Host gestartet werden soll
        dataHandler dataHandler = new dataHandler();
        //TODO Add Thread Names
        new messageEndpoint(dataHandler);
        new acceptListener(dataHandler).start();
        new consoleListener(dataHandler).start();
    }
}
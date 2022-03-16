package main;

import main.data.*;
import main.listener.*;
import main.ressources.protocol.MessageEndpoint;


public class ServerMain {

    public static void main(String[] args) {
        //Dialog Option, ob als Client, oder als Host gestartet werden soll
        DataHandler dataHandler = new DataHandler();
        //noinspection InstantiationOfUtilityClass
        new MessageEndpoint(dataHandler);
        new AcceptListener(dataHandler).start();
        new ConsoleListener(dataHandler).start();
    }
}
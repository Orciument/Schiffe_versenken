package main;

import main.data.DataHandler;
import main.listener.AcceptListener;
import main.listener.ConsoleListener;


public class ServerMain {

    public static void main(String[] args) {
        //Dialog Option, ob als Client, oder als Host gestartet werden soll
        DataHandler dataHandler = new DataHandler();
        new AcceptListener(dataHandler).start();
        new ConsoleListener(dataHandler).start();
    }
}
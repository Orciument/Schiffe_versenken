package main;

import main.data.DataHandler;
import main.listener.ConsoleListener;
import main.listener.RequestListener;
import main.ressources.DebugOut;
import main.ressources.Display;

public class ClientMain {

    public static void main(String[] args) {
        //Dialog Option, ob als Client, oder als Host gestartet werden soll
        DataHandler dataHandler = new DataHandler();
        new Display(dataHandler);
        new DebugOut(dataHandler);
        new RequestListener(dataHandler).start();
        new ConsoleListener(dataHandler).start();
    }
}

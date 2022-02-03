package main.listener;

import main.data.dataHandler;

public class consoleListener extends Thread {

    dataHandler dataHandler;

    public consoleListener(dataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void run() {
        listener();
    }


    public void listener() {
        while (dataHandler.getRUN()) {

        }
    }
}

package main.listener;

import main.data.dataHandler;

public class consoleListener extends Thread {

    final dataHandler dataHandler;

    public consoleListener(dataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void run() {
        listener();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    public void listener() {
        while (dataHandler.getRUN()) {

        }
    }
}

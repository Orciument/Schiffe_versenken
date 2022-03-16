package main.listener;

import main.data.DataHandler;

public class ConsoleListener extends Thread {

    final DataHandler dataHandler;

    public ConsoleListener(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        Thread.currentThread().setName("ConsoleListener");
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

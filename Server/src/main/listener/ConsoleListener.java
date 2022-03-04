package listener;

import data.DataHandler;

public class ConsoleListener extends Thread {

    final DataHandler dataHandler;

    public ConsoleListener(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        Thread.currentThread().setName("consoleListener");
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

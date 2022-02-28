package listener;

import data.dataHandler;

public class consoleListener extends Thread {

    final dataHandler dataHandler;

    public consoleListener(dataHandler dataHandler) {
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

package main.listener;

import main.data.dataHandler;

public class requestListener extends Thread {

    dataHandler dataHandler;

    public requestListener(dataHandler dataHandler) {
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

    //TODO Bearbeitet alle Requests
    //Behandelt alle Request die eingehen, und bietet Methoden um diese zu beantworten
}

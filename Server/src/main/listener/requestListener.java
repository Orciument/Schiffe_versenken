package listener;

import data.dataHandler;

public class requestListener extends Thread {

    final dataHandler dataHandler;

    public requestListener(dataHandler dataHandler) {
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

    //TODO Bearbeitet alle Requests
    //Behandelt alle Request die eingehen, und bietet Methoden um diese zu beantworten
}

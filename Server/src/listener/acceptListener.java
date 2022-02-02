package listener;

import data.dataHandler;

import java.net.Socket;

public class acceptListener extends Thread {
    dataHandler dataHandler;

    public acceptListener(dataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void run() {
        listener();
    }

    public void listener() {
        while (dataHandler.getRUN())
            try {
                Socket newClientSocket = dataHandler.getServerSocket().accept();
                //TODO Request Information from Client, and Validate the connection
                dataHandler.addClient(newClientSocket, "test");
            } catch (Exception e) {
                e.printStackTrace();
            }

    }
}

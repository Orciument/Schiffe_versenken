package listener;

import data.dataHandler;
import protocol.messageEndpoint;

import java.net.Socket;
import java.util.LinkedHashMap;

public class acceptListener extends Thread {
    final dataHandler dataHandler;

    public acceptListener(dataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void run() {
        listener();
    }

    public void listener() {
        while (dataHandler.getRUN()) {
            try {
                Socket newClientSocket = dataHandler.getServerSocket().accept();
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                messageEndpoint.sent("Identification Request", body, newClientSocket);
                //TODO Client wird hinzugefügt, wenn der Request Listener sie überprüft, und nicht hier
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


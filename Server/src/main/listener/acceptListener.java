package listener;

import data.dataHandler;
import protocol.message_builder;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
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
        while (dataHandler.getRUN())
            try {
                Socket newClientSocket = dataHandler.getServerSocket().accept();
                //TODO Request Information from Client, and Validate the connection
                //TODO Identification Request
                LinkedHashMap<String, String> body = new LinkedHashMap<>();
                String message = message_builder.buildMessage("Identification Request", newClientSocket.getRemoteSocketAddress(), getOwnInet4Address(dataHandler.getServerSocket().getLocalPort()), body);
                //TODO füge message zu, messageTimeOutCache hinzu
                //TODO Client wird hinzugefügt, wenn der Request Listener sie überpfüft, und nicht hier
                //dataHandler.addClient(newClientSocket, "test");
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    InetSocketAddress getOwnInet4Address(int port) {
        try {
            String hostname = String.valueOf(Inet4Address.getLocalHost());
            hostname = hostname.substring(hostname.indexOf("/"));
            return new InetSocketAddress(hostname.substring(1), port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

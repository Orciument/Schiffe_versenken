package data;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class dataHandler {
    private final server server;
    private final ArrayList<client> clientArrayList = new ArrayList<>();

    public dataHandler() {
        server = new server();
    }


    //Methode does not Validate if a Client should be allowed to be added to the game
    public void addClient(Socket socket, String name) {

        client newClient = new client(socket, name);
        clientArrayList.add(newClient);
        System.out.println("[Server] Neuer Client hinzugefügt");
    }

    public ServerSocket getServerSocket() {
        return server.getServerSocket();
    }

    public client getClient(int index) {
        return clientArrayList.get(index);
    }

    public boolean getRUN() {
        return server.getRun();
    }

    public void setRUN(boolean run) {
        server.setRun(run);
    }
}
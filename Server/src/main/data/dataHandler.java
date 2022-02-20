package data;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class dataHandler {
    private final server server;
    private final ArrayList<client> clientArrayList = new ArrayList<>();
    private final ArrayList<Socket> clientJoinList = new ArrayList<>();

    public dataHandler() {
        server = new server();
    }


    //Methode does not Validate if a Client should be allowed to be added to the game
    public void addClient(client client) {
        clientArrayList.add(client);
        System.out.println("[Server] Neuer Client hinzugefügt");
    }

    public ServerSocket getServerSocket() {
        return server.getServerSocket();
    }

    public ArrayList<client> getClientList() {
        return clientArrayList;
    }

    public client getOtherClient (client client)
    {
        int oldClientIndex = clientArrayList.indexOf(client);
        if (oldClientIndex == clientArrayList.size())
        {
            return clientArrayList.get(0);
        }
        return clientArrayList.get(oldClientIndex+1);
    }

    public boolean getRUN() {
        return server.getRun();
    }

    public void setRUN(boolean run) {
        server.setRun(run);
    }

    public int getGamestate() {
        return server.getGameState();
        /*
        0 = Vorm Spiel/Server Start
        1 = Vor der Runde, Joinen
        2 = In der Runde
        3 = Nach der Runde
        */
    }

    public void setGamestate(int gamestate) {
        server.setGameState(gamestate);
    }

    public ArrayList<Socket> getClientJoinList() {
        return clientJoinList;
    }
}
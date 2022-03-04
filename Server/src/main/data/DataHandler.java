package data;

import java.net.ServerSocket;
import java.util.ArrayList;

public class DataHandler {
    private final Server server;
    private final ArrayList<Client> clientArrayList = new ArrayList<>();

    public DataHandler() {
        server = new Server();
    }


    //Methode does not Validate if a Client should be allowed to be added to the game
    public void addClient(Client client) {
        clientArrayList.add(client);
        System.out.println("[Server] Neuer Client hinzugefügt");
    }

    public ServerSocket getServerSocket() {
        return server.getServerSocket();
    }

    public Client getOtherClient (Client client) {
        int oldClientIndex = clientArrayList.indexOf(client);
        if (oldClientIndex == clientArrayList.size())
        {
            return clientArrayList.get(0);
        }
        return clientArrayList.get(oldClientIndex+1);
    }

    public boolean allShipsPlaced() {
        for (Client client: clientArrayList)
        {
            for (int i: client.currentShips)
            {
                if(client.currentShips[i] != client.maxShips[i])
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getRUN() {
        return server.getRun();
    }

    public void setRUN(boolean run) {
        server.setRun(run);
    }

    public int getGamePhase() {
        return server.getGamePhase();
        /*
        0 = Vorm Spiel/Server Start
        1 = Vor der Runde, Joinen
        2 = In der Runde
        3 = Nach der Runde
        */
    }

    public void setGamePhase(int gamePhase) {
        server.setGamePhase(gamePhase);
        /*
        0 = Vorm Spiel/Server Start
        1 = Vor der Runde, Joinen
        2 = In der Runde
        3 = Nach der Runde
        */
    }

    public boolean getIfClientHasTurn(Client client) {
        return clientArrayList.indexOf(client) == server.getClientIndexHasTurn();
    }

    public void changeClientIndexHasTurn()
    {
        if (server.getClientIndexHasTurn() == 0)
        {
            server.setClientIndexHasTurn(1);
        }
        if (server.getClientIndexHasTurn() == 1)
        {
            server.setClientIndexHasTurn(0);
        }
    }
}
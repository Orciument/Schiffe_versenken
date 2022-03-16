package main.data;

import java.net.ServerSocket;
import java.util.ArrayList;

import static main.ressources.DebugOut.*;

public class DataHandler {
    private final Server server;
    private final ArrayList<Client> clientArrayList = new ArrayList<>();

    public DataHandler() {
        server = new Server();
    }


    //Methode does not Validate if a Client should be allowed to be added to the game
    public void addClient(Client client) {
        clientArrayList.add(client);
        debugOut("[Server] Neuer Client hinzugefügt: " + client.name);
    }

    public ServerSocket getServerSocket() {
        return server.getServerSocket();
    }

    public Client getOtherClient(Client client) {
        if (clientArrayList.indexOf(client) == 1) {
            return clientArrayList.get(0);
        } else {
            return clientArrayList.get(1);
        }
    }

    public boolean allShipsPlaced() {
        for (Client client : clientArrayList) {
            for (int i = 0; i < client.currentShips.length; i++) {
                if (client.currentShips[i] != client.maxShips[i]) {
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

    public void changeClientIndexHasTurn() {
        System.out.println(server.getClientIndexHasTurn());
        if (server.getClientIndexHasTurn() == 0) {
            server.setClientIndexHasTurn(1);
        } else {
            server.setClientIndexHasTurn(0);
        }
        System.out.println(server.getClientIndexHasTurn());
    }

    public int getClientCount() {
        return clientArrayList.size();
    }
}
package data;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.util.Scanner;

public class server {
    private ServerSocket serverSocket;
    private boolean run = true;
    int gameState = 0;
    /*
    0 = Vorm Spiel/Server Start
    1 = Vor der Runde, Joinen
    2 = In der Runde
    3 = Nach der Runde
    */
    int clientIndexHasTurn = 0;

    public server() {
        String hostname;
        try {
            hostname = Inet4Address.getLocalHost().toString();
        } catch (java.net.UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("[Server] Bitte gebe den Port an: ");
        int port = scanner.nextInt();
        try {
            serverSocket = new ServerSocket(port);
        } catch (BindException | IllegalArgumentException e) {
            System.out.println("[Server] Selected Port is invalid, please select a different Port");
            scanner.close();
            new server(); //Tries to open a new Server, in case the process has failed
        } catch (IOException e) {
            e.printStackTrace();
        }
        hostname = hostname.substring(hostname.indexOf("/") + 1);
        System.out.println("[Server] Started auf:");
        System.out.println("[Server] IP:" + hostname);
        System.out.println("[Server] Port: " + port);
        gameState = 1;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public boolean getRun() {
        return run;
    }

    public int getGameState() {
        return gameState;
    }

    public int getClientIndexHasTurn() {
        return clientIndexHasTurn;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public void setClientIndexHasTurn(int clientIndexHasTurn) {
        this.clientIndexHasTurn = clientIndexHasTurn;
    }
}

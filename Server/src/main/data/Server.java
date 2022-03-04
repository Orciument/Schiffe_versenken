package data;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Server {
    private static ServerSocket serverSocket;
    private boolean run = true;
    private int gamePhase = 0;
    /*
    0 = Vorm Spiel/Server Start
    1 = Vor der Runde, Joinen
    2 = In der Runde
    3 = Nach der Runde
    */
    private int clientIndexHasTurn = 0;

    public Server() {
        String hostname;
        int port;
        try {
            hostname = Inet4Address.getLocalHost().toString();
        } catch (java.net.UnknownHostException e) {
            run = false;
            return;
        }
        port = choosePort();
        hostname = hostname.substring(hostname.indexOf("/") + 1);
        System.out.println("[Server] Started auf:");
        System.out.println(hostname + ":" + port);
        System.out.println("----------------------");
        gamePhase = 1;
    }

    private int choosePort() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[Server] Bitte gebe den Port an: ");
        int port = 0;
        try {
            port = scanner.nextInt();
            serverSocket = new ServerSocket(port);
        } catch (BindException | NoSuchElementException | IllegalArgumentException e) {
            System.out.println("[Server] Selected Port is invalid, please select a different Port");

            return choosePort(); //Tries to open a new Server, in case the process has failed
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public boolean getRun() {
        return run;
    }

    public int getGamePhase() {
        return gamePhase;
    }

    public int getClientIndexHasTurn() {
        return clientIndexHasTurn;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public void setGamePhase(int gameState) {
        this.gamePhase = gameState;
    }

    public void setClientIndexHasTurn(int clientIndexHasTurn) {
        this.clientIndexHasTurn = clientIndexHasTurn;
    }
}

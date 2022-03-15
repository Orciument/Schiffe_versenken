package data;

import java.io.IOException;
import java.net.*;
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
        serverSocket = initialiseServerSocket();
        System.out.println("[Server] Started...");
        System.out.println("----------------------");
        gamePhase = 1;
    }

    private ServerSocket initialiseServerSocket() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(choosePort());
            return socket;
        } catch (BindException | NoSuchElementException | IllegalArgumentException e) {
            System.out.println("[Server] Selected Port is invalid, please select a different Port");

            return initialiseServerSocket(); //Tries to open a new Server, in case the process has failed
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int choosePort() {
        int port;
        Scanner scanner = new Scanner(System.in);

        System.out.println("[Server] Bitte wählen den Server Port: ");
        try {
            port = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Port muss eine Nummer sein: ");
            port = choosePort();
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

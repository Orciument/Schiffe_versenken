package main;


import client.*;
import listener.*;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class server {

    public static void main(String[] args) throws IOException {
        new server();
    }

    //TODO Soll ersetzt werden durch main
    //TODO Daten werden von einer anderen Klasse verwaltet


    public ServerSocket serverSocket;
    //TODO Global? Variable that controls the 3 listener Threads
    public boolean run = true;


    Thread acceptListener;
    Thread requestListener;
    Thread consoleListener;

    ArrayList<client> ClientList = new ArrayList<client>();
    int gameState = 0;
    /*
    0 = Vorm Spiel/Server Start
    1 = Vor der Runde, Joinen
    2 = In der Runde
    3 = Nach der Runde
    */
    int clientIndexHasTurn = 0;



    public server() throws IOException {
        newServer();
        listener();
    }




    public void newServer() throws IOException {
        //TODO UnknownHostException einfügen. Ist ONLINE?

        Scanner scanner = new Scanner(System.in);
        System.out.println("[Server] Bitte gebe den Port an: ");
        int port = scanner.nextInt();
        try {
            serverSocket = new ServerSocket(port);
        } catch (BindException e)
        {
            System.out.println("[Server] Port already in use, please select a different Port");
            scanner.close();
            newServer(); //Tries to open a new Server, in case the process has failed
        }
        String hostname = Inet4Address.getLocalHost().toString();
        hostname = hostname.substring(hostname.indexOf("/") +1 );
        System.out.println("[Server] Started auf:");
        System.out.println("[Server] IP:" + hostname);
        System.out.println("[Server] Port: " + port);
        gameState = 1;
    }

    public void listener() {
        acceptListener = new Thread(() -> {
            new acceptListener(serverSocket, this);
        });
        requestListener = new Thread(() -> {
            new requestListener(serverSocket, this);
        });
        consoleListener = new Thread(() -> {
            new consoleListener(this);
        });
        acceptListener.start();
        requestListener.start();
        consoleListener.start();
    }



    //TODO Deprecated - Funktioniert vermutlich wegen Thread Security eh nicht -> "bool run = false"
    public void killThreads() {
        run = false;
        acceptListener.interrupt();
        requestListener.interrupt();
        consoleListener.interrupt();
    }

    //TODO Soll hier nicht sein, sondern in AcceptListener
    public void newClient(Socket socket) {
        //Validate?
        if (gameState == 1)
        {
            client newClient = new client(socket);
            //Request an Client nach Infos, Client werden Infos hinzugefügt, wenn das nicht klappt, wird dem CLient ein Error geschicht, und nicht dem Spiel hinzugefügt
            ClientList.add(newClient);
            System.out.println("[Server] Neuer Client hinzugefügt");
        }

    }
}
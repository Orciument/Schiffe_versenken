package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class server {

    public static void main(String[] args) {
        new server();
    }

    ServerSocket serverSocket;

    ArrayList<client> clients = new ArrayList<>();

    Scanner scanner = new Scanner(System.in);

    public server() {
        System.out.println("[Server] Bitte gebe den Port an: ");
        int port = scanner.nextInt();
        try {
            try {
                serverSocket = new ServerSocket(port);
            } catch (BindException e) {
                e.printStackTrace();
                System.out.println("[Server] Port already in use, please select a different Port");
                new Thread(() -> {
                    try {
                        new server();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
                return;
            }

            System.out.println("[Server] Started");
            for (int i = 0; i < 2; i++) {
                Socket newClientSocket = serverSocket.accept();
                clients.add(new client(new InetSocketAddress(newClientSocket.getInetAddress(), newClientSocket.getPort()), newClientSocket, receiveUTF(newClientSocket)));
                System.out.println("[Server] Client accepted");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sentStringToAll("prepStart");
        System.out.println("Sent prepStart");

        //Preparation Phase, waits until every Client has sent their "ready" message
        ArrayList<Thread> threads = new ArrayList<>();
        for (client client : clients) {
            threads.add(new listenerThread(client, client.name));
        }
        //Wartet bis alle Threads beendet sind
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Main Phase, start
        sentStringToAll("gameStart");
        int start = (int) (Math.random() + 1);
        if (clients.size() < 2) {
            sentStringToAll("gameEnd");
            new Exception("Exception: not enough Players to continue the match").printStackTrace();
        }
        sentStringToClient(clients.get(start), "shoot");


        //Loops until one player ist dead and the game ends
        while (!oneClientDead()) {
            if (clients.size() < 2) {
                sentStringToAll("gameEnd");
                new Exception("Exception: not enough Players to continue the match").printStackTrace();
            }
            for (int i = start; i < clients.size(); i++) {
                client client1 = clients.get(i);
                client client2 = clients.get(i + 1);
                //Emfange "Shoot"
                try {
                    //Sende zu anderem Client "checkHit"
                    int a = client1.dataInputStream.readInt();
                    int b = client1.dataInputStream.readInt();

                    sentStringToClient(client2, "checkhit");
                    sentIntToClient(client2, a);
                    sentIntToClient(client2, b);
                    boolean bool = receiveBool(client2);
                    if (bool) {
                        client2.health--;
                    }
                    sentBoolToClient(client1, bool);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                //Empfange  "checkHit"
                //Speicher
                //Sende anwort an 1ten Client
            }
        }
        //Ende
        sentStringToAll("gameEnd");

    }

    public boolean oneClientDead() {
        for (client client : clients) {
            if (client.health >= 0) {
                return true;
            }
        }
        return false;
    }

    public String receiveUTF(Socket socket) {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            return dataInputStream.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean receiveBool(client client) {
        try {
            return client.dataInputStream.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    void sentBoolToClient(client client, boolean b) {
        try {
            client.dataOutputStream.writeBoolean(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sentIntToClient(client client, int integer) {
        try {
            client.dataOutputStream.writeInt(integer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sentStringToAll(String str) {
        for (client client : clients) {
            sentStringToClient(client, str);
        }
    }

    public void sentStringToClient(client client, String str) {
        try {
            client.dataOutputStream.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
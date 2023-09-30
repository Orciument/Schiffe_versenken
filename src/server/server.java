package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
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
            System.out.println(Inet4Address.getLocalHost());


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
        System.out.println("Main Game starts");
        sentStringToAll("gameStart");
        for (client client : clients) {
            if (!client.socket.isConnected()) {
                sentStringToAll("gameEnd");
                sentStringToAll("[Error] Not enough Players");
                System.out.println("Not enough Players exeption");
                new Exception("Exception: not enough Players to continue the match").printStackTrace();
                return;
            }
        }


        //Loops until one player ist dead and the game ends
        client client1 = clients.get(0);
        client client2 = clients.get(1);
        while (!oneClientDead()) {
            for (client client : clients) {
                if (!client.socket.isConnected()) {
                    sentStringToAll("gameEnd");
                    sentStringToAll("[Error] Not enough Players");
                    System.out.println("Not enough Players exeption");
                    new Exception("Exception: not enough Players to continue the match").printStackTrace();
                    return;
                }
            }

            try {
                sentStringToClient(client1, "shoot");
                //Empfange "Shoot"
                int a = client1.dataInputStream.readInt();
                int b = client1.dataInputStream.readInt();

                //Sende zu anderem Client "checkHit"
                sentStringToClient(client2, "checkHit");
                sentIntToClient(client2, a);
                sentIntToClient(client2, b);
                //Empfange  "checkHit"
                boolean bool = receiveBool(client2);
                //Speicher
                if (bool) {
                    client2.health--;
                }
                //Sende anwort an 1ten Client
                sentBoolToClient(client1, bool);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (client1 == clients.get(1)) {
                client1 = clients.get(0);
                client2 = clients.get(1);
            } else {
                client1 = clients.get(1);
                client2 = clients.get(0);
            }


        }
        //Ende
        sentStringToAll("gameEnd");
        sentStringToAll(client2.name);
        System.out.println("[Server] Game finished, Server closing...");
    }

    private boolean oneClientDead() {
        for (client client : clients) {
            if (client.health <= 0) {
                return true;
            }
        }
        return false;
    }

    private String receiveUTF(Socket socket) {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            return dataInputStream.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean receiveBool(client client) {
        try {
            return client.dataInputStream.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sentBoolToClient(client client, boolean b) {
        try {
            client.dataOutputStream.writeBoolean(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sentIntToClient(client client, int integer) {
        try {
            client.dataOutputStream.writeInt(integer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sentStringToAll(String str) {
        for (client client : clients) {
            sentStringToClient(client, str);
        }
    }

    private void sentStringToClient(client client, String str) {
        try {
            client.dataOutputStream.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
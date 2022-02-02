package client;

import java.net.Socket;

public class client {

    Socket clientSocket;

    String name;
    int[][] shipField = new int[10][10];
    char[][] targetField = new char[10][10];
    int[] maxShips = {1, 2, 3, 4};
    int[] currentShips = new int[4];

    public client(Socket socket)
    {
        clientSocket = socket;
    }
}

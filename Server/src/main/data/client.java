package data;

import java.net.Socket;

public class client {

    final Socket clientSocket;

    final String name;
    int[][] shipField = new int[10][10];
    char[][] targetField = new char[10][10];
    int[] maxShips = {1, 2, 3, 4};
    int[] currentShips = new int[4];

    public client(Socket socket, String name) {
        clientSocket = socket;
        this.name = name;
    }

    public Socket Socket() {
        return clientSocket;
    }

    public String name() {
        return name;
    }

    public int[][] shipField() {
        return shipField;
    }


    public char[][] targetField() {
        return targetField;
    }


    public int[] maxShips() {
        return maxShips;
    }

    public client setShipField(int[][] shipField) {
        this.shipField = shipField;
        return this;
    }

    public client setTargetField(char[][] targetField) {
        this.targetField = targetField;
        return this;
    }

    public client setMaxShips(int[] maxShips) {
        this.maxShips = maxShips;
        return this;
    }

    public client setCurrentShips(int[] currentShips) {
        this.currentShips = currentShips;
        return this;
    }
}

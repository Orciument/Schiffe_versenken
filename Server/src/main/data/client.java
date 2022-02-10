package data;

import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;

public class client {

    final Socket clientSocket;

    final String name;
    int[][] shipField = new int[10][10];
    char[][] targetField = new char[10][10];
    int[] maxShips = {1, 2, 3, 4};
    int[] currentShips = new int[4];

    public void addShip(int size, int x, int y, String direction) throws RejectedExecutionException, IllegalArgumentException {
        //b ≠ y -> b==0 is at the top
        int a = shipField[0].length - y;
        int b = x;
        b--;

        //Exceptions
        if (shipField.length < a) {
            throw new IllegalArgumentException("Unexpected value: " + a);
        } else if (shipField[0].length < b) {
            throw new IllegalArgumentException("Unexpected value: " + b);
        } else if (size < 1 || size > 4) {
            throw new IllegalArgumentException("Unexpected value: " + size);
        }

        for (int i = 0; i < size; i++) {
            switch (direction) {
                case "oben", "up" -> {
                    if (shipField[a - i][b] != 0) {
                        throw new RejectedExecutionException();
                    }
                    shipField[a - i][b] = size;
                }
                case "rechts", "right" -> {
                    if (shipField[a - i][b] != 0) {
                        throw new RejectedExecutionException();
                    }
                    shipField[a][b + i] = size;
                }
                case "links", "left" -> {
                    if (shipField[a - i][b] != 0) {
                        throw new RejectedExecutionException();
                    }
                    shipField[a][b - i] = size;
                }
                case "unten", "down" -> {
                    if (shipField[a - i][b] != 0) {
                        throw new RejectedExecutionException();
                    }
                    shipField[a + i][b] = size;
                }
            }
        }
        currentShips[size - 1]++;
    }

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

    public Socket clientSocket() {
        return clientSocket;
    }

    public int[][] shipField() {
        return shipField;
    }

    public void setShipField(int[][] shipField) {
        this.shipField = shipField;
    }

    public char[][] targetField() {
        return targetField;
    }

    public void setTargetField(char[][] targetField) {
        this.targetField = targetField;
    }

    public int[] maxShips() {
        return maxShips;
    }

    public void setMaxShips(int[] maxShips) {
        this.maxShips = maxShips;
    }

    public int[] currentShips() {
        return currentShips;
    }

    public void setCurrentShips(int[] currentShips) {
        this.currentShips = currentShips;
    }
}

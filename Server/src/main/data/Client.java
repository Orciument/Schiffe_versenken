package main.data;

import main.ressources.Exceptions.ShipAlreadyThereException;

import java.net.Socket;

public class Client {

    final Socket clientSocket;

    final String name;
    char[][] shipField = new char[10][10];
    /*
    Does not say what ship there is, but only THAT there is one, or was one
        'S'= Ship is there
        'W'= Ship was there and was hit
     */
    final int[] maxShips = {0,0,0,1};
    int[] currentShips = new int[4];
    int lives;

    public void addShip(int size, int x, int y, String direction) throws ShipAlreadyThereException, IllegalArgumentException {
        //b ≠ y -> b==0 is at the top
        int a = shipField[0].length - y;
        int b = x;
        b--;

        //Exceptions
        if (shipField.length < a) {
            throw new IllegalArgumentException("Unexpected value: " + a);
        } else if (shipField[0].length < b) {
            throw new IllegalArgumentException("Unexpected value: " + b);
        } else if (size < 1 | size > 4) {
            throw new IllegalArgumentException("Unexpected value: " + size);
        }

        for (int i = 0; i < size; i++) {
            switch (direction) {
                case "oben", "up" -> {
                    if (shipField[a - i][b] != 0) {
                        throw new ShipAlreadyThereException();
                    }
                    shipField[a - i][b] = 'S';
                }
                case "rechts", "right" -> {
                    if (shipField[a][b + i] != 0) {
                        throw new ShipAlreadyThereException();
                    }
                    shipField[a][b + i] = 'S';
                }
                case "links", "left" -> {
                    if (shipField[a][b - i] != 0) {
                        throw new ShipAlreadyThereException();
                    }
                    shipField[a][b - i] = 'S';
                }
                case "unten", "down" -> {
                    if (shipField[a + i][b] != 0) {
                        throw new ShipAlreadyThereException();
                    }
                    shipField[a + i][b] = 'S';
                }
            }
        }
        lives += size;
        currentShips[size - 1]++;
    }

    public Client(Socket socket, String name) {
        clientSocket = socket;
        this.name = name;
    }

    public Object name() {
        return name;
    }

    public Socket clientSocket() {
        return clientSocket;
    }

    public char[][] shipField() {
        return shipField;
    }

    public int[] maxShips() {
        return maxShips;
    }

    public int[] currentShips() {
        return currentShips;
    }

    public int lives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
}

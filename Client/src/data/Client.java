package data;

import ressources.Exceptions.ShipAlreadyThereException;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private Socket socket = new Socket();
    private final String name;
    private final char[][] shipField = new char[10][10];
    private final char[][] targetField = new char[10][10];
    private boolean onTurn;

    public Client() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bitte gebe einen Spieler Namen ein:");
        name = scanner.nextLine();
        System.out.println("Name bestätigt: " + name);

        socket = connect(socket);

        fillField(targetField);
        fillField(shipField);
    }

    private Socket connect(Socket socket) {
        Scanner scanner = new Scanner(System.in);
        String hostname;
        int port;

        System.out.println("Bitte geben sie die Server Adresse ein: ");
        String input = scanner.nextLine();
        if (input.contains(".")) {
            //Input is IPV4 Address
            if (input.contains(":")) {
                //Input is Hostname:Port
                hostname = input.substring(0, input.indexOf(":"));
                port = Integer.parseInt(input.substring(input.indexOf(":") + 1));
            } else {
                //Input is just the Hostname
                hostname = input;
                port = choosePort();
            }
            try {
                socket.connect(new InetSocketAddress(hostname, port));
                return socket;

            } catch (IOException e) {
                System.out.println("Error, failed to establish connection");
                connect(socket);
            }

        } else {
            //Input is IPV6
            InetAddress inetAddress;
            try {
                inetAddress = Inet6Address.getByName(input);
                port = Integer.parseInt(inetAddress.toString().substring(inetAddress.toString().lastIndexOf(":") + 1));

                try {

                    socket.connect(new InetSocketAddress(inetAddress.getHostName(), port));
                    return socket;
                } catch (IOException e) {
                    System.out.println("Error, failed to establish connection");
                    connect(socket);
                }
            } catch (UnknownHostException e) {
                System.out.println(e);
                connect(socket);
            }

        }
        return socket;
    }

    private int choosePort() {
        int port;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bitte geben sie den Ziel Port ein: ");
        try {
            port = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Port muss eine Nummer sein: ");
            port = choosePort();
        }
        return port;
    }

    private void fillField(char[][] field) {
        for (char[] chars : field) {
            Arrays.fill(chars, '0');
        }
    }

    public void addWreck(int x, int y) {
        //b ≠ y -> b==0 is at the top
        int a = shipField[0].length - y;
        int b = x;
        b--;

        //Exceptions
        if (shipField.length < a) {
            throw new IllegalArgumentException("Unexpected value: " + a);
        } else if (shipField[0].length < b) {
            throw new IllegalArgumentException("Unexpected value: " + b);
        }

        shipField[a][b] = 'W';
    }

    public void addHit(int x, int y) {
        //b ≠ y -> b==0 is at the top
        int a = targetField[0].length - y;
        int b = x;
        b--;

        //Exceptions
        if (targetField.length < a) {
            throw new IllegalArgumentException("Unexpected value: " + a);
        } else if (targetField[0].length < b) {
            throw new IllegalArgumentException("Unexpected value: " + b);
        }

        targetField[a][b] = 'X';
    }


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
        } else if (size < 1 || size > 4) {
            throw new IllegalArgumentException("Unexpected value: " + size);
        }

        for (int i = 0; i < size; i++) {
            switch (direction) {
                case "oben", "up" -> {
                    if (shipField[a - i][b] != '0') {
                        throw new ShipAlreadyThereException();
                    }
                    shipField[a - i][b] = Character.forDigit(size,10);
                }
                case "rechts", "right" -> {
                    if (shipField[a][b + i] != '0') {
                        throw new ShipAlreadyThereException();
                    }
                    shipField[a][b + i] = Character.forDigit(size,10);
                }
                case "links", "left" -> {
                    if (shipField[a][b - i] != '0') {
                        throw new ShipAlreadyThereException();
                    }
                    shipField[a][b - i] = Character.forDigit(size,10);
                }
                case "unten", "down" -> {
                    if (shipField[a + i][b] != '0') {
                        throw new ShipAlreadyThereException();
                    }
                    shipField[a + i][b] = Character.forDigit(size,10);
                }
            }
        }
    }

    public Socket socket() {
        return socket;
    }

    public String name() {
        return name;
    }

    public char[][] shipField() {
        return shipField;
    }

    public char[][] targetField() {
        return targetField;
    }

    public boolean onTurn() {
        return onTurn;
    }

    public void setOnTurn(boolean onTurn) {
        this.onTurn = onTurn;
    }


}

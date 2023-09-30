package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;


public class client {

    public static void main(String[] args) {
        new client();
    }

    Socket socket = new Socket();
    InetSocketAddress serverAddress;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    BufferedInputStream bufferedInputStream;

    Thread threadNetworkListener;
    Thread threadConsoleListener;
    boolean run = true;
    String gamestate;
    Scanner scanner = new Scanner(System.in);

    String name;
    int[][] shipField = new int[10][10];
    char[][] targetField = new char[10][10];
    int[] maxShips = {0, 0, 0, 1};
    int[] currentShips = new int[4];


    public client() {
        System.out.println("[Client] Bitte gebe einen Namen ein:");
        name = scanner.nextLine();
        System.out.println("[Client] Name: " + name);
        /*
        System.out.println("[Client] Bitte gebe eine Server IP an:");
        String hostname = scanner.nextLine();
        System.out.println("[Client] Bitte gebe den Port an:");
        int port = scanner.nextInt();
        serverAddress = new InetSocketAddress(hostname, port);
        System.out.println("[Client] Die Server Address ist: " + serverAddress);
        */
        serverAddress = new InetSocketAddress("127.0.0.1", 8000);
        connectToServer(serverAddress);
        sentUTF(name);
        consoleListener();
        networkListener();
        System.out.println("Main Thread ende");
    }

    public void connectToServer(InetSocketAddress address) {

        try {
            try { //Tries to connect to the Server, when no Server can be reached a one is created at that Port
                socket.connect(address, 5000);
                System.out.println("Connected");
            } catch (ConnectException e) {
                e.printStackTrace();
                System.out.println("[Client] Cant connect to the Server...");
                run = false;
                return;
            }
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedInputStream = new BufferedInputStream(dataInputStream);
            System.out.println("[Client] successfully connected");
        } catch (IOException e) {
            e.printStackTrace();
            run = false;
        }


    }

    public void placeShip(int size, int x, int y, String direction) {
        //b ≠ y -> b==0 is at the top
        int a = shipField[0].length - y;
        int b = x;
        b--;

        //Exceptions
        if (!gamestate.equals("preparation")) {
            System.out.println("[Game] Unable to place Ship. Game has already started");
            return;
        } else if (shipField.length < a) {
            throw new IllegalStateException("Unexpected value: " + a);
        } else if (shipField[0].length < b) {
            throw new IllegalStateException("Unexpected value: " + b);
        } else if (size < 1 || size > 4) {
            throw new IllegalStateException("Unexpected value: " + size);
        } else if (currentShips[size - 1] >= maxShips[size - 1]) {
            System.out.println("Du hast bereits alle Schiffe der Größe plaziert!");
            return;
        }

        System.out.println("Placing new Ship. Size:" + size + " X:" + x + " Y:" + y + " Richtung: " + direction);
        direction = direction.toLowerCase();
        switch (direction) {
            case "left":
            case "links":
                for (int i = 0; i < size; i++) {
                    shipField[a][b - i] = size;
                }
                break;
            case "right":
            case "rechts":
                for (int i = 0; i < size; i++) {
                    shipField[a][b + i] = size;
                }
                break;
            case "down":
            case "unten":
                for (int i = 0; i < size; i++) {
                    shipField[a + i][b] = size;
                }
                break;
            case "up":
            case "oben":
                for (int i = 0; i < size; i++) {
                    shipField[a - i][b] = size;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }

        currentShips[size - 1]++;
        System.out.println("Dein aktualisiertes Feld:");
        printShipField();
        prepTest();
    } //Places Ship

    public void prepTest() {
        boolean prepfinished = true;

        for (int i = 0; i < 4; i++) {
            if (currentShips[i] < maxShips[i]) {
                prepfinished = false;
                break;
            }
        }
        if (prepfinished) {
            sentUTF("ready");
            System.out.println("Ready gesendet");
        }
    } //Checks if every possible ship has been placed

    public void checkHit(int a, int b) {
        if (shipField[a][b] > 0) {
            sentBool(true);
            System.out.println("Du wurdest getroffen");
        } else {
            sentBool(false);
        }
    } //Checks if there was a ship at the transferred Coordinates

    public void shot() {
        printTargetField();
        System.out.println("Bitte geben sie die Zielkoordinaten ein: [X Y]");
        int x = scanner.nextInt();
        int y = scanner.nextInt();

        //b ≠ y -> b==0 is at the top
        y--;
        int a = shipField[0].length - x;
        int b = y;

        //Exceptions
        if (shipField.length < a) {
            throw new IllegalStateException("Unexpected value: " + a);
        } else if (shipField[0].length < b) {
            throw new IllegalStateException("Unexpected value: " + b);
        }

        sentInt(a);
        sentInt(b);

        boolean bool = receiveBool();
        if (bool) {
            targetField[a][b] = 1;
            System.out.println("Du hast getroffen!");
        } else {
            System.out.println("Du hast NICHT getroffen :(");
        }
    } //

    public void end(String Name) {
        System.out.println("Game has Ended!");
        System.out.println(name + " Won!");
        run = false;
        try {
            socket.close();
            scanner.close();
            threadNetworkListener.interrupt();
            threadConsoleListener.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Prints the Field in the Console
    public void printShipField() {
        for (int j = 0; j < shipField.length; j++) {
            for (int i = 0; i < shipField[j].length; i++) {

                System.out.print("  " + shipField[j][i]);
            }
            System.out.println();
        }
    }

    public void printTargetField() {
        System.out.println("Gegenerfeld: ");
        int c = 0;
        for (int j = 0; j < targetField.length; j++) {
            for (int i = 0; i < targetField[j].length; i++) {
                System.out.print("  " + targetField[j][i]);
                c++;
            }
            System.out.println();
        }
    }

    //receive From the Server
    private boolean receiveBool() {
        try {
            return dataInputStream.readBoolean();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Send to the Server
    public void sentInt(int integer) {
        try {
            dataOutputStream.writeInt(integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sentUTF(String str) {
        try {
            dataOutputStream.writeUTF(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sentBool(boolean b) {
        try {
            dataOutputStream.writeBoolean(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Listener
    public void networkListener() {

        threadNetworkListener = new Thread(() -> {
            System.out.println("[Client] Started new NetworkListener in a new Thread");
            while (run) {
                try {
                    //Checks for different Messages from the server
                    String input = dataInputStream.readUTF();
                    if (input.equals("prepStart")) {
                        gamestate = "preparation";
                        System.out.println();
                        System.out.println();
                        System.out.println();
                        System.out.println("-----------------------------------------------------------------");
                        System.out.println("Round Starts. Please place you Ships");
                        printShipField();
                    }
                    if (input.equals("checkHit")) {
                        checkHit(dataInputStream.readInt(), dataInputStream.readInt());
                    }
                    if (input.equals("shot")) {
                        shot();
                    }
                    if (input.equals("gameEnd")) {
                        end(dataInputStream.readUTF());
                        gamestate = "end";
                        run = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threadNetworkListener.start();
    }

    public void consoleListener() {
        threadConsoleListener = new Thread(() -> {
            System.out.println("[Client] Started new ConsoleListener in a new Thread");
            try {
                String str = scanner.next();
                if (str.equals("test")) {
                    sentUTF("ready");
                }

                if (str.equals("placeship") || str.equals("placeShip") || str.equals("newship") || str.equals("newShip")) {
                    placeShip(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.next());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        threadConsoleListener.start();
    }


    //Listens for messages from the Server
    public void listener() {

        while (run) {
            try {
                //Checks for different Messages from the server
                if (dataInputStream.available() != 0) {
                    String input = dataInputStream.readUTF();
                    if (input.equals("prepStart")) {
                        gamestate = "preparation";
                        System.out.println("Round Starts. Please place you Ships");
                        printShipField();
                    }
                    if (input.equals("checkHit")) {
                        checkHit(dataInputStream.readInt(), dataInputStream.readInt());
                    }
                    if (input.equals("shot")) {
                        shot();
                    }
                    if (input.equals("gameEnd")) {
                        end(dataInputStream.readUTF());
                        gamestate = "end";
                        run = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Checks for Commands from the console
            if (scanner.hasNext()) {

                try {
                    String str = scanner.next();
                    if (str.equals("test")) {
                        sentUTF("ready");
                    }

                    if (str.equals("placeship") || str.equals("placeShip") || str.equals("newship") || str.equals("newShip")) {
                        placeShip(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.next());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;


@SuppressWarnings({"SpellCheckingInspection", "CanBeFinal"})
public class client {

    public static void main(String[] args) {
        new client();
    }

    final Socket socket = new Socket();
    final InetSocketAddress serverAddress;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    BufferedInputStream bufferedInputStream;

    Thread threadNetworkListener;
    Thread threadConsoleListener;
    boolean networkListenerRun = true;
    boolean consoleListenerRun = true;
    String gamestate = "noGame";
    Scanner scanner = new Scanner(System.in);

    String name;
    int[][] shipField = new int[10][10];
    char[][] targetField = new char[10][10];
    int[] maxShips = {1, 2, 3, 4};
    int[] currentShips = new int[4];


    public client() {
        System.out.println("[Client] Bitte gebe einen Namen ein:");
        name = scanner.nextLine();
        System.out.println("[Client] Name: " + name);
        System.out.println("[Client] Bitte gebe eine Server IP an:");
        String hostname = scanner.nextLine();
        System.out.println("[Client] Bitte gebe den Port an:");
        int port = scanner.nextInt();
        serverAddress = new InetSocketAddress(hostname, port);
        System.out.println("[Client] Die Server Address ist: " + serverAddress);
        connectToServer(serverAddress);
        sentUTF(name);
        networkListener();
        consoleListener();
        System.out.println("[Client] Warte bis die Lobby voll ist...");
    }

    public void connectToServer(InetSocketAddress address) {

        try {
            try { //Tries to connect to the Server, when no Server can be reached a one is created at that Port
                socket.connect(address, 5000);
            } catch (ConnectException e) {
                e.printStackTrace();
                System.out.println("[Client] Kann nicht zum Server verbinden...");
                networkListenerRun = false;
                return;
            }
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedInputStream = new BufferedInputStream(dataInputStream);
            System.out.println("[Client] Verbindung erfolgreich aufgebaut");
        } catch (IOException e) {
            e.printStackTrace();
            networkListenerRun = false;
        }


    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public void placeShip(int size, int x, int y, String direction) {
        //b ≠ y -> b==0 is at the top
        int a = shipField[0].length - y;
        int b = x;
        b--;

        //Exceptions
        if (!gamestate.equals("preparation")) {
            System.out.println("[Game] Es ist nicht möglich weitere Schiffe zu plazieren, das Spiel hat bereits begonnen");
            return;
        } else if (shipField.length < a) {
            throw new IllegalStateException("Unexpected value: " + a);
        } else if (shipField[0].length < b) {
            throw new IllegalStateException("Unexpected value: " + b);
        } else if (size < 1 || size > 4) {
            throw new IllegalStateException("Unexpected value: " + size);
        } else if (currentShips[size - 1] >= maxShips[size - 1]) {
            System.out.println("[Game] Du hast bereits alle Schiffe der Größe platziert!");
            return;
        }

        System.out.println("[Game] Platziere neues Schiff. Größe:" + size + " X:" + x + " Y:" + y + " Richtung: " + direction);
        direction = direction.toLowerCase();
        switch (direction) {
            case "left":
                for (int i = 0; i < size; i++) {
                    shipField[a][b - i] = size;
                }
                break;
            case "links":
                for (int i = 0; i < size; i++) {
                    shipField[a][b - i] = size;
                }
                break;
            case "right":
                for (int i = 0; i < size; i++) {
                    shipField[a][b + i] = size;
                }
                break;
            case "rechts":
                for (int i = 0; i < size; i++) {
                    shipField[a][b + i] = size;
                }
                break;
            case "unten":
                for (int i = 0; i < size; i++) {
                    shipField[a + i][b] = size;
                }
                break;
            case "down":
                for (int i = 0; i < size; i++) {
                    shipField[a + i][b] = size;
                }
                break;
            case "oben":
                for (int i = 0; i < size; i++) {
                    shipField[a - i][b] = size;
                }
                break;
            case "up":
                for (int i = 0; i < size; i++) {
                    shipField[a - i][b] = size;
                }
                break;
            default:
                throw new IllegalStateException("Ungültiger Wert: " + direction);
        }

        currentShips[size - 1]++;
        System.out.println("[Game] Dein aktualisiertes Feld:");
        printShipField();
        prepTest();
    } //Places Ship

    public void prepTest() {
        boolean prepfinished = true;
        //Checks if every possible ship has been placed
        for (int i = 0; i < 4; i++) {
            if (currentShips[i] < maxShips[i]) {
                prepfinished = false;
                break;
            }
        }
        if (prepfinished) {
            sentUTF("ready");
            System.out.println("[Game] Alle Schiffe platziert. Warte auf Spielstart...");
            consoleListenerRun = false;
            threadConsoleListener.interrupt();
            for (char[] chars : targetField) {
                Arrays.fill(chars, '0');
            }
        }
    }

    public void checkHit(int a, int b) {
        if (shipField[a][b] > 0) {
            sentBool(true);
            System.out.println("[Game] Du wurdest getroffen :(");
        } else {
            sentBool(false);
        }
    } //Checks if there was a ship at the transferred Coordinates

    public void shot() {
        System.out.println("[Game] Du bist nun wieder am Zug:");
        System.out.println();
        printTargetField();
        System.out.println("[Game] Bitte geben sie die Zielkoordinaten ein:");
        System.out.println("[Game] X-Zielkoordinaten:");
        int x = scanner.nextInt();
        System.out.println("[Game] Y-Zielkoordinaten:");
        int y = scanner.nextInt();

        x--;
        int a = shipField[0].length - y;
        int b = x;

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
            targetField[a][b] = 'X';
            System.out.println("[Game] Du hast getroffen!");
        } else {
            System.out.println("[Game] Du hast NICHT getroffen :(");
        }
        System.out.println();
        System.out.println();
        System.out.println("[Game] Gegner ist am zug...");
        System.out.println();
    }

    public void end(String Name) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("[Game] Das Spiel ist zu Ende!");
        System.out.println("[Game] " + Name + " hat gewonnen!");
        networkListenerRun = false;
        try {
            socket.close();
            scanner.close();
            threadNetworkListener.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Prints the Field in the Console
    public void printShipField() {
        //noinspection ForLoopReplaceableByForEach
        for (int j = 0; j < shipField.length; j++) {
            for (int i = 0; i < shipField[j].length; i++) {

                System.out.print("  " + shipField[j][i]);
            }
            System.out.println();
        }
    }

    public void printTargetField() {
        System.out.println("[Game] Gegner Feld: ");
        //noinspection ForLoopReplaceableByForEach
        for (int j = 0; j < targetField.length; j++) {
            for (int i = 0; i < targetField[j].length; i++) {
                System.out.print("  " + targetField[j][i]);
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
            while (networkListenerRun) {
                try {
                    //Checks for different Messages from the server
                    String input = dataInputStream.readUTF();
                    if (input.equals("gameStart")) {
                        gamestate = "mainGame";
                        consoleListenerRun = false;
                        System.out.println();
                        System.out.println();
                        System.out.println();
                        System.out.println("-----------------------------------------------------------------");
                        System.out.println("Runde startet:");
                    }
                    if (input.equals("prepStart")) {
                        gamestate = "preparation";
                        System.out.println();
                        System.out.println();
                        System.out.println();
                        System.out.println("-----------------------------------------------------------------");
                        System.out.println("Bitte plaziere deine Schiffe");
                        printShipField();
                    }
                    if (input.equals("checkHit")) {
                        checkHit(dataInputStream.readInt(), dataInputStream.readInt());
                    }
                    if (input.equals("shoot")) {
                        shot();
                    }
                    if (input.equals("gameEnd")) {
                        end(dataInputStream.readUTF());
                        gamestate = "end";
                        networkListenerRun = false;
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
            while (consoleListenerRun) {
                try {
                    String str = scanner.next();
                    if (str.equals("placeship") || str.equals("placeShip") || str.equals("newship") || str.equals("newShip")) {
                        placeShip(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.next());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threadConsoleListener.start();
    }
}
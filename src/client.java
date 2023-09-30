import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class client {

    public static void main(String[] args) {
        new Thread() {
            client newClient = new client("localhost", 8000, "Client1");
        };
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread() {
            client newClient2 = new client("localhost", 8000, "Client2");
        };
    }

    Socket socket;
    InetSocketAddress address;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;

    Scanner Scanner = new Scanner(System.in);

    String name;
    int[] shipcount = new int[5];
    int[] maxShipCount;
    private int[][] field;
    String gameState = "nogame";

    public client(String hostname, int port, String name) {

        address = new InetSocketAddress(hostname, port);
        this.name = name;
        //Establishing a connection to the server
        try {
            socket = new Socket();
            socket.connect(address, 5000);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Connection Check
        if (Objects.equals(recifeString(), "Accepted!")) {
            System.out.println("[Client] Connection succesfuly established");
        } else {
            System.out.println("[Client] Error");
            return;
        }

        //Preparation Phase, Ship Placement
        //wait until Game Starts
        if (recifeString() == "Preperation") {
            gameState = "Preperation";
            maxShipCount = recifeIntArray();
        } else {
            System.out.println("[Client] Game Phase Error");
            return;
        }

    }

    public boolean hit(int x, int y) {
        return false;
    }

    public void shot(int x, int y) {

    }

    public String recifeString() {
        try {
            return dataInputStream.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int recifeInt() {
        try {
            return dataInputStream.readInt();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int[] recifeIntArray() {
        int[] intArray = new int[recifeInt()];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = recifeInt();
        }
        return intArray;
    }
}

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class server {

    public static void main(String[] args) {
        server newServer = new server(8000);
    }


    private ServerSocket serverSocket;
    ArrayList<server_client> clientList = new ArrayList<>();
    int maxPlayer = 2;
    int[] maxShipCount = {10, 4, 3, 2, 1};


    public server(int port) {
        //Start Server and fill Lobby
        try {
            //Open a new Serversocket
            serverSocket = new ServerSocket(port);
            System.out.println("[Server] Server started...");
            for (int i = 0; i < maxPlayer; i++) {
                //Establish connection with each client and stores the information
                System.out.println("[Server] waiting for clients...");
                Socket newClientSocket = serverSocket.accept();
                clientList.add(new server_client(new InetSocketAddress(newClientSocket.getInetAddress(), newClientSocket.getPort()), newClientSocket));
                clientList.get(i).dataOutputStream.writeUTF("Accepted!");
            }
            System.out.println("[Game] Lobby full. Preparation phase starts.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Starts the different Game phases
        preparation();
        System.out.println("[Game] Preparations completed, game starting.");
        play();
        System.out.println("[Game] Game finished.");
    }

    //handels the game, until every player has placed the maximum number of ships allowed
    public void preparation() {
        sentStringToAllClients("Preperation");
        sentIntArrayToAllClients(maxShipCount);
    }

    //handels the main part of the game
    public void play() {
    }

    public void sentIntArrayToAllClients(int[] intArray) {
        //Sends every String in the String Array to each Client
        sentIntToAllClients(intArray.length);
        for (int i = 0; i < intArray.length; i++) {
            sentIntToAllClients(intArray[i]);
        }
    }

    public void sentIntToAllClients(int integer) {
        try {
            for (server_client client : clientList) {
                client.dataOutputStream.writeInt(integer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sentStringToAllClients(String str) {
        try {
            for (server_client client : clientList) {
                client.dataOutputStream.writeUTF(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

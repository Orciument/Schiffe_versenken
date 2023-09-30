package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class client {
    //Networking
    private InetSocketAddress address;
    public Socket socket;
    public DataOutputStream dataOutputStream;
    public DataInputStream dataInputStream;

    //Game
    String name;
    int health = 20;

    public client(InetSocketAddress address, Socket socket, String name) {
        this.address = address;
        this.socket = socket;
        this.name = name;

        try {
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
            dataInputStream = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

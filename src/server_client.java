import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class server_client {
    //Networking
    private InetSocketAddress address;
    public Socket socket;
    public DataOutputStream dataOutputStream;
    public DataInputStream dataInputStream;

    //Game
    int shipcount = 0;

    public server_client(InetSocketAddress address, Socket socket) {
        this.address = address;
        this.socket = socket;

        try {
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
            dataInputStream = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package listener;

import main.server;

import java.net.ServerSocket;
import java.net.Socket;

public class acceptListener extends Thread{
    server server;

    public acceptListener(ServerSocket serverSocket, server server)
    {
        this.server = server;
        listener();
    }

    public void listener()
    {
        while (server.run)
            try {
                Socket newClientSocket = server.serverSocket.accept();
                server.newClient(newClientSocket);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

    }

    @Override
    public void run() {
        listener();
    }
}

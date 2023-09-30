package server;

import java.io.DataInputStream;
import java.io.IOException;

public class listenerThread extends Thread{

    client client;
    String name;

    public listenerThread(client client, String name)
    {
        this.name = name;
        this.client = client;
        start();
    }

    @Override
    public void run() {
        System.out.println("[Listener] Opening new Thread. Clientname: " + name);
        listener(client.dataInputStream);
    }

    public void listener(DataInputStream inputStream)
    {
        try {
            // Wartet bis irgendwann die richtige Nachricht kommt
            while (true)
            {
                if (inputStream.available() != 0)
                {
                    if (inputStream.readUTF().equals("ready"))
                    {
                        break;
                    }

                }
            }
            System.out.println("[Thread] exiting...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

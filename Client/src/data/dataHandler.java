package data;

public class dataHandler {
    client client; //Representation of its own
    server server; //Representation of the connected server

    public dataHandler() {
        client = new client();
    }

    public boolean getRUN()
    {
        return server.run();
    }

    public client getClient()
    {
        return client;
    }
}

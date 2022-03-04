package data;

public class dataHandler {
    client client; //Representation of its own
    server server; //Representation of the connected server

    public dataHandler() {
        server = new server();
        client = new client();
        setGameState(1);
    }

    public int getGameState()
    {
        return server.gameState();
    }

    public void setGameState(int gameState)
    {
        server.setGameState(gameState);
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

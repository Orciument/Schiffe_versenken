package main.data;

import java.util.Locale;

public class DataHandler {
    private final Client client; //Representation of its own
    private final Server server; //Representation of the connected server
    private boolean debug = false;

    public DataHandler() {
        server = new Server();
        client = new Client();
        if (client.name().toLowerCase(Locale.ROOT).contains("debug")) {
            debug = true;
        }
        setGameState(1);
    }

    public boolean debug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getGameState() {
        return server.gameState();
    }

    public void setGameState(int gameState) {
        server.setGameState(gameState);
    }

    public boolean getRUN() {
        return server.run();
    }

    public Client getClient() {
        return client;
    }
}

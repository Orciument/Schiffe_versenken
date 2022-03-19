package main.data;

import java.util.Locale;

public class DataHandler {
    private final Client client; //Representation of its own
    private boolean debug = false;
    private boolean run = true;
    int gameState = 0;
    /*
    0 = Vorm Spiel/Server Start
    1 = Vor der Runde, Joinen
    2 = In der Runde
    3 = Nach der Runde
    */

    public DataHandler() {
        client = new Client();
        if (client.name().toLowerCase(Locale.ROOT).contains("debug")) {
            debug = true;
        }
        setGamePhase(1);
    }

    public boolean debug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getGamePhase() {
        return gameState;
    }

    public void setGamePhase(int gameState) {
        this.gameState = gameState;
    }

    public void setRUN(boolean run) {
        this.run = run;
    }

    public boolean getRUN() {
        return run;
    }

    public Client getClient() {
        return client;
    }
}

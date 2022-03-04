package data;

import java.util.Objects;

public final class server {
    private int gameState; /*
    0 = Vorm Spiel/Server Start
    1 = Vor der Runde, Joinen
    2 = In der Runde
    3 = Nach der Runde */
    private boolean run;

    public server() {
        gameState = 0;
        run = true;
    }

    public int gameState() {
        return gameState;
    }

    public boolean run() {
        return run;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (server) obj;
        return this.gameState == that.gameState &&
                this.run == that.run;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameState, run);
    }

    @Override
    public String toString() {
        return "server[" +
                "gameState=" + gameState + ", " +
                "run=" + run + ']';
    }

}

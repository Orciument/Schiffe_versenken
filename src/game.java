import java.util.List;

public class game {
    private List<Player> playerList;
    private boolean gameStarted = false;
    //Rules
    private int ship4x =1; //Allowed Number of Ships with the Dimensions 4 by 1
    private int ship3x =2;
    private int ship2x =3;
    private int ship1x =4;
    private int x = 10; //Width of the Match-field
    private int y = 10; //Height of the Match-field

    public game(int ship1x, int ship2x, int ship3x, int ship4x, int x, int y)
    { // New Game with Custom Rules
        this.ship1x = ship1x;
        this.ship2x = ship2x;
        this.ship3x = ship3x;
        this.ship4x = ship4x;
        this.x = x;
        this.y = y;
    }

    public game ()
    { //New Game

    }

    public void joinGame(Player newPlayer)
    {
        if(gameStarted == true)
        {
            playerList.add(newPlayer);
        }
        System.out.println("Cannot Join Game. Game already started.");
    }

    public void startGame()
    {
        gameStarted = true;
        //Each Turn
        for(int i = 0; i < playerList.size(); i ++)
        {
            if(playerList.get(i).getShipCount() <= 0)
            {
                System.out.println("Game Over");
                return;
            }

        }
    }



    public int getShip1x() {
        return ship1x;
    }

    public int getShip2x() {
        return ship2x;
    }

    public int getShip3x() {
        return ship3x;
    }

    public int getShip4x() {
        return ship4x;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}


public class Player {
    private String name;
    private int[] [] matchtfield;
    private int shipCount = 0;

    public Player(String name, int x, int y)
    {
        this.name = name;
        matchtfield = new int[x][y];
    }

    public void placeShip(int size, int posx, int posy, String direction)
    {
        if(direction =="u")
        {
            for(int i = 0; i < size; i++)
            {
                matchtfield[posx][posy+i] = size;
                shipCount ++;
            }
        }
        if(direction =="l")
        {
            for(int i = 0; i < size; i++)
            {
                matchtfield[posx-i][posy] = size;
                shipCount ++;
            }
        }
        if(direction =="r")
        {
            for(int i = 0; i < size; i++)
            {
                matchtfield[posx+i][posy] = size;
                shipCount ++;
            }
        }
        if(direction =="d")
        {
            for(int i = 0; i < size; i++)
            {
                matchtfield[posx][posy-i] = size;
                shipCount ++;
            }
        }
    }

    public boolean hitDetection(int x, int y)
    {
        if(matchtfield[x][y] > 0)
        {
            return true;
        }
        else if(matchtfield[x][y] == 0)
        {
            return false;
        }
        System.out.println("ERROR: Field value is not => 0");
        new Error("Matchfield value is not => 0");
        return false;
    }

    public void shot(int x, int y)
    {

    }

    public String getName() {
        return name;
    }

    public int getShipCount() {
        return shipCount;
    }
}

package ressources;

import data.DataHandler;


public class Display {
    private static DataHandler dataHandler = null;

    public Display(DataHandler dataHandler)
    {
        this.dataHandler = dataHandler;
    }

    public static void update() {
        if (dataHandler.getGameState() == 1) {
            System.out.println("Dein Feld: ");
            print(dataHandler.getClient().shipField());
            System.out.println("Plaziere deine Schiffe: ");
        } else if (dataHandler.getGameState() == 2) {
            System.out.println("Dein Feld: ");
            print(dataHandler.getClient().shipField());
        }
        if (dataHandler.getGameState() == 2) {
            System.out.println("Gegner Feld: ");
            print(dataHandler.getClient().targetField());
        }
        if (dataHandler.getClient().onTurn()) {
            System.out.println("Schieße mit [Command]:");
        }
    }

    private static void print(char [][] field) {
        for (int i = 0; i < field.length; i++)
        {
            for (int p = 0; p < field[i].length; p++) {
                System.out.print(' ');
                System.out.print(' ');
                System.out.print(field[i][p]);
            }
            System.out.println();
        }
    }
}
package main.ressources;

import main.data.DataHandler;


public class Display {
    private static DataHandler dataHandler = null;

    public Display(DataHandler dataHandler) {
        Display.dataHandler = dataHandler;
    }

    public static void update() {
        if (dataHandler.getGamePhase() == 1) {
            System.out.println("Dein Feld: ");
            print(dataHandler.getClient().shipField());
            System.out.println("Platziere deine Schiffe: ");
        } else if (dataHandler.getGamePhase() == 2) {
            System.out.println("Dein Feld: ");
            print(dataHandler.getClient().shipField());
        }
        if (dataHandler.getGamePhase() == 2) {
            System.out.println("Gegner Feld: ");
            print(dataHandler.getClient().targetField());
        }
    }

    private static void print(char[][] field) {
        for (char[] chars : field) {
            for (char aChar : chars) {
                System.out.print("  ");
                System.out.print(aChar);
            }
            System.out.println();
        }
    }
}
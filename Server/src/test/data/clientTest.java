package data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class clientTest {
    client client;

    @BeforeEach
    void setUp() {
        client = new client(null, "Test");
    }

    @Test
    public void addShip()
    {
        printShipField();
        client.addShip(4,3,2,"rechts");
        System.out.println(" ------------------------------");
        printShipField();
    }



    public void printShipField() {
        for (int j = 0; j < client.shipField.length; j++) {
            for (int i = 0; i < client.shipField[j].length; i++) {

                System.out.print("  " + client.shipField[j][i]);
            }
            System.out.println();
        }
    }
}
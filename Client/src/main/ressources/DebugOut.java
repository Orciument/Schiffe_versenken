package main.ressources;

import main.data.DataHandler;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class DebugOut {
    private static DataHandler dataHandler = null;

    public DebugOut(DataHandler dataHandler) {
        DebugOut.dataHandler = dataHandler;
    }

    public static void debugOut(String... strings) {
        if (dataHandler.debug()) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
            String dateString = format.format(new Date());

            String arrayString = Arrays.toString(strings);
            arrayString = arrayString.substring(1, arrayString.length() - 1);
            System.out.println("<" + dateString + "> " + arrayString);
        }
    }
}

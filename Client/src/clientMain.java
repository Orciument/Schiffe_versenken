import data.dataHandler;
import listener.requestListener;
import ressources.protocol.messageEndpoint;

public class clientMain {

    public static void main(String[] args) {
        //Dialog Option, ob als Client, oder als Host gestartet werden soll
        dataHandler dataHandler = new dataHandler();
        //noinspection InstantiationOfUtilityClass
        new messageEndpoint(dataHandler);
        new requestListener(dataHandler).start();

    }
}

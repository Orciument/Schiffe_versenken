package protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class messageTimeoutCache {
    //TODO Should save messages and handels Retries
    ArrayList<packade> messageList = new ArrayList<>();

    public void sent(String message, DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        messageList.add(new packade(message, message.hashCode(), 0, System.currentTimeMillis()));
    }
    //TODO If a timeout runs out...


    static class packade {
        private final String message;
        private final int hashcode;
        private int retriesCounter;
        private long lastRetry;

        public packade(String message, int hashcode, int retriesCounter, long lastRetry) {
            this.message = message;
            this.hashcode = hashcode;
            this.retriesCounter = retriesCounter;
            this.lastRetry = lastRetry;
        }

        public String getMessage() {
            return message;
        }

        public int getHashcode() {
            return hashcode;
        }

        public int getRetriesCounter() {
            return retriesCounter;
        }

        public long getLastRetry() {
            return lastRetry;
        }

        public void setRetriesCounter(int retriesCounter) {
            this.retriesCounter = retriesCounter;
        }

        public void setLastRetry(long lastRetry) {
            this.lastRetry = lastRetry;
        }
    }
}

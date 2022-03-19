package main.ressources;

public class Exceptions {
    static public class MessageMissingArgumentsException extends Exception {
        public MessageMissingArgumentsException() {
        }

        public MessageMissingArgumentsException(String message) {
            super(message);
        }

        public MessageMissingArgumentsException(String message, Throwable cause) {
            super(message, cause);
        }

        public MessageMissingArgumentsException(Throwable cause) {
            super(cause);
        }

        public MessageMissingArgumentsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    static public class MessageProtocolVersionIncompatible extends Exception {
        public MessageProtocolVersionIncompatible() {
        }

        public MessageProtocolVersionIncompatible(String message) {
            super(message);
        }

        public MessageProtocolVersionIncompatible(String message, Throwable cause) {
            super(message, cause);
        }

        public MessageProtocolVersionIncompatible(Throwable cause) {
            super(cause);
        }

        public MessageProtocolVersionIncompatible(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    static public class ActionNotAllowedNow extends Exception {
        public ActionNotAllowedNow() {
        }

        public ActionNotAllowedNow(String message) {
            super(message);
        }

        public ActionNotAllowedNow(String message, Throwable cause) {
            super(message, cause);
        }

        public ActionNotAllowedNow(Throwable cause) {
            super(cause);
        }

        public ActionNotAllowedNow(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    static public class ShipAlreadyThereException extends Exception {
        public ShipAlreadyThereException() {
        }

        public ShipAlreadyThereException(String message) {
            super(message);
        }

        public ShipAlreadyThereException(String message, Throwable cause) {
            super(message, cause);
        }

        public ShipAlreadyThereException(Throwable cause) {
            super(cause);
        }

        public ShipAlreadyThereException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    static public class ConnectionResetByPeerException extends Exception {
        public ConnectionResetByPeerException() {
        }

        public ConnectionResetByPeerException(String message) {
            super(message);
        }

        public ConnectionResetByPeerException(String message, Throwable cause) {
            super(message, cause);
        }

        public ConnectionResetByPeerException(Throwable cause) {
            super(cause);
        }

        public ConnectionResetByPeerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}

package org.spbgu.pmpu.athynia.central.communications;

/**
 * User: vasiliy
 */
public class CommunicationException extends Exception {
    public CommunicationException() {
        super();
    }

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicationException(Throwable cause) {
        super(cause);
    }
}

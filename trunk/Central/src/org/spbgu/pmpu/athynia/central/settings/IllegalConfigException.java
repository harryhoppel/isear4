package org.spbgu.pmpu.athynia.central.settings;

/**
 * User: vasiliy
 */
public class IllegalConfigException extends Exception {
    public IllegalConfigException() {
        super();
    }

    public IllegalConfigException(String message) {
        super(message);
    }

    public IllegalConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalConfigException(Throwable cause) {
        super(cause);
    }
}

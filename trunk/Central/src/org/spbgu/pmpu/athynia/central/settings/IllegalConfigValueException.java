package org.spbgu.pmpu.athynia.central.settings;

public class IllegalConfigValueException extends Exception {
    public IllegalConfigValueException(String message) {
        super(message);
    }

    public IllegalConfigValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalConfigValueException(Throwable cause) {
        super(cause);
    }
}

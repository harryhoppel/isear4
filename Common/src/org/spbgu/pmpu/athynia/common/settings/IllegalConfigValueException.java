package org.spbgu.pmpu.athynia.common.settings;

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

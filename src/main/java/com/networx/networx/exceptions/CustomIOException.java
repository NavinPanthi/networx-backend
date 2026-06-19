package com.networx.networx.exceptions;

import java.io.IOException;

public class CustomIOException extends IOException {
    public CustomIOException(String message) {
        super(message);
    }

    public CustomIOException(String message, Throwable cause) {
        super(message, cause);
    }
}

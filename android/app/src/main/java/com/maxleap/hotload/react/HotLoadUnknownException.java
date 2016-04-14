package com.maxleap.hotload.react;

class HotLoadUnknownException extends RuntimeException {

    public HotLoadUnknownException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotLoadUnknownException(String message) {
        super(message);
    }
}
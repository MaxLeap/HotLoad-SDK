package com.maxleap.hotload.react;

import java.net.MalformedURLException;

public class HotLoadMalformedDataException extends RuntimeException {
    public HotLoadMalformedDataException(String path, Throwable cause) {
        super("Unable to parse contents of " + path + ", the file may be corrupted.", cause);
    }
    public HotLoadMalformedDataException(String url, MalformedURLException cause) {
        super("The package has an invalid downloadUrl: " + url, cause);
    }
}

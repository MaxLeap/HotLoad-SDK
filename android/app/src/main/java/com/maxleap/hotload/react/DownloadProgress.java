package com.maxleap.hotload.react;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

class DownloadProgress {
    private long totalBytes;
    private long receivedBytes;

    public DownloadProgress (long totalBytes, long receivedBytes){
        this.totalBytes = totalBytes;
        this.receivedBytes = receivedBytes;
    }

    public WritableMap createWritableMap() {
        WritableMap map = new WritableNativeMap();
        if (totalBytes < Integer.MAX_VALUE) {
            map.putInt("totalBytes", (int) totalBytes);
            map.putInt("receivedBytes", (int) receivedBytes);
        } else {
            map.putDouble("totalBytes", totalBytes);
            map.putDouble("receivedBytes", receivedBytes);
        }
        return map;
    }
}

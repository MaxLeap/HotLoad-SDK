package com.maxleap.hotload.react;

public enum HotLoadInstallMode {
    IMMEDIATE(0),
    ON_NEXT_RESTART(1),
    ON_NEXT_RESUME(2);

    private final int value;
    HotLoadInstallMode(int value) {
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }
}
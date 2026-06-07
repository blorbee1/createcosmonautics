package com.blorbee.createcosmonautics.system.thermal;

public enum ThermalContext {
    AMBIENT,
    ASCENT,
    REENTRY,
    DEEP_SPACE;

    public boolean isHeating() {
        return false;
    }
}

package com.blorbee.createcosmonautics.system.orbit;

public enum OrbitZone {
    SURFACE,
    TRANSITION,
    ORBIT,
    DEEP_SPACE;

    public boolean isAboveSurface() {
        return this == TRANSITION || this == ORBIT || this == DEEP_SPACE;
    }

    public boolean isVacuum() {
        return this == ORBIT || this == DEEP_SPACE;
    }
}

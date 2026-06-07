package com.blorbee.createcosmonautics.system.atmosphere;

public interface PressureResistance {
    /**
     * The maximum ATM difference this block can hold without failing
     */
    float getMaxPressureDifferential();

    /**
     * Structural blocks protect adjacent non-structural blocks, they need to fail
     * first before interior blocks are exposed
     */
    boolean isStructural();
}

package com.blorbee.createcosmonautics.system.atmosphere;

/**
 * Implement for a block to shield against radiation
 */
public interface IRadiationShielding {
    /**
     * @return 0..1 factor where 0 is no protected and 1 is full absorption
     */
    float getShieldingFactor();
}

package com.blorbee.createcosmonautics.system.thermal;

/**
 * Implement for a block to shield against heat
 */
public interface IHeatShielding {
    float getMaxOperatingTemperatureKelvin();

    /**
     * @return Kelvin per second this block sheds when not under load
     */
    float getHeatDissipationRate();

    /**
     * @return How much heat passes through to adjacent blocks
     */
    float getHeatConductivity();

    /**
     * @return Does this degrade over time instead of breaking instantly when overheated
     */
    boolean isAblative();
}

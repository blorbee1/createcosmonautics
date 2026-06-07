package com.blorbee.createcosmonautics.system.thermal;

public record HeatLoad(
    float temperatureKelvin,
    ThermalContext context,
    float intensity
) {}

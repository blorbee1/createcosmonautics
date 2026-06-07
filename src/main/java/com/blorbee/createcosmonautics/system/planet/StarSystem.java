package com.blorbee.createcosmonautics.system.planet;

import net.minecraft.resources.ResourceLocation;

public record StarSystem(
   ResourceLocation id,
   float temperatureKelvin,
   float luminosity
) {}

package com.blorbee.createcosmonautics.system.planet.definition;

public record PlanetRendererDefinition(
    int[] deepOcean, // RGBA
    int[] shallowOcean,
    int[] beach,
    int[] lowland,
    int[] highland,
    int[] peak
) {
    public static PlanetRendererDefinition earth() {
        return new PlanetRendererDefinition(
            new int[]{30,  80,  180, 255},
            new int[]{50,  120, 220, 255},
            new int[]{200, 190, 130, 255},
            new int[]{60,  140, 50,  255},
            new int[]{40,  90,  30,  255},
            new int[]{240, 240, 240, 255}
        );
    }

    public static PlanetRendererDefinition moon() {
        return new PlanetRendererDefinition(
            new int[]{60,  60,  60,  255},
            new int[]{80,  80,  80,  255},
            new int[]{100, 100, 100, 255},
            new int[]{120, 120, 120, 255},
            new int[]{140, 140, 140, 255},
            new int[]{200, 200, 200, 255}
        );
    }

    public int[] forBiome(int index) {
        return switch (index) {
            case 0 -> deepOcean;
            case 1 -> shallowOcean;
            case 2 -> beach;
            case 3 -> lowland;
            case 4 -> highland;
            default -> peak;
        };
    }
}

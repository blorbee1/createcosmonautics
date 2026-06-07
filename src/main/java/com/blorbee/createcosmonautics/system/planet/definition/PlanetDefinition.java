package com.blorbee.createcosmonautics.system.planet.definition;

import com.blorbee.createcosmonautics.system.atmosphere.RadiationLevel;
import com.blorbee.createcosmonautics.system.planet.StarSystem;
import net.minecraft.resources.ResourceLocation;

public record PlanetDefinition(
    ResourceLocation dimensionId,
    ResourceLocation starSystemId,

    StarRenderDefinition starRender,
    AtmosphereDefinition atmosphere,
    PlanetRendererDefinition planetRender,
    OrbitDefinition orbit,

    boolean renderClouds,
    boolean renderStarsOnSurface,
    boolean hasOrbitTransition,
    float orbitTransitionStartY,
    float orbitTransitionEndY
) {
    public static Builder builder(ResourceLocation dimensionId) {
        return new Builder(dimensionId);
    }

    public static final class Builder {
        private final ResourceLocation dimensionId;
        private ResourceLocation starSystemId;

        private StarRenderDefinition starRender = new StarRenderDefinition(true,
            0.5f, 1.0f, ResourceLocation.withDefaultNamespace("textures/enviroment/sun.png"));
        private AtmosphereDefinition atmosphere = new AtmosphereDefinition(true,
            RadiationLevel.NONE, 288f);
        private PlanetRendererDefinition planetRender = PlanetRendererDefinition.earth();
        private OrbitDefinition orbit = OrbitDefinition.earth();

        private boolean renderClouds = true;
        private boolean renderStarsOnSurface = true;
        private boolean hasOrbitTransition = true;
        private float orbitTransitionStartY = 700f;
        private float orbitTransitionEndY = 1000f;

        private Builder(ResourceLocation dimensionId) {
            this.dimensionId = dimensionId;
        }

        public Builder starRender(StarRenderDefinition v) {
            this.starRender = v;
            return this;
        }
        public Builder atmosphere(AtmosphereDefinition v) {
            this.atmosphere = v;
            return this;
        }
        public Builder planetRender(PlanetRendererDefinition v) {
            this.planetRender = v;
            return this;
        }
        public Builder orbit(OrbitDefinition v) {
            this.orbit = v;
            return this;
        }

        public Builder starSystem(StarSystem v) {
            this.starSystemId = v.id();
            return this;
        }
        public Builder starSystem(ResourceLocation id) {
            this.starSystemId = id;
            return this;
        }

        public Builder noClouds() {
            this.renderClouds = false;
            return this;
        }
        public Builder noStarsOnSurface() {
            this.renderStarsOnSurface = false;
            return this;
        }

        public Builder noOrbitTransition() {
            this.hasOrbitTransition = false;
            return this;
        }
        public Builder orbitTransitionStartY(float v) {
            this.orbitTransitionStartY = v;
            return this;
        }
        public Builder orbitTransitionEndY(float v) {
            this.orbitTransitionEndY = v;
            return this;
        }

        public PlanetDefinition build() {
            return new PlanetDefinition(
                dimensionId, starSystemId, starRender, atmosphere, planetRender,
                orbit, renderClouds, renderStarsOnSurface, hasOrbitTransition, orbitTransitionStartY,
                orbitTransitionEndY
            );
        }
    }
}

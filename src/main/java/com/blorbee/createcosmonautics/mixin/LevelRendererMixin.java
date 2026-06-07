package com.blorbee.createcosmonautics.mixin;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import com.blorbee.createcosmonautics.system.orbit.OrbitTransitionTracker;
import com.blorbee.createcosmonautics.system.orbit.OrbitVisualRenderer;
import com.blorbee.createcosmonautics.system.planet.PlanetRegistry;
import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    private static final ResourceLocation WHITE_SUN_LOCATION = CreateCosmonautics.path("textures/environment/white_sun.png");

    @Shadow
    private void renderSnowAndRain(LightTexture pLightTexture, float pPartialTick, double pCamX, double pCamY, double pCamZ) {}

    @WrapOperation(
        method = "renderSky",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;")
    )
    private Vec3 cosmonautics$blackSky(ClientLevel instance, Vec3 pos, float partialTick, Operation<Vec3> original) {
        Vec3 color = original.call(instance, pos, partialTick);

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return color;

        CompoundTag data = mc.player.getPersistentData();
        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        float progress = tracker.getTransitionProgress();
        if (progress <= 0)
            return color;

        PlanetDefinition planet = PlanetRegistry.forLevel(mc.level).orElse(null);
        if (planet == null || !planet.hasOrbitTransition())
            return color;

        return new Vec3(
            Mth.lerp(progress, color.x, 0),
            Mth.lerp(progress, color.y, 0),
            Mth.lerp(progress, color.z, 0)
        );
    }

    @WrapOperation(
        method = "renderSky",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F")
    )
    private float cosmonautics$cancelVanillaStars(ClientLevel instance, float partialTick, Operation<Float> original) {
        float brightness = original.call(instance, partialTick);

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return brightness;

        CompoundTag data = mc.player.getPersistentData();
        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        float progress = tracker.getTransitionProgress();
        if (progress <= 0)
            return brightness;

        PlanetDefinition planet = PlanetRegistry.forLevel(mc.level).orElse(null);
        if (planet == null || !planet.hasOrbitTransition())
            return brightness;

        return 0.0f;
    }

    @WrapOperation(
        method = "renderSky",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F")
    )
    private float[] cosmonautics$fadeSunrise(DimensionSpecialEffects instance, float angle, float partialTick, Operation<float[]> original) {
        float[] color = original.call(instance, angle, partialTick);
        if (color == null)
            return null;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return color;

        CompoundTag data = mc.player.getPersistentData();
        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        float progress = tracker.getTransitionProgress();
        if (progress <= 0)
            return color;

        PlanetDefinition planet = PlanetRegistry.forLevel(mc.level).orElse(null);
        if (planet == null || !planet.hasOrbitTransition())
            return color;

        if (progress >= 1.0f)
            return null; // no sunrise

        float[] faded = color.clone();
        faded[3] *= 1.0f - progress;
        return faded;
    }

    @WrapOperation(
        method = "renderSky",
        at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
            ordinal = 0)
    )
    private void cosmonautics$replaceSunTexture(int unit, ResourceLocation texture, Operation<Void> original) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            original.call(unit, texture);
            return;
        }

        CompoundTag data = mc.player.getPersistentData();
        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        float progress = tracker.getTransitionProgress();
        if (progress <= 0.5f) {
            original.call(unit, texture);
            return;
        }

        PlanetDefinition planet = PlanetRegistry.forLevel(mc.level).orElse(null);
        if (planet == null || !planet.hasOrbitTransition()) {
            original.call(unit, texture);
            return;
        }

        original.call(unit, WHITE_SUN_LOCATION);
    }

    @Redirect(
        method = "renderLevel",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V")
    )
    private void cosmonautics$cancelClouds(LevelRenderer instance, PoseStack poseStack, Matrix4f projMatrix,
                                           Matrix4f cloudProjMatrix, float partialTick,
                                           double camX, double camY, double camZ
    ) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return;

        CompoundTag data = mc.player.getPersistentData();
        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        float progress = tracker.getTransitionProgress();
        if (progress > 0)
            return;

        PlanetDefinition planet = PlanetRegistry.forLevel(mc.level).orElse(null);
        if (planet == null || planet.hasOrbitTransition())
            return;

        instance.renderClouds(poseStack, projMatrix, cloudProjMatrix, partialTick, camX, camY, camZ);
    }

    @Redirect(
        method = "renderLevel",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V")
    )
    private void cosmonautics$cancelWeather(LevelRenderer instance, LightTexture lightTexture,
                                           float partialTick, double camX, double camY, double camZ
    ) {
        if (camY > 400.0)
            return;
        renderSnowAndRain(lightTexture, partialTick, camX, camY, camZ);
    }
}

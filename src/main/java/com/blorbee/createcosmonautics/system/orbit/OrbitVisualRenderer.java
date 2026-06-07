package com.blorbee.createcosmonautics.system.orbit;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import com.blorbee.createcosmonautics.system.planet.PlanetRegistry;
import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import com.blorbee.createcosmonautics.system.planet.definition.PlanetRendererDefinition;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = CreateCosmonautics.MOD_ID, value = Dist.CLIENT)
public final class OrbitVisualRenderer {
    private static final float SKYBOX_DISTANCE = 100f;
    private static final int CLOUD_COLOR_BGR = 0xEDEDED;
    private static final long CLOUD_CYCLE_TIME_MS = 180_000L;

    private static final Map<ResourceLocation, ResourceLocation> PLANET_TEX_CACHE = new HashMap<>();
    private static ResourceLocation CLOUD_TEX_ID = null;
    private static ResourceLocation GLOW_TEX_ID = null;

    private static RenderedStar[] STARS = null;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return;

        CompoundTag data = mc.player.getPersistentData();
        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        float visibility = tracker.getTransitionProgress();
        if (visibility <= 0)
            return;

        PlanetDefinition planet = PlanetRegistry.forLevel(mc.level).orElse(null);
        if (planet == null || !planet.hasOrbitTransition())
            return;

        Camera camera = event.getCamera();
        double camY = camera.getPosition().y;

        if (camY < planet.orbitTransitionStartY())
            return;

        ensureCloudTexture();
        ensureGlowTexture();
        ensureStars();

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.mulPose(event.getModelViewMatrix());

        float celestialAngle = mc.level.getTimeOfDay(event.getPartialTick().getGameTimeDeltaTicks());

        drawOrbitStars(poseStack, visibility, celestialAngle);
        drawPlanetTexture(planet, camY, poseStack.last().pose(), visibility, celestialAngle);

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level  == null || mc.player == null)
            return;

        CompoundTag data = mc.player.getPersistentData();
        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        float factor = tracker.getTransitionProgress();
        if (factor <= 0f)
            return;

        PlanetDefinition planet = PlanetRegistry.forLevel(mc.level).orElse(null);
        if (planet == null || !planet.hasOrbitTransition())
            return;

        event.setRed(Mth.lerp(factor, event.getRed(), 0f));
        event.setGreen(Mth.lerp(factor, event.getGreen(), 0f));
        event.setBlue(Mth.lerp(factor, event.getBlue(), 0f));
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level  == null || mc.player == null)
            return;

        CompoundTag data = mc.player.getPersistentData();
        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        float factor = tracker.getTransitionProgress();
        if (factor <= 0f)
            return;

        PlanetDefinition planet = PlanetRegistry.forLevel(mc.level).orElse(null);
        if (planet == null || !planet.hasOrbitTransition())
            return;

        event.setNearPlaneDistance(Mth.lerp(factor, event.getNearPlaneDistance(), 1000f));
        event.setFarPlaneDistance(Mth.lerp(factor, event.getNearPlaneDistance(), 2000f));
        event.setCanceled(true);
    }

    private static void drawPlanetTexture(PlanetDefinition planet, double camY, Matrix4f matrix, float visibility, float celestialAngle) {
        double quadWorldSize = camY * 2.0;

        float parallaxFactor = (float) (SKYBOX_DISTANCE / Math.max(1.0, quadWorldSize));
        float size = (float) (quadWorldSize * parallaxFactor);

        float relX = 0.0f;
        float relY = (float) (-camY * parallaxFactor);
        float relZ = 0.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        Tesselator tess = Tesselator.getInstance();

        RenderSystem.setShaderTexture(0, getPlanetTextureId(planet));
        BufferBuilder buf = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buf.addVertex(matrix, relX - size, relY, relZ - size).setUv(0f, 0f).setColor(1f, 1f, 1f, visibility);
        buf.addVertex(matrix, relX - size, relY, relZ + size).setUv(0f, 1f).setColor(1f, 1f, 1f, visibility);
        buf.addVertex(matrix, relX + size, relY, relZ + size).setUv(1f, 1f).setColor(1f, 1f, 1f, visibility);
        buf.addVertex(matrix, relX + size, relY, relZ - size).setUv(1f, 0f).setColor(1f, 1f, 1f, visibility);
        BufferUploader.drawWithShader(buf.buildOrThrow());

        if (planet.renderClouds() && CLOUD_TEX_ID != null) {
            RenderSystem.setShaderTexture(0, CLOUD_TEX_ID);
            float timeOffset = (System.currentTimeMillis() % CLOUD_CYCLE_TIME_MS) / (float) CLOUD_CYCLE_TIME_MS;

            BufferBuilder cloudBuf = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            cloudBuf.addVertex(matrix, relX - size, relY, relZ - size).setUv(0f + timeOffset, 0f).setColor(1f, 1f, 1f, visibility);
            cloudBuf.addVertex(matrix, relX - size, relY, relZ + size).setUv(0f + timeOffset, 1f).setColor(1f, 1f, 1f, visibility);
            cloudBuf.addVertex(matrix, relX + size, relY, relZ + size).setUv(1f + timeOffset, 1f).setColor(1f, 1f, 1f, visibility);
            cloudBuf.addVertex(matrix, relX + size, relY, relZ - size).setUv(1f + timeOffset, 0f).setColor(1f, 1f, 1f, visibility);
            BufferUploader.drawWithShader(cloudBuf.buildOrThrow());
        }

        if (GLOW_TEX_ID != null) {
            RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE);
            RenderSystem.setShaderTexture(0, GLOW_TEX_ID);

            float haloSize = size * 1.3f;

            double tAngle = celestialAngle * 2.0 * Math.PI;
            float sunIntensity  = (float) Math.cos(tAngle);
            float sideIntensity = (float) Math.abs(Math.sin(tAngle));
            float haloR, haloG, haloB;
            if (sunIntensity > 0) {
                haloR = Mth.lerp(sideIntensity, 0.40f, 1.00f);
                haloG = Mth.lerp(sideIntensity, 0.70f, 0.42f);
                haloB = Mth.lerp(sideIntensity, 1.00f, 0.15f);
            } else {
                float t = -sunIntensity;
                haloR = Mth.lerp(t, 1.00f, 0.18f);
                haloG = Mth.lerp(t, 0.42f, 0.08f);
                haloB = Mth.lerp(t, 0.15f, 0.45f);
            }

            BufferBuilder haloBuf = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            haloBuf.addVertex(matrix, relX - haloSize, relY, relZ - haloSize).setUv(0f, 0f).setColor(haloR, haloG, haloB, visibility);
            haloBuf.addVertex(matrix, relX - haloSize, relY, relZ + haloSize).setUv(0f, 1f).setColor(haloR, haloG, haloB, visibility);
            haloBuf.addVertex(matrix, relX + haloSize, relY, relZ + haloSize).setUv(1f, 1f).setColor(haloR, haloG, haloB, visibility);
            haloBuf.addVertex(matrix, relX + haloSize, relY, relZ - haloSize).setUv(1f, 0f).setColor(haloR, haloG, haloB, visibility);
            BufferUploader.drawWithShader(haloBuf.buildOrThrow());

            RenderSystem.defaultBlendFunc();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void drawOrbitStars(PoseStack pose, float visibility, float celestialAngle) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE
        );
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        pose.pushPose();
        pose.mulPose(Axis.XP.rotationDegrees(celestialAngle * 360f));

        Matrix4f matrix = pose.last().pose();
        float radius = SKYBOX_DISTANCE;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        boolean hasVertices = false;
        for (RenderedStar star : STARS) {
            float px = star.x * radius;
            float py = star.y * radius;
            float pz = star.z * radius;

            float twinkle = 0.62f + 0.38f * (float) Math.sin(
                (System.currentTimeMillis() / 1000.0) * star.twinkleSpeed + star.twinkleOffset
            );
            float alpha = visibility * twinkle;

            float absY = Math.abs(star.y);
            if (absY > 0.94f) {
                alpha *= Math.clamp((0.98f - absY) / 0.04f, 0f, 1f);
            }
            if (alpha <= 0f)
                continue;

            float ux, uy, uz;
            if (Math.abs(star.x) < 0.99f) {
                float len = (float) Math.sqrt(star.y * star.y + star.z * star.z);
                ux = 0;
                uy = -star.z / len;
                uz = star.y / len;
            } else {
                float len = (float) Math.sqrt(star.x * star.x + star.y * star.y);
                ux = -star.y / len;
                uy = star.x / len;
                uz = 0;
            }

            float vx = star.y * uz - star.z * uy;
            float vy = star.z * ux - star.x * uz;
            float vz = star.x * uy - star.y * ux;

            float s = star.size * 0.1f;
            if (star.type == 2) {
                // X shape
                float L = s * 7.5f;
                float W = s * 0.45f;

                buf.addVertex(matrix, px, py, pz).setColor(star.r, star.g, star.b, alpha);
                buf.addVertex(matrix, px + L * ux, py + L * uy, pz + L * uz).setColor(star.r, star.g, star.b, 0f);
                buf.addVertex(matrix, px + W * ux + W * vx, py + W * uy + W * vy, pz + W * uz + W * vz).setColor(star.r, star.g, star.b, alpha * 0.9f);
                buf.addVertex(matrix, px + L * vx, py + L * vy, pz + L * vz).setColor(star.r, star.g, star.b, 0f);

                buf.addVertex(matrix, px, py, pz).setColor(star.r, star.g, star.b, alpha);
                buf.addVertex(matrix, px + L * vx, py + L * vy, pz + L * vz).setColor(star.r, star.g, star.b, 0f);
                buf.addVertex(matrix, px - W * ux + W * vx, py - W * uy + W * vy, pz - W * uz + W * vz).setColor(star.r, star.g, star.b, alpha * 0.9f);
                buf.addVertex(matrix, px - L * ux, py - L * uy, pz - L * uz).setColor(star.r, star.g, star.b, 0f);

                buf.addVertex(matrix, px, py, pz).setColor(star.r, star.g, star.b, alpha);
                buf.addVertex(matrix, px - L * ux, py - L * uy, pz - L * uz).setColor(star.r, star.g, star.b, 0f);
                buf.addVertex(matrix, px - W * ux - W * vx, py - W * uy - W * vy, pz - W * uz - W * vz).setColor(star.r, star.g, star.b, alpha * 0.9f);
                buf.addVertex(matrix, px - L * vx, py - L * vy, pz - L * vz).setColor(star.r, star.g, star.b, 0f);

                buf.addVertex(matrix, px, py, pz).setColor(star.r, star.g, star.b, alpha);
                buf.addVertex(matrix, px - L * vx, py - L * vy, pz - L * vz).setColor(star.r, star.g, star.b, 0f);
                buf.addVertex(matrix, px + W * ux - W * vx, py + W * uy - W * vy, pz + W * uz - W * vz).setColor(star.r, star.g, star.b, alpha * 0.9f);
                buf.addVertex(matrix, px + L * ux, py + L * uy, pz + L * uz).setColor(star.r, star.g, star.b, 0f);

                hasVertices = true;
            } else {
                float qs = star.type == 1 ? s * 1.8f : s;
                buf.addVertex(matrix, px - qs * ux - qs * vx, py - qs * uy - qs * vy, pz - qs * uz - qs * vz).setColor(star.r, star.g, star.b, alpha);
                buf.addVertex(matrix, px - qs * ux + qs * vx, py - qs * uy + qs * vy, pz - qs * uz + qs * vz).setColor(star.r, star.g, star.b, alpha);
                buf.addVertex(matrix, px + qs * ux + qs * vx, py + qs * uy + qs * vy, pz + qs * uz + qs * vz).setColor(star.r, star.g, star.b, alpha);
                buf.addVertex(matrix, px + qs * ux - qs * vx, py + qs * uy - qs * vy, pz + qs * uz - qs * vz).setColor(star.r, star.g, star.b, alpha);
                hasVertices = true;
            }
        }

        if (hasVertices) {
            BufferUploader.drawWithShader(buf.buildOrThrow());
        } else {
            buf.build();
        }

        pose.popPose();

        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static ResourceLocation getPlanetTextureId(PlanetDefinition planet) {
        ResourceLocation dimId = planet.dimensionId();
        if (PLANET_TEX_CACHE.containsKey(dimId))
            return PLANET_TEX_CACHE.get(dimId);

        Minecraft mc = Minecraft.getInstance();
        byte[] biomeData = generateBiomeData(256);
        NativeImage image = composePlanetTexture(256, biomeData, planet.planetRender());

        DynamicTexture tex = new DynamicTexture(image);
        String regName = CreateCosmonautics.MOD_ID + ".planet." + dimId.getNamespace() + "." + dimId.getPath();
        ResourceLocation id = mc.getTextureManager().register(regName, tex);
        tex.setFilter(false, false);
        image.close();

        PLANET_TEX_CACHE.put(dimId, id);
        return id;
    }

    private static NativeImage composePlanetTexture(int renderDataSize, byte[] biomeData, PlanetRendererDefinition palette) {
        int texSize = 512;
        int virtualSize = 128;
        int blockSize = texSize / virtualSize;

        NativeImage image = new NativeImage(texSize, texSize, false);
        byte[] virtualBiomes = new byte[virtualSize * virtualSize];
        NoiseGrid noise = new NoiseGrid(64, new Random(98765L));

        for (int vy = 0; vy < virtualSize; vy++) {
            for (int vx = 0; vx < virtualSize; vx++) {
                double u = (vx / (double) virtualSize) * renderDataSize;
                double v = (vy / (double) virtualSize) * renderDataSize;

                double nx = u * 0.1;
                double ny = v * 0.1;
                double warpX = (Math.sin(nx) + 0.5 * Math.sin(nx * 2.1)) * 1.5;
                double warpY = (Math.cos(ny) + 0.5 * Math.cos(ny * 2.1)) * 1.5;
                double wu = u + warpX;
                double wv = v + warpY;

                int cx = (int) Math.round(wu);
                int cy = (int) Math.round(wv);

                // 0=deep ocean, 1=shallow, 2=beach, 3=land, 4=highland, 5=snow
                float[] influence = new float[6];
                double radius = 3.2;
                double radiusSqr = radius * radius;

                for (int dx = -3; dx <= 3; dx++) {
                    for (int dy = -3; dy <= 3; dy++) {
                        int gx = Mth.clamp(cx + dx, 0, renderDataSize - 1);
                        int gy = Mth.clamp(cy + dy, 0, renderDataSize - 1);
                        byte biome = biomeData[gx + gy * renderDataSize];

                        double distX = wu - (gx + 0.5);
                        double distY = wv - (gy + 0.5);
                        double dSqr = distX * distX + distY * distY;

                        if (dSqr < radiusSqr) {
                            double ratio = dSqr / radiusSqr;
                            double term = 1.0 - ratio;
                            double weight = term * term * term;

                            influence[biome] += (float) (biome > 1 ? weight * 1.1 : weight);
                        }
                    }
                }

                byte best = 0;
                float maxInf = -1f;
                for (int b = 0; b < 6; b++) {
                    if (influence[b] > maxInf) {
                        maxInf = influence[b];
                        best = (byte) b;
                    }
                }
                virtualBiomes[vx + vy * virtualSize] = best;
            }
        }

        for (int vy = 0; vy < virtualSize; vy++) {
            for (int vx = 0; vx < virtualSize; vx++) {
                int biome = virtualBiomes[vx + vy * virtualSize] & 0xFF;
                int[] col = palette.forBiome(biome);
                int r = col[0];
                int g = col[1];
                int b = col[2];
                int a = col[3];

                boolean isWater = biome <= 1;
                if (isWater) {
                    int tx = vx - 1;
                    int ty = vy - 1;
                    if (tx >= 0 && ty >= 0) {
                        int nb = virtualBiomes[tx + ty * virtualSize] & 0xFF;
                        if (nb > 1) {
                            r = (int) (r * 0.75f);
                            g = (int) (g * 0.75f);
                            b = (int) (b * 0.75f);
                        }
                    }
                } else {
                    int tx = vx + 1;
                    int ty = vy + 1;
                    if (tx < virtualSize && ty < virtualSize) {
                        int nb = virtualBiomes[tx + ty * virtualSize] & 0xFF;
                        if (nb > 1) {
                            r = (int) (r * 0.85f);
                            g = (int) (g * 0.85f);
                            b = (int) (b * 0.85f);
                        }
                    }
                }

                int packed = (a << 24) | (b << 16) | (g << 8) | r;
                for (int bx = 0; bx < blockSize; bx++) {
                    for (int by = 0; by < blockSize; by++) {
                        image.setPixelRGBA(vx * blockSize + bx, vy * blockSize + by, packed);
                    }
                }
            }
        }

        return image;
    }

    private static byte[] generateBiomeData(int size) {
        byte[] data = new byte[size * size];
        NoiseGrid noise = new NoiseGrid(64, new Random(11223L));

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float u = (x / (float) size) * 32f;
                float v = (y / (float) size) * 32f;

                float wx = noise.sample(u + 0.5f, v + 1.2f) * 2f;
                float wy = noise.sample(u + 2.3f, v + 0.8f) * 2f;

                float n = 0f;
                float amp = 0.5f;
                float freq = 1f;
                for (int o = 0; o < 6; o++) {
                    n += noise.sample((u + wx) * freq, (v + wy) * freq) * amp;
                    amp *= 0.5f;
                    freq *= 2f;
                }

                byte biome;
                if (n < 0.35f) biome = 0;
                else if (n < 0.48f) biome = 1;
                else if (n < 0.53f) biome = 2;
                else if (n < 0.66f) biome = 3;
                else if (n < 0.80f) biome = 4;
                else biome = 5;

                data[x + y * size] = biome;
            }
        }

        return data;
    }

    private static void ensureCloudTexture() {
        if (CLOUD_TEX_ID != null)
            return;

        Minecraft mc = Minecraft.getInstance();

        int virtualSize = 128;
        int texSize = 512;
        int blockSize = texSize / virtualSize;

        NativeImage image = new NativeImage(texSize, texSize, true);
        NoiseGrid noise = new NoiseGrid(64, new Random(4242L));

        for (int vx = 0; vx < virtualSize; vx++) {
            for (int vy = 0; vy < virtualSize; vy++) {
                float u = (vx / (float) virtualSize) * 64f;
                float v = (vy / (float) virtualSize) * 64f;

                float n = noise.sample(u, v) * 0.7f
                    + noise.sample(u * 2f + 5f, v * 2f + 3f) * 0.3f;

                int alpha = n > 0.72f ? 180 : 0;

                int packed = (alpha << 24) | CLOUD_COLOR_BGR;
                for (int bx = 0; bx < blockSize; bx++) {
                    for (int by = 0;  by < blockSize; by++) {
                        image.setPixelRGBA(vx * blockSize + bx, vy * blockSize + by, packed);
                    }
                }
            }
        }

        DynamicTexture cloudTexture = new DynamicTexture(image);
        CLOUD_TEX_ID = mc.getTextureManager().register(CreateCosmonautics.MOD_ID + ".clouds", cloudTexture);
        cloudTexture.setFilter(false, false);
        image.close();
    }

    private static void ensureGlowTexture() {
        if (GLOW_TEX_ID != null)
            return;

        Minecraft mc = Minecraft.getInstance();

        int size = 256;
        NativeImage image = new NativeImage(size, size, false);

        double sizeOver2 = size / 2.0;
        double planetRadius = 1.0 / 1.3;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double dx = (x - sizeOver2) / sizeOver2;
                double dy = (y - sizeOver2) / sizeOver2;
                double dist = Math.max(Math.abs(dx), Math.abs(dy)); // square falloff

                int r = 0, g = 0, b = 0, a = 0;
                if (dist <= planetRadius) {
                    double norm = dist / planetRadius;
                    double depth = Math.pow(norm, 3.0);
                    r = Math.min(255, 40 + (int) (depth * 215));
                    g = Math.min(255, 120 + (int) (depth * 135));
                    b = 255;
                    a = 80 + (int) (depth * 175);
                } else if (dist <= 1.0) {
                    double gradient = (dist - planetRadius) / (1.0 - planetRadius);
                    double fade = Math.pow(1.0 - gradient, 1.0);
                    r = (int) (140 * fade);
                    g = (int) (220 * fade);
                    b = (int) (255 * fade);
                    a = (int) (255 * fade);
                }

                image.setPixelRGBA(x, y, (a << 24) | (b << 16) | (g << 8) | r);
            }
        }

        DynamicTexture glowTexture = new DynamicTexture(image);
        GLOW_TEX_ID = mc.getTextureManager().register(CreateCosmonautics.MOD_ID + ".planet_glow", glowTexture);
        image.close();
    }

    private static void ensureStars() {
        if (STARS != null)
            return;

        STARS = new RenderedStar[1600];
        Random rand = new Random(13374242L);

        float[][] clusterCenters = {
            {0.5f, 0.4f, 0.76f},
            {-0.65f, -0.3f, 0.69f},
            {0.1f, -0.8f, -0.59f},
            {-0.4f, 0.75f, -0.53f},
            {0.68f, -0.48f, 0.55f}
        };

        for (int i = 0; i < STARS.length; i++) {
            RenderedStar star = new RenderedStar();
            boolean inCluster = i % 3 == 0;

            if (inCluster) {
                int centerIdx = rand.nextInt(clusterCenters.length);
                float[] cc = clusterCenters[centerIdx];
                float spread = 0.04f + rand.nextFloat() * 0.05f;

                star.x = cc[0] + (float) rand.nextGaussian() * spread;
                star.y = cc[1] + (float) rand.nextGaussian() * spread;
                star.z = cc[2] + (float) rand.nextGaussian() * spread;

                // normalize
                float len = (float) Math.sqrt(star.x * star.x + star.y * star.y + star.z * star.z);
                star.x /= len;
                star.y /= len;
                star.z /= len;

                switch (centerIdx) {
                    case 0 -> { star.r = 0.5f; star.g = 0.85f; star.b = 1.0f; }
                    case 1 -> { star.r = 1.0f; star.g = 0.75f; star.b = 0.4f; }
                    case 4 -> { star.r = 0.9f; star.g = 0.5f; star.b = 1.0f; }
                    default -> { star.r = 0.95f; star.g = 0.95f; star.b = 1.0f; }
                }

                float sizeRoll = rand.nextFloat();
                star.size = 0.35f + sizeRoll * 0.7f;
                star.type = sizeRoll > 0.99f ? 2 : (sizeRoll > 0.8f ? 1 : 0);
            } else {
                // random point on sphere
                double theta = rand.nextDouble() * 2.0 * Math.PI;
                double phi = Math.acos(2.0 * rand.nextDouble() - 1.0);

                star.x = (float) (Math.sin(phi) * Math.cos(theta));
                star.y = (float) (Math.sin(phi) * Math.sin(theta));
                star.z = (float) Math.cos(phi);

                star.r = 1f;
                star.g = 1f;
                star.b = 1f;

                double colorRoll = rand.nextDouble();
                if (colorRoll < 0.15) {
                    star.r = 0.65f + rand.nextFloat() * 0.1f;
                    star.g = 0.80f + rand.nextFloat() * 0.1f;
                } else if (colorRoll < 0.30) {
                    star.r = 0.95f;
                    star.g = 0.95f;
                } else if (colorRoll < 0.42) {
                    star.g = 0.90f + rand.nextFloat() * 0.08f;
                    star.b = 0.65f + rand.nextFloat() * 0.08f;
                } else if (colorRoll < 0.52) {
                    star.g = 0.60f + rand.nextFloat() * 0.12f;
                    star.b = 0.40f + rand.nextFloat() * 0.12f;
                }

                float sizeRoll = rand.nextFloat();
                star.size = 0.25f + sizeRoll * 0.75f;
                star.type = sizeRoll > 0.992f ? 2 : (sizeRoll > 0.85f ? 1 : 0);
            }

            star.twinkleSpeed  = 0.3f + rand.nextFloat() * 1.5f;
            star.twinkleOffset = rand.nextFloat() * 100f;
            STARS[i] = star;
        }
    }

    private static class NoiseGrid {
        private final float[] grid;
        private final int size;

        NoiseGrid(int size, Random rand) {
            this.size = size;
            this.grid = new float[size * size];
            for (int i = 0; i < grid.length; i++)
                grid[i] = rand.nextFloat();
        }

        private float get(int x, int y) {
            x = (x % size + size) % size;
            y = (y % size + size) % size;
            return grid[x + y * size];
        }

        public float sample(float x, float y) {
            int x0 = (int) Math.floor(x);
            int y0 = (int) Math.floor(y);

            float tx = x - x0, ty = y - y0;
            float sx = tx * tx * (3 - 2 * tx); // smoothstep
            float sy = ty * ty * (3 - 2 * ty);
            float v00 = get(x0, y0), v10 = get(x0+1, y0);
            float v01 = get(x0, y0+1);
            float v11 = get(x0+1, y0+1);
            float nx0 = v00 + sx * (v10 - v00);
            float nx1 = v01 + sx * (v11 - v01);
            return nx0 + sy * (nx1 - nx0);
        }
    }

    private static class RenderedStar {
        float x, y, z;
        float r, g, b, size;
        float twinkleSpeed, twinkleOffset;
        int type; // 0=dot, 1=large, 2=cross
    }
}

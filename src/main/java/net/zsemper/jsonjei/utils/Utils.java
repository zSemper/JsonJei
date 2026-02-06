package net.zsemper.jsonjei.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.zsemper.jsonjei.JsonJei;

import java.util.Objects;

public final class Utils {
    private Utils() {}

    public static Item getItem(String name) {
        ResourceLocation itemKey = ResourceLocation.parse(name);
        if (!BuiltInRegistries.ITEM.containsKey(itemKey)) {
            JsonJei.LOGGER.error("Invalid item for key '{}'", name);
            return Items.AIR;
        }
        return Objects.requireNonNullElse(BuiltInRegistries.ITEM.get(itemKey), Items.AIR);
    }

    public static ResourceLocation validate(String id) {
        if (id != null) {
            if (id.contains(":")) {
                return ResourceLocation.parse(id);
            }
            return JsonJei.id(id);
        }
        return null;
    }

    public static ResourceLocation validateFile(String id, String ext) {
        if (ext.startsWith(".")) {
            ext = ext.substring(1);
        }
        if (id.endsWith(ext)) {
            return validate(id);
        }
        return validate(String.format("%s.%s", id, ext));
    }

    public static void assertInRange(int min, int max, int value) {
        assertInRange(min, value);
        if (max < value) {
            JsonJei.LOGGER.error("Value {} cannot be larger than {}", value, max);
        }
    }

    public static void assertInRange(int min, int value) {
        if (min > value) {
            JsonJei.LOGGER.error("Value {} cannot be smaller than {}", value, min);
        }
    }

    public static void blitAnimated(
            GuiGraphics guiGraphics, ResourceLocation texture,
            int width, int height, int u, int v, int x, int y,
            int ticksPerCycle, String startDirection, boolean inverted
    ) {
        long ticks = System.currentTimeMillis() / 50;
        float progress;
        if (inverted) {
            progress = 1.0f - ((ticks % ticksPerCycle) / (float) ticksPerCycle);
        } else {
            progress = (ticks % ticksPerCycle) / (float) ticksPerCycle;
        }

        int renderWidth = width;
        int renderHeight = height;
        int renderU = u;
        int renderV = v;
        int renderX = x;
        int renderY = y;

        switch (RenderDirection.parse(startDirection)) {
            case LEFT -> renderWidth = Math.round(width * progress);
            case RIGHT -> {
                renderWidth = Math.round(width * progress);
                renderX += width - renderWidth;
                renderU += width - renderWidth;
            }
            case TOP -> renderHeight = Math.round(height * progress);
            case BOTTOM -> {
                renderHeight = Math.round(height * progress);
                renderY += height - renderHeight;
                renderV += height - renderHeight;
            }
        }

        if (renderWidth <= 0 || renderHeight <= 0) {
            return;
        }

        guiGraphics.blit(
                texture, renderX, renderY,
                renderU, renderV, renderWidth, renderHeight
        );
    }

    enum RenderDirection {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM;

        public static RenderDirection parse(String direction) {
            return switch (direction.toUpperCase()) {
                case "RIGHT" -> RIGHT;
                case "TOP" -> TOP;
                case "BOTTOM" -> BOTTOM;
                default -> LEFT;
            };
        }
    }
}

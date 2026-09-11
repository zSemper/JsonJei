package net.zsemper.jsonjei.component.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.component.reference.ReferenceCodecs;
import net.zsemper.jsonjei.component.reference.ReferenceValue;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record TextureRenderComponent(
        String key,
        ResourceLocation texture,
        ReferenceValue<Integer> x,
        ReferenceValue<Integer> y,
        ReferenceValue<Integer> width,
        ReferenceValue<Integer> height,
        ReferenceValue<Integer> u,
        ReferenceValue<Integer> v,
        ReferenceValue<Integer> time,
        ReferenceValue<String> direction,
        ReferenceValue<Boolean> inverted,
        Optional<JsonElement> values
) implements JsonRenderComponent {
    public static final MapCodec<TextureRenderComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(TextureRenderComponent::key),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(TextureRenderComponent::texture),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("x", new ReferenceValue.Literal<>(0)).forGetter(TextureRenderComponent::x),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("y", new ReferenceValue.Literal<>(0)).forGetter(TextureRenderComponent::y),
            ReferenceCodecs.POSITIVE_INT_CODEC.optionalFieldOf("width", new ReferenceValue.Literal<>(1)).forGetter(TextureRenderComponent::width),
            ReferenceCodecs.POSITIVE_INT_CODEC.optionalFieldOf("height", new ReferenceValue.Literal<>(1)).forGetter(TextureRenderComponent::height),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("u", new ReferenceValue.Literal<>(0)).forGetter(TextureRenderComponent::u),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("v", new ReferenceValue.Literal<>(0)).forGetter(TextureRenderComponent::u),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("time", new ReferenceValue.Literal<>(0)).forGetter(TextureRenderComponent::time),
            ReferenceCodecs.STRING_CODEC.optionalFieldOf("direction", new ReferenceValue.Literal<>("left")).forGetter(TextureRenderComponent::direction),
            ReferenceCodecs.BOOLEAN_CODEC.optionalFieldOf("inverted", new ReferenceValue.Literal<>(false)).forGetter(TextureRenderComponent::inverted),
            ExtraCodecs.JSON.optionalFieldOf("values").forGetter(TextureRenderComponent::values)
    ).apply(instance, TextureRenderComponent::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("texture");
    }

    @Override
    public void render(GuiGraphics guiGraphics, @Nullable JsonElement override, JsonObject recipeValues, int mouseX, int mouseY) {
        if (override != null) {
            JsonJei.parseCodec(CODEC.codec(), override, component -> component.render(guiGraphics, null, recipeValues, mouseX, mouseY));
            return;
        }

        int dx = decode(Codec.INT, "x", () -> x.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dy = decode(Codec.INT, "y", () -> y.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dWidth = decode(Codec.INT, "width", () -> width.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dHeight = decode(Codec.INT, "height", () -> height.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int du = decode(Codec.INT, "u", () -> u.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dv = decode(Codec.INT, "v", () -> v.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dTime = decode(Codec.INT, "time", () -> time.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        String dDirection = decode(Codec.STRING, "direction", () -> direction.resolve(key -> GsonHelper.getAsString(recipeValues, key)));
        boolean dInverted = decode(Codec.BOOL, "inverted", () -> inverted.resolve(key -> GsonHelper.getAsBoolean(recipeValues, key)));

        if (dTime == 0) {
            JsonJei.renderOver(guiGraphics, g -> g.blit(JsonJei.validateTexture(texture), dx, dy, du, dv, dWidth, dHeight));
        } else {
            JsonJei.renderOver(guiGraphics, g -> blitAnimated(g, texture, dWidth, dHeight, du, dv, dx, dy, dTime, dDirection, dInverted));
        }
    }

    private static void blitAnimated(
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

        switch (startDirection.toLowerCase()) {
            case "left" -> renderWidth = Math.round(width * progress);
            case "right" -> {
                renderWidth = Math.round(width * progress);
                renderX += width - renderWidth;
                renderU += width - renderWidth;
            }
            case "top" -> renderHeight = Math.round(height * progress);
            case "bottom" -> {
                renderHeight = Math.round(height * progress);
                renderY += height - renderHeight;
                renderV += height - renderHeight;
            }
        }

        if (renderWidth <= 0 || renderHeight <= 0) {
            return;
        }

        guiGraphics.blit(
                JsonJei.validateTexture(texture), renderX, renderY,
                renderU, renderV, renderWidth, renderHeight
        );
    }
}

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

public record SpriteRenderComponent(
        String key,
        ResourceLocation texture,
        ReferenceValue<Integer> x,
        ReferenceValue<Integer> y,
        ReferenceValue<Integer> width,
        ReferenceValue<Integer> height,
        Optional<JsonElement> values
) implements JsonRenderComponent {
    public static final MapCodec<SpriteRenderComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(SpriteRenderComponent::key),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(SpriteRenderComponent::texture),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("x", new ReferenceValue.Literal<>(0)).forGetter(SpriteRenderComponent::x),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("y", new ReferenceValue.Literal<>(0)).forGetter(SpriteRenderComponent::y),
            ReferenceCodecs.POSITIVE_INT_CODEC.optionalFieldOf("width", new ReferenceValue.Literal<>(1)).forGetter(SpriteRenderComponent::width),
            ReferenceCodecs.POSITIVE_INT_CODEC.optionalFieldOf("height", new ReferenceValue.Literal<>(1)).forGetter(SpriteRenderComponent::height),
            ExtraCodecs.JSON.optionalFieldOf("values").forGetter(SpriteRenderComponent::values)
    ).apply(instance, SpriteRenderComponent::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("sprite");
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

        guiGraphics.blitSprite(JsonJei.validateTexture(texture), dx, dy, dWidth, dHeight);
    }
}

package net.zsemper.jsonjei.component.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.component.reference.ReferenceCodecs;
import net.zsemper.jsonjei.component.reference.ReferenceValue;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record TextRenderComponent(
        String key,
        ReferenceValue<String> text,
        ReferenceValue<Integer> x,
        ReferenceValue<Integer> y,
        ReferenceValue<String> color,
        ReferenceValue<Boolean> shadow,
        Optional<JsonElement> values
) implements JsonRenderComponent {
    public static final MapCodec<TextRenderComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(TextRenderComponent::key),
            ReferenceCodecs.STRING_CODEC.fieldOf("text").forGetter(TextRenderComponent::text),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("x", new ReferenceValue.Literal<>(0)).forGetter(TextRenderComponent::x),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("y", new ReferenceValue.Literal<>(0)).forGetter(TextRenderComponent::y),
            ReferenceCodecs.STRING_CODEC.optionalFieldOf("color", new ReferenceValue.Literal<>("0x3F3F3F")).forGetter(TextRenderComponent::color),
            ReferenceCodecs.BOOLEAN_CODEC.optionalFieldOf("shadow", new ReferenceValue.Literal<>(false)).forGetter(TextRenderComponent::shadow),
            ExtraCodecs.JSON.optionalFieldOf("values").forGetter(TextRenderComponent::values)
    ).apply(instance, TextRenderComponent::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("text");
    }

    @Override
    public void render(GuiGraphics guiGraphics, @Nullable JsonElement override, JsonObject recipeValues, int mouseX, int mouseY) {
        if (override != null) {
            JsonJei.parseCodec(CODEC.codec(), override, component -> component.render(guiGraphics, null, recipeValues, mouseX, mouseY));
            return;
        }

        String dText = decode(Codec.STRING, "text", () -> text.resolve(key -> GsonHelper.getAsString(recipeValues, key)));
        int dx = decode(Codec.INT, "x", () -> x.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dy = decode(Codec.INT, "y", () -> y.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dColor = Integer.decode(decode(Codec.STRING, "color", () -> color.resolve(key -> GsonHelper.getAsString(recipeValues, key))));
        boolean dShadow = decode(Codec.BOOL, "shadow", () -> shadow.resolve(key -> GsonHelper.getAsBoolean(recipeValues, key)));

        JsonJei.renderOver(guiGraphics, g -> g.drawString(Minecraft.getInstance().font, Component.translatable(dText), dx, dy, dColor, dShadow));
    }
}

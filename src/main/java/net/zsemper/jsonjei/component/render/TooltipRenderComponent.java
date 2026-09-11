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

import java.util.List;
import java.util.Optional;

public record TooltipRenderComponent(
        String key,
        List<ReferenceValue<String>> tooltip,
        ReferenceValue<Integer> x,
        ReferenceValue<Integer> y,
        ReferenceValue<Integer> width,
        ReferenceValue<Integer> height,
        Optional<JsonElement> values
) implements JsonRenderComponent {
    public static final MapCodec<TooltipRenderComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(TooltipRenderComponent::key),
            ReferenceCodecs.STRING_CODEC.listOf().fieldOf("tooltip").forGetter(TooltipRenderComponent::tooltip),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("x", new ReferenceValue.Literal<>(0)).forGetter(TooltipRenderComponent::x),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("y", new ReferenceValue.Literal<>(0)).forGetter(TooltipRenderComponent::y),
            ReferenceCodecs.POSITIVE_INT_CODEC.optionalFieldOf("width", new ReferenceValue.Literal<>(1)).forGetter(TooltipRenderComponent::width),
            ReferenceCodecs.POSITIVE_INT_CODEC.optionalFieldOf("height", new ReferenceValue.Literal<>(1)).forGetter(TooltipRenderComponent::height),
            ExtraCodecs.JSON.optionalFieldOf("values").forGetter(TooltipRenderComponent::values)
    ).apply(instance, TooltipRenderComponent::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("tooltip");
    }

    @Override
    public void render(GuiGraphics guiGraphics, @Nullable JsonElement override, JsonObject recipeValues, int mouseX, int mouseY) {
        if (override != null) {
            JsonJei.parseCodec(CODEC.codec(), override, component -> component.render(guiGraphics, null, recipeValues, mouseX, mouseY));
            return;
        }

        var dTooltip = tooltip.stream()
                .map(value -> value.resolve(key -> decode(Codec.STRING, key, () -> GsonHelper.getAsString(recipeValues, key))))
                .map(text -> Component.translatable(text).getVisualOrderText())
                .toList();

        int dx = decode(Codec.INT, "x", () -> x.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dy = decode(Codec.INT, "y", () -> y.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dWidth = decode(Codec.INT, "width", () -> width.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dHeight = decode(Codec.INT, "height", () -> height.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));

        if (mouseX > dx && mouseX < dx + dWidth && mouseY > dy && mouseY < dy + dHeight) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, dTooltip, mouseX, mouseY);
        }
    }
}

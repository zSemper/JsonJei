package net.zsemper.jsonjei.component.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.component.reference.ReferenceCodecs;
import net.zsemper.jsonjei.component.reference.ReferenceValue;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ItemRenderComponent(
        String key,
        ItemStack stack,
        ReferenceValue<Integer> x,
        ReferenceValue<Integer> y,
        Optional<JsonElement> values
) implements JsonRenderComponent {
    public static final MapCodec<ItemRenderComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(ItemRenderComponent::key),
            ItemStack.CODEC.fieldOf("item").forGetter(ItemRenderComponent::stack),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("x", new ReferenceValue.Literal<>(0)).forGetter(ItemRenderComponent::x),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("y", new ReferenceValue.Literal<>(0)).forGetter(ItemRenderComponent::y),
            ExtraCodecs.JSON.optionalFieldOf("values").forGetter(ItemRenderComponent::values)
    ).apply(instance, ItemRenderComponent::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("item");
    }

    @Override
    public void render(GuiGraphics guiGraphics, @Nullable JsonElement override, JsonObject recipeValues, int mouseX, int mouseY) {
        if (override != null) {
            JsonJei.parseCodec(CODEC.codec(), override, component -> component.render(guiGraphics, null, recipeValues, mouseX, mouseY));
            return;
        }

        int dx = decode(Codec.INT, "x", () -> x.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dy = decode(Codec.INT, "y", () -> y.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));

        JsonJei.renderOver(guiGraphics, g -> {
            g.renderItem(stack, dx, dy);
            g.renderItemDecorations(Minecraft.getInstance().font, stack, dx, dy);
        });
    }
}

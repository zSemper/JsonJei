package net.zsemper.jsonjei.component.ingredient;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.jei.ingredient.energy.EnergyIngredient;
import net.zsemper.jsonjei.jei.ingredient.energy.EnergyIngredientRenderer;
import org.jetbrains.annotations.UnknownNullability;

public record JsonEnergy(String key, int x, int y, int capacity, boolean showTooltip, int width, int height, boolean hasBackground) implements JsonRenderedIngredient {
    public static final MapCodec<JsonEnergy> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(JsonEnergy::key),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("x", 0).forGetter(JsonEnergy::x),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("y", 0).forGetter(JsonEnergy::y),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("capacity", 1).forGetter(JsonEnergy::capacity),
            Codec.BOOL.optionalFieldOf("show_tooltip", false).forGetter(JsonEnergy::showTooltip),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("width", 16).forGetter(JsonEnergy::width),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("height", 16).forGetter(JsonEnergy::height),
            Codec.BOOL.optionalFieldOf("has_background", false).forGetter(JsonEnergy::hasBackground)
    ).apply(instance, JsonEnergy::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("energy");
    }

    @Override
    public void place(IRecipeSlotBuilder builder, @UnknownNullability JsonElement recipe, boolean isInput) {
        JsonJei.parseCodec(EnergyIngredient.CODEC, recipe, ingredient -> builder
                .addIngredient(EnergyIngredient.TYPE, ingredient)
                .setCustomRenderer(EnergyIngredient.TYPE, new EnergyIngredientRenderer(capacity, showTooltip, width, height))
        );
    }

    @Override
    public boolean placeNullable() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (hasBackground) {
            guiGraphics.blitSprite(JsonJei.id("energy_background"), x - 1, y - 1, width + 2, height + 2);
        }
    }
}

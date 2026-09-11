package net.zsemper.jsonjei.component.ingredient;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.zsemper.jsonjei.JsonJei;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public record JsonFluid(String key, int x, int y, int capacity, boolean showTooltip, int width, int height, boolean hasBackground, boolean hasOverlay) implements JsonRenderedIngredient {
    public static final MapCodec<JsonFluid> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(JsonFluid::key),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("x", 0).forGetter(JsonFluid::x),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("y", 0).forGetter(JsonFluid::y),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("capacity", 1).forGetter(JsonFluid::capacity),
            Codec.BOOL.optionalFieldOf("show_tooltip", false).forGetter(JsonFluid::showTooltip),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("width", 16).forGetter(JsonFluid::width),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("height", 16).forGetter(JsonFluid::height),
            Codec.BOOL.optionalFieldOf("has_background", false).forGetter(JsonFluid::hasBackground),
            Codec.BOOL.optionalFieldOf("has_overlay", false).forGetter(JsonFluid::hasOverlay)
    ).apply(instance, JsonFluid::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("fluid");
    }

    @Override
    public void place(IRecipeSlotBuilder builder, @UnknownNullability JsonElement recipe, boolean isInput) {
        builder.setFluidRenderer(capacity, showTooltip, width, height);

        if (recipe != null) {
            if (isInput) {
                JsonJei.parseCodec(SizedFluidIngredient.FLAT_CODEC, recipe, ingredient -> builder
                        .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(ingredient.getFluids()))
                        .setFluidRenderer(capacity, showTooltip, width, height)
                );
            } else {
                JsonJei.parseCodec(FluidStack.CODEC, recipe, stack -> builder
                        .addIngredient(NeoForgeTypes.FLUID_STACK, stack)
                        .setFluidRenderer(capacity, showTooltip, width, height)
                );
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (hasBackground) {
            guiGraphics.blitSprite(JsonJei.id("tank_background"), x - 1, y - 1, width + 2, height + 2);
        }
        if (hasOverlay) {
            JsonJei.renderOver(guiGraphics, g -> g.blitSprite(JsonJei.id("tank_overlay"), x - 1, y - 1, Math.min(width, 16) + 2, height + 2));
        }
    }
}

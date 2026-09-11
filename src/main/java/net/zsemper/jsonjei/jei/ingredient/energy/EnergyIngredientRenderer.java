package net.zsemper.jsonjei.jei.ingredient.energy;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import net.zsemper.jsonjei.JsonJei;
import org.joml.Matrix4f;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public record EnergyIngredientRenderer(int capacity, boolean showTooltip, int width, int height) implements IIngredientRenderer<EnergyIngredient> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    public EnergyIngredientRenderer() {
        this(1, false, 16, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, EnergyIngredient ingredient) {


        if (ingredient.energy() == 0 || capacity <= ingredient.energy()) {
            guiGraphics.blitSprite(JsonJei.id("energy_overlay"), -1, -1, width + 2, height + 2);
        } else {
            int filled = (ingredient.energy() * height / capacity) + 1;
            Matrix4f matrix = guiGraphics.pose().last().pose();
            int x = (int) matrix.m30();
            int y = (int) matrix.m31();

            guiGraphics.enableScissor(
                    x - 1,
                    y - 1 + (height + 2) - filled,
                    x - 1 + (width + 2),
                    y - 1 + (height + 2)
            );
            guiGraphics.blitSprite(
                    JsonJei.id("energy_overlay"),
                    -1, -1,
                    width + 2, height + 2
            );
            guiGraphics.disableScissor();
        }
    }

    @SuppressWarnings("removal")
    @Override
    public List<Component> getTooltip(EnergyIngredient ingredient, TooltipFlag tooltipFlag) {
        return tooltip(ingredient, tooltipFlag);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, EnergyIngredient ingredient, TooltipFlag tooltipFlag) {
        tooltip.addAll(tooltip(ingredient, tooltipFlag));
    }

    private List<Component> tooltip(EnergyIngredient ingredient, TooltipFlag flags) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("json_jei.gui.energy"));
        if (ingredient.energy() != 0) {
            MutableComponent cap;
            if (showTooltip && capacity != 0) {
                cap = Component.translatable("json_jei.gui.fe_capacity", nf.format(ingredient.energy()), nf.format(capacity));
            } else {
                cap = Component.translatable("json_jei.gui.fe", nf.format(ingredient.energy()));
            }
            tooltip.add(cap.withColor(0xAAAAAA));
        }
        if (flags.isAdvanced()) {
            tooltip.add(Component.literal("json_jei:energy").withColor(0x555555));
        }
        return tooltip;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}

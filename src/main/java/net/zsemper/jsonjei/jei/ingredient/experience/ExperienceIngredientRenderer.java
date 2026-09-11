package net.zsemper.jsonjei.jei.ingredient.experience;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.zsemper.jsonjei.JsonJei;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public record ExperienceIngredientRenderer(boolean isInput, boolean coloredTooltip) implements IIngredientRenderer<ExperienceIngredient> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    public ExperienceIngredientRenderer() {
        this(false, true);
    }

    @Override
    public void render(GuiGraphics guiGraphics, ExperienceIngredient ingredient) {
        guiGraphics.blitSprite(JsonJei.id("experience"), 0, 0, 16, 16);
    }

    @SuppressWarnings("removal")
    @Override
    public List<Component> getTooltip(ExperienceIngredient ingredient, TooltipFlag tooltipFlag) {
        return tooltip(ingredient, tooltipFlag);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ExperienceIngredient ingredient, TooltipFlag tooltipFlag) {
        tooltip.addAll(tooltip(ingredient, tooltipFlag));
    }

    private List<Component> tooltip(ExperienceIngredient ingredient, TooltipFlag flags) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("json_jei.gui.experience"));
        if (ingredient.experience() != 0) {
            MutableComponent xp = Component.translatable("json_jei.gui.xp", nf.format(ingredient.experience()));
            if (isInput && coloredTooltip) {
                xp.withColor(xpColor(ingredient));
            } else {
                xp.withColor(0xAAAAAA);
            }
            tooltip.add(xp);
        }
        if (flags.isAdvanced()) {
            tooltip.add(Component.literal("json_jei:experience").withColor(0x555555));
        }
        return tooltip;
    }

    private static int xpColor(ExperienceIngredient ingredient) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (player.isCreative() || player.experienceLevel >= ingredient.experience()) {
                return 0x55FF55;
            } else {
                return 0xFF5555;
            }
        }
        return 0xAAAAAA;
    }
}

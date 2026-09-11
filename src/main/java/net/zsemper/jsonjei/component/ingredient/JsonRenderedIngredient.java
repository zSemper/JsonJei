package net.zsemper.jsonjei.component.ingredient;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Extension to the normal json ingredient allowing to render extra things
 */
public interface JsonRenderedIngredient extends JsonIngredient {

    /**
     * Allows for rendering of extra things
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     */
    void render(GuiGraphics guiGraphics, int mouseX, int mouseY);
}

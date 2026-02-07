package net.zsemper.jsonjei.jei;

/**
 * <p>Describes a component that can be rendered inside a jei category</p>
 *
 * <p>All components are rendered using {@code IRecipeCategory#draw()}</p>
 */
public enum RenderComponent {
    /**
     * {@code TEXT} is a simple string that is drawn
     * over the slot
     */
    TEXT,

    /**
     * {@code TOOLTIP} is an invisible field that will,
     * when hovered over, show a tooltip
     */
    TOOLTIP,

    /**
     * {@code TEXTURE} is a static or animated texture
     */
    TEXTURE
}

package net.zsemper.jsonjei.utils;

/**
 * Holds all JSON keys used inside both the categories and recipes JSON files.
 */
public final class JsonKey {
    private JsonKey() {}

    public static final String KEY = "key";
    public static final String DEFAULT = "default";
    public static final String TYPE = "type";
    public static final String UID = "uid";

    // Base category keys
    public static final String TITLE = "title";
    public static final String ICON = "icon";
    public static final String BACKGROUND = "background";
    public static final String RECIPE_BORDER = "recipe_border";
    public static final String RECIPE = "recipe";
    public static final String RENDERING = "rendering";
    public static final String RECIPE_ITEMS = "recipe_items";

    // Jei recipe
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";

    // Recipe JSON Decoding
    public static final String TAG = "tag";
    public static final String ITEM = "item";
    public static final String COUNT = "count";
    public static final String FLUID = "fluid";
    public static final String AMOUNT = "amount";

    // Extra recipe keys
    public static final String CAPACITY = "capacity";
    public static final String SHOW_TOOLTIP = "show_tooltip";

    // Rendering Types
    public static final String TEXT = "text";
    public static final String TOOLTIP = "tooltip";
    public static final String TEXTURE = "texture";

    // Rendering
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String U = "u";
    public static final String V = "v";

    // Extra rendering keys
    public static final String COLOR = "color";
    public static final String TIME = "time";
    public static final String START_DIRECTION = "start_direction";
    public static final String INVERTED = "inverted";
}

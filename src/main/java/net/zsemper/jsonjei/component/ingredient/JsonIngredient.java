package net.zsemper.jsonjei.component.ingredient;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.resources.ResourceLocation;
import net.zsemper.jsonjei.component.JsonRegisterEvent;
import net.zsemper.jsonjei.component.JsonRegistry;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Base interface for every ingredient that can be represented inside jei
 *
 * @see JsonRegisterEvent
 */
public interface JsonIngredient {

    /**
     * Codec for all registered json ingredients
     * <p>
     * To register a new json ingredient use {@link JsonRegisterEvent#registerIngredient(ResourceLocation, MapCodec)}
     */
    Codec<JsonIngredient> CODEC = ResourceLocation.CODEC.dispatch(JsonIngredient::type, JsonRegistry::getIngredientCodec);

    /**
     * Defines the type of the json ingredient. This should <strong>NOT</strong> be serialized in a
     * codec and should only be implemented
     */
    ResourceLocation type();

    /**
     * The name of a json ingredient. Should be serialized inside a codec
     */
    String key();

    /**
     * The x position of a json ingredient. Should be serialized inside a codec
     */
    int x();

    /**
     * The y position of a json ingredient. Should be serialized inside a codec
     */
    int y();

    /**
     * Called to place the json ingredient in the category
     *
     * @param builder The slot builder for the ingredient with the x and y position as well
     *                as being input or output predefined
     * @param recipe  The part of the recipe that holds the json ingredient. This will always
     *                be not null is {@link #placeNullable()} returns {@code false}
     * @param isInput If the ingredient is an input or an output
     */
    void place(IRecipeSlotBuilder builder, @UnknownNullability JsonElement recipe, boolean isInput);

    /**
     * Allows to place a json ingredient even if the JsonElement of it is {@code null}
     *
     * @return If slot is allowed to be placed even when {@code null}
     */
    default boolean placeNullable() {
        return true;
    }
}

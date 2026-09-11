package net.zsemper.jsonjei.component.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.component.JsonRegisterEvent;
import net.zsemper.jsonjei.component.JsonRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base interface for every render component that can be drawn inside a jei category
 *
 * @see JsonRegisterEvent
 */
public interface JsonRenderComponent {

    /**
     * Codec for all registered json render components
     * <p>
     * To register a new json render component use {@link JsonRegisterEvent#registerRenderComponent(ResourceLocation, MapCodec)}
     */
    Codec<JsonRenderComponent> CODEC = ResourceLocation.CODEC.dispatch(JsonRenderComponent::type, JsonRegistry::getRenderComponentType);

    /**
     * Defines the type of the json ingredient. This should <strong>NOT</strong> be serialized in a
     * codec and should only be implemented
     */
    ResourceLocation type();

    /**
     * The name of a json render component. Should be serialized inside a codec
     */
    String key();

    /**
     * Optional default values for render components that use reference values
     * to set only override these values in specific recipes instead of setting
     * them in every recipe
     */
    Optional<JsonElement> values();

    /**
     * Called to render the actual json render component
     *
     * @param override     The full overridden json render component, null when not provided
     * @param recipeValues The specified values of an individual recipe
     * @param mouseX       The x position of the mouse
     * @param mouseY       The y position of the mouse
     */
    void render(GuiGraphics guiGraphics, @Nullable JsonElement override, JsonObject recipeValues, int mouseX, int mouseY);

    /**
     * Decodes the reference into an actual value
     *
     * @param codec The codec of the value
     * @param key   The key of the value
     * @param value A supplier for the decoding of the reference value
     * @return      The encoded value
     */
    @ApiStatus.NonExtendable
    default /*final*/ <T> T decode(Codec<T> codec, String key, Supplier<T> value) {
        try {
            return value.get();
        } catch (Exception ignored) {
            if (values().isPresent()) {
                Optional<T> opt = JsonJei.parseCodec(codec.fieldOf(key).codec(), values().get());
                if (opt.isPresent()) {
                    return opt.get();
                }
            }
        }
//        throw new JsonParseException("Expected key '" + key + "', but its missing");
        throw new JsonParseException(key); // Delegate error message to jei category draw method
    }
}

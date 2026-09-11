package net.zsemper.jsonjei.component;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.zsemper.jsonjei.component.ingredient.JsonIngredient;
import net.zsemper.jsonjei.component.render.JsonRenderComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers all json based elements of categories
 */
public class JsonRegisterEvent extends Event {
    final Map<ResourceLocation, MapCodec<? extends JsonIngredient>> ingredients = new HashMap<>();
    final Map<ResourceLocation, MapCodec<? extends JsonRenderComponent>> renderComponents = new HashMap<>();

    JsonRegisterEvent() {}

    /**
     * Registers a new {@link JsonIngredient}
     *
     * @param type  The registry type, should be the same as {@link JsonIngredient#type()}
     * @param codec The map codec of the component
     */
    public void registerIngredient(ResourceLocation type, MapCodec<? extends JsonIngredient> codec) {
        ingredients.put(type, codec);
    }

    /**
     * Registers a new {@link JsonRenderComponent}
     *
     * @param type  The registry type, should be the same as {@link JsonRenderComponent#type()}
     * @param codec The map codec of the component
     */
    public void registerRenderComponent(ResourceLocation type, MapCodec<? extends JsonRenderComponent> codec) {
        renderComponents.put(type, codec);
    }
}

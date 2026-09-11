package net.zsemper.jsonjei.component;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import net.zsemper.jsonjei.component.ingredient.JsonIngredient;
import net.zsemper.jsonjei.component.render.JsonRenderComponent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public final class JsonRegistry {
    private static final Map<ResourceLocation, MapCodec<? extends JsonIngredient>> ingredients;
    private static final Map<ResourceLocation, MapCodec<? extends JsonRenderComponent>> renderComponents;

    static {
        JsonRegisterEvent event = NeoForge.EVENT_BUS.post(new JsonRegisterEvent());
        ingredients = event.ingredients;
        renderComponents = event.renderComponents;
    }

    public static MapCodec<? extends JsonIngredient> getIngredientCodec(ResourceLocation type) {
        MapCodec<? extends JsonIngredient> codec = ingredients.get(type);
        if (codec == null) {
            throw new IllegalArgumentException("No JsonIngredient for type: '" + type + "' is registered");
        }
        return codec;
    }

    public static MapCodec<? extends JsonRenderComponent> getRenderComponentType(ResourceLocation type) {
        MapCodec<? extends JsonRenderComponent> codec = renderComponents.get(type);
        if (codec == null) {
            throw new IllegalArgumentException("No JsonRenderComponent for type: '" + type + "' is registered");
        }
        return codec;
    }
}

package net.zsemper.jsonjei.jei;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

/**
 * Represents a recipe built from a json file
 */
@SuppressWarnings("ClassCanBeRecord")
public class JeiRecipe {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, Map<ResourceLocation, JeiRecipe>> recipes = new HashMap<>();

    private final ResourceLocation id;
    private final JsonObject recipe;

    public JeiRecipe(ResourceLocation id, JsonObject recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    // Clears out any registered recipes to avoid collision on /reload
    public static void clearRecipe() {
        recipes.clear();
    }

    public static void addRecipe(ResourceLocation uid, ResourceLocation id, JsonObject object) {
        if (recipes.containsKey(uid)) {
            if (recipes.get(uid).containsKey(id)) {
                LOGGER.warn("Recipe '{}' with type '{}' has already been registered any will be skipped", id, uid);
            } else {
                recipes.get(uid).put(id, new JeiRecipe(id, object));
            }
        } else {
            Map<ResourceLocation, JeiRecipe> recipe = new HashMap<>();
            recipe.put(id, new JeiRecipe(id, object));
            recipes.put(uid, recipe);
        }
    }

    /**
     * Returns all recipes under the given recipe uid
     *
     * @param uid The recipe uid of the recipes
     * @return A list of JeiRecipes
     */
    public static List<JeiRecipe> getRecipes(ResourceLocation uid) {
        return new ArrayList<>(recipes.get(uid).values());
    }

    /**
     * Gets individual entries inside the recipe like recipe inputs/outputs and
     * render component overrides
     *
     * @param key The name of the entry
     * @return    The entry under the given name or null if no entry exists
     */
    @Nullable
    public JsonElement getElement(String key) {
        return recipe.get(key);
    }

    /**
     * Gets the custom values set inside the individual recipes
     *
     * @param key The key of the render component
     * @return    The json object with the custom values, or an empty json object
     */
    public JsonObject getValues(String key) {
        if (recipe.has("values") && recipe.get("values").isJsonObject()) {
            JsonObject values = recipe.getAsJsonObject("values");
            if (values.has(key) && values.get(key).isJsonObject()) {
                return values.getAsJsonObject(key);
            }
        }
        return new JsonObject();
    }

    /**
     * Used to get the registry path the individual recipe
     *
     * @return The registry path
     */
    public ResourceLocation getId() {
        return id;
    }
}

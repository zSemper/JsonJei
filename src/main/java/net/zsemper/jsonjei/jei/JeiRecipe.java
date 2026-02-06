package net.zsemper.jsonjei.jei;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.zsemper.jsonjei.utils.JsonKey;
import net.zsemper.jsonjei.utils.GsonUtils;
import net.zsemper.jsonjei.utils.Utils;

import org.slf4j.Logger;

import java.util.*;

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
     * Returns a list of {@link ItemStack}'s based on the given key.
     * The list either contains multiple items, if the recipe used a tag,
     * a single ItemStack if a single item was set or an empty list, if the key
     * could not be found in the recipe.
     *
     * @param key The key in the JSON of the recipe
     * @return A list of {@link ItemStack}'s
     */
    public List<ItemStack> getItemInputs(String key) {
        JsonObject object = GsonUtils.getAsJsonObject(recipe, key, null);
        if (object != null) {
            int count = GsonUtils.getAsInt(object, JsonKey.COUNT, 1);
            Utils.assertInRange(1, 64, count);

            if (GsonUtils.containsExclusive(object, JsonKey.ITEM, JsonKey.TAG)) {
                Item item = GsonUtils.getItem(object, JsonKey.ITEM);
                return List.of(new ItemStack(item, count));
            } else if (GsonUtils.containsExclusive(object, JsonKey.TAG, JsonKey.ITEM)) {
                TagKey<Item> tag = TagKey.create(Registries.ITEM, GsonUtils.parse(object, JsonKey.TAG));
                return Arrays.asList(SizedIngredient.of(tag, count).getItems());
            } else if (GsonUtils.containsAll(object, JsonKey.ITEM, JsonKey.TAG)) {
                LOGGER.error("Recipe '{}' cannot contain item and tag key", id);
                return List.of();
            } else {
                LOGGER.error("Recipe '{}' does not contain item or tag key", id);
                return List.of();
            }
        } else {
            return List.of();
        }
    }

    /**
     * Returns a list of {@link FluidStack}'s based on the given key.
     * The list either contains multiple fluids, if the recipe used a tag,
     * a single FluidStack if a single fluid was set or an empty list, if the key
     * could not be found in the recipe.
     *
     * @param key The key in the JSON of the recipe
     * @return A list of {@link FluidStack}'s
     */
    public List<FluidStack> getFluidInputs(String key) {
        JsonObject object = GsonUtils.getAsJsonObject(recipe, key, null);
        if (object != null) {
            int amount = GsonUtils.getAsInt(object, JsonKey.AMOUNT, 1);
            Utils.assertInRange(1, amount);

            if (GsonUtils.containsExclusive(object, JsonKey.FLUID, JsonKey.TAG)) {
                Fluid fluid = GsonUtils.getFluid(object, JsonKey.FLUID);
                return List.of(new FluidStack(fluid, amount));
            } else if (GsonUtils.containsExclusive(object, JsonKey.TAG, JsonKey.FLUID)) {
                TagKey<Fluid> tag = TagKey.create(Registries.FLUID, GsonUtils.parse(object, JsonKey.TAG));
                return Arrays.asList(SizedFluidIngredient.of(tag, amount).getFluids());
            } else if (GsonUtils.containsAll(object, JsonKey.FLUID, JsonKey.TAG)) {
                LOGGER.error("Recipe '{}' cannot contain fluid and tag key", id);
                return List.of();
            } else {
                LOGGER.error("Recipe '{}' does not contain fluid or tag key", id);
                return List.of();
            }
        } else {
            return List.of();
        }
    }

    /**
     * Returns a {@link ItemStack} based on the given key.
     *
     * @param key The key in the JSON of the recipe
     * @return A {@link ItemStack} or {@link ItemStack#EMPTY} if recipe does not have the key
     */
    public ItemStack getItemOutput(String key) {
        JsonObject object = GsonUtils.getAsJsonObject(recipe, key, null);
        if (object != null) {
            int count = GsonUtils.getAsInt(object, JsonKey.COUNT, 1);
            Utils.assertInRange(1, 64, count);

            if (object.has(JsonKey.ITEM)) {
                Item item = GsonUtils.getItem(object, JsonKey.ITEM);
                return new ItemStack(item, count);
            } else {
                LOGGER.error("Recipe '{}' does not contain item key", id);
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Returns a {@link FluidStack} based on the given key.
     *
     * @param key The key in the JSON of the recipe
     * @return A {@link FluidStack} or {@link FluidStack#EMPTY} if recipe does not have the key
     */
    public FluidStack getFluidOutput(String key) {
        JsonObject object = GsonUtils.getAsJsonObject(recipe, key, null);
        if (object != null) {
            int amount = GsonUtils.getAsInt(object, JsonKey.AMOUNT, 1);
            Utils.assertInRange(1, amount);

            if (object.has(JsonKey.FLUID)) {
                Fluid fluid = GsonUtils.getFluid(object, JsonKey.FLUID);
                return new FluidStack(fluid, amount);
            } else {
                LOGGER.error("Recipe '{}' does not contain fluid key", id);
                return FluidStack.EMPTY;
            }
        } else {
            return FluidStack.EMPTY;
        }
    }

    /**
     * Returns a {@link JsonObject} containing the info about the rendered component,
     * or {@code null} if the recipe either does not contain the key to the component
     * or the component missing one or more keys.
     *
     * @param object The JsonObject containing the render information
     * @param renderComponent The type of component to render
     * @return A {@link JsonObject} or {@code null}
     */
    public JsonObject getRenderComponent(JsonObject object, RenderComponent renderComponent) {
        JsonObject component = GsonUtils.getAsJsonObject(recipe, getKey(object), null);

        if (component != null) {
            switch (renderComponent) {
                case TEXT -> {
                    if (GsonUtils.containsAll(component, JsonKey.TEXT, JsonKey.X, JsonKey.Y)) {
                        return component;
                    }
                    return null;
                }
                case TOOLTIP -> {
                    if (GsonUtils.containsAll(component, JsonKey.TOOLTIP, JsonKey.WIDTH, JsonKey.HEIGHT)) {
                        return component;
                    }
                    return null;
                }
                case TEXTURE -> {
                    if (GsonUtils.containsAll(component, JsonKey.TEXTURE, JsonKey.WIDTH, JsonKey.HEIGHT, JsonKey.U, JsonKey.V)) {
                        return component;
                    }
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Used to check if a given JsonObject has all the info to render the component.
     *
     * @param object The JsonObject containing the render information
     * @param renderComponent The type of component to check
     * @return true, if the recipe contains the component and the component containing all it's keys
     * based on the {@code renderComponent}
     */
    public boolean hasRenderComponent(JsonObject object, RenderComponent renderComponent) {
        JsonObject component = GsonUtils.getAsJsonObject(recipe, getKey(object), null);
        if (component != null) {
            return switch (renderComponent) {
                case TEXT -> GsonUtils.containsAll(component, JsonKey.TEXT, JsonKey.X, JsonKey.Y);
                case TOOLTIP -> GsonUtils.containsAll(component, JsonKey.TOOLTIP, JsonKey.WIDTH, JsonKey.HEIGHT);
                case TEXTURE -> GsonUtils.containsAll(component, JsonKey.TEXTURE, JsonKey.WIDTH, JsonKey.HEIGHT, JsonKey.U, JsonKey.V);
            };
        }
        return false;
    }

    private String getKey(JsonObject object) {
        return GsonUtils.getAsString(object, JsonKey.KEY);
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

package net.zsemper.jsonjei.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.resources.ResourceLocation;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.loader.RecipeCategoryLoader;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@JeiPlugin
public class JsonJeiPlugin implements IModPlugin {
    private static final Map<ResourceLocation, JeiRecipeCategory> categories = new HashMap<>();

    public static void addCategory(ResourceLocation id, JeiRecipeCategory category) {
        categories.put(id, category);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return JsonJei.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        RecipeCategoryLoader.create(registration.getJeiHelpers().getGuiHelper());

        for (JeiRecipeCategory category : categories.values()) {
            registration.addRecipeCategories(category);
        }
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        for (JeiRecipeCategory category : categories.values()) {
            registration.addRecipes(category.getRecipeType(), category.getRecipes());
        }
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        for (JeiRecipeCategory category : categories.values()) {
            registration.addRecipeCatalysts(category.getRecipeType(), category.getRecipeItems());
        }
    }
}

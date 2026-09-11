package net.zsemper.jsonjei.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.jei.ingredient.energy.*;
import net.zsemper.jsonjei.jei.ingredient.experience.*;
import net.zsemper.jsonjei.jei.loader.RecipeCategoryLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class JsonJeiPlugin implements IModPlugin {
    private static final Map<ResourceLocation, JeiRecipeCategory> categories = new HashMap<>();

    @Override
    public ResourceLocation getPluginUid() {
        return JsonJei.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        RecipeCategoryLoader.create(registration.getJeiHelpers().getGuiHelper(), categories);

        for (JeiRecipeCategory category : categories.values()) {
            registration.addRecipeCategories(category);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (JeiRecipeCategory category : categories.values()) {
            registration.addRecipes(category.getRecipeType(), category.getRecipes());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (JeiRecipeCategory category : categories.values()) {
            registration.addRecipeCatalysts(category.getRecipeType(), category.getCatalysts());
        }
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(
                EnergyIngredient.TYPE,
                List.of(new EnergyIngredient()),
                new EnergyIngredientHelper(),
                new EnergyIngredientRenderer(),
                EnergyIngredient.CODEC
        );
        registration.register(
                ExperienceIngredient.TYPE,
                List.of(new ExperienceIngredient()),
                new ExperienceIngredientHelper(),
                new ExperienceIngredientRenderer(),
                ExperienceIngredient.CODEC
        );
    }
}

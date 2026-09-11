package net.zsemper.jsonjei.jei.ingredient.experience;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.zsemper.jsonjei.JsonJei;
import org.jetbrains.annotations.Nullable;

public class ExperienceIngredientHelper implements IIngredientHelper<ExperienceIngredient> {
    @Override
    public IIngredientType<ExperienceIngredient> getIngredientType() {
        return ExperienceIngredient.TYPE;
    }

    @Override
    public String getDisplayName(ExperienceIngredient ingredient) {
        return Component.translatable("json_jei.gui.xp").getString();
    }

    @SuppressWarnings("removal")
    @Override
    public String getUniqueId(ExperienceIngredient ingredient, UidContext context) {
        return getUid(ingredient, context).toString();
    }

    @Override
    public Object getUid(ExperienceIngredient ingredient, UidContext context) {
        return "xp";
    }

    @Override
    public Object getGroupingUid(ExperienceIngredient ingredient) {
        return ExperienceIngredient.class;
    }

    @Override
    public ResourceLocation getResourceLocation(ExperienceIngredient ingredient) {
        return JsonJei.id("xp");
    }

    @Override
    public ExperienceIngredient copyIngredient(ExperienceIngredient ingredient) {
        return ingredient.copy();
    }

    @Override
    public String getErrorInfo(@Nullable ExperienceIngredient ingredient) {
        if (ingredient == null) {
            return "xp null";
        }
        return getDisplayName(ingredient);
    }
}

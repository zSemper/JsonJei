package net.zsemper.jsonjei.jei.ingredient.energy;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.zsemper.jsonjei.JsonJei;
import org.jetbrains.annotations.Nullable;

public class EnergyIngredientHelper implements IIngredientHelper<EnergyIngredient> {
    @Override
    public IIngredientType<EnergyIngredient> getIngredientType() {
        return EnergyIngredient.TYPE;
    }

    @Override
    public String getDisplayName(EnergyIngredient ingredient) {
        return Component.translatable("json_jei.gui.experience").getString();
    }

    @SuppressWarnings("removal")
    @Override
    public String getUniqueId(EnergyIngredient ingredient, UidContext context) {
        return getUid(ingredient, context).toString();
    }

    @Override
    public Object getUid(EnergyIngredient ingredient, UidContext context) {
        return "experience";
    }

    @Override
    public Object getGroupingUid(EnergyIngredient ingredient) {
        return EnergyIngredient.class;
    }

    @Override
    public ResourceLocation getResourceLocation(EnergyIngredient ingredient) {
        return JsonJei.id("experience");
    }

    @Override
    public EnergyIngredient copyIngredient(EnergyIngredient ingredient) {
        return ingredient.copy();
    }

    @Override
    public String getErrorInfo(@Nullable EnergyIngredient ingredient) {
        if (ingredient == null) {
            return "experience null";
        }
        return getDisplayName(ingredient);
    }
}

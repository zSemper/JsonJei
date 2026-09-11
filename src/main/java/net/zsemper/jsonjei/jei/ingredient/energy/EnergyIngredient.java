package net.zsemper.jsonjei.jei.ingredient.energy;

import com.mojang.serialization.Codec;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.util.ExtraCodecs;

public record EnergyIngredient(int energy) {
    public EnergyIngredient() {
        this(0);
    }

    public static final IIngredientType<EnergyIngredient> TYPE = () -> EnergyIngredient.class;

    public static final Codec<EnergyIngredient> CODEC = ExtraCodecs.NON_NEGATIVE_INT.xmap(
            EnergyIngredient::new,
            EnergyIngredient::energy
    );

    public EnergyIngredient copy() {
        return new EnergyIngredient(energy);
    }
}

package net.zsemper.jsonjei.jei.ingredient.experience;

import com.mojang.serialization.Codec;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.util.ExtraCodecs;

public record ExperienceIngredient(int experience) {
    public ExperienceIngredient() {
        this(0);
    }

    public static final IIngredientType<ExperienceIngredient> TYPE = () -> ExperienceIngredient.class;

    public static final Codec<ExperienceIngredient> CODEC = ExtraCodecs.NON_NEGATIVE_INT.xmap(
            ExperienceIngredient::new,
            ExperienceIngredient::experience
    );

    public ExperienceIngredient copy() {
        return new ExperienceIngredient(experience);
    }
}

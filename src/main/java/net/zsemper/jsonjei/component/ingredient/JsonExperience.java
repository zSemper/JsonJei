package net.zsemper.jsonjei.component.ingredient;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.jei.ingredient.experience.ExperienceIngredient;
import net.zsemper.jsonjei.jei.ingredient.experience.ExperienceIngredientRenderer;
import org.jetbrains.annotations.UnknownNullability;

public record JsonExperience(String key, int x, int y, boolean coloredTooltip) implements JsonIngredient {
    public static final MapCodec<JsonExperience> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(JsonExperience::key),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("x", 0).forGetter(JsonExperience::x),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("y", 0).forGetter(JsonExperience::y),
            Codec.BOOL.optionalFieldOf("colored_tooltip", true).forGetter(JsonExperience::coloredTooltip)
    ).apply(instance, JsonExperience::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("experience");
    }

    @Override
    public void place(IRecipeSlotBuilder builder, @UnknownNullability JsonElement recipe, boolean isInput) {
        JsonJei.parseCodec(ExperienceIngredient.CODEC, recipe, ingredient -> builder
                .addIngredient(ExperienceIngredient.TYPE, ingredient)
                .setCustomRenderer(ExperienceIngredient.TYPE, new ExperienceIngredientRenderer(isInput, coloredTooltip))
        );
    }

    @Override
    public boolean placeNullable() {
        return false;
    }
}

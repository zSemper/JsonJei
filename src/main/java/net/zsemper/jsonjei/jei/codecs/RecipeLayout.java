package net.zsemper.jsonjei.jei.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.zsemper.jsonjei.component.ingredient.JsonIngredient;

import java.util.List;

public record RecipeLayout(List<JsonIngredient> input, List<JsonIngredient> output) {
    public static final Codec<RecipeLayout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            JsonIngredient.CODEC.listOf().optionalFieldOf("input", List.of()).forGetter(RecipeLayout::input),
            JsonIngredient.CODEC.listOf().optionalFieldOf("output", List.of()).forGetter(RecipeLayout::output)
    ).apply(instance, RecipeLayout::new));
}

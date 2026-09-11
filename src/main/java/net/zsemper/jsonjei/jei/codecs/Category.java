package net.zsemper.jsonjei.jei.codecs;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.zsemper.jsonjei.component.render.JsonRenderComponent;

import java.util.List;

public record Category(
        ResourceLocation uid, String title, Either<ItemStack, Drawable> icon,
        Drawable background, List<Item> catalysts, boolean recipeBorder,
        RecipeLayout recipeLayout, List<JsonRenderComponent> renderComponents
) {
    public static final Codec<Category> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("uid").forGetter(Category::uid),
            Codec.STRING.fieldOf("title").forGetter(Category::title),
            Icon.CODEC.optionalFieldOf("icon", Either.left(ItemStack.EMPTY)).forGetter(Category::icon),
            Drawable.CODEC.fieldOf("background").forGetter(Category::background),
            BuiltInRegistries.ITEM.byNameCodec().listOf().optionalFieldOf("catalysts", List.of()).forGetter(Category::catalysts),
            Codec.BOOL.optionalFieldOf("recipe_border", true).forGetter(Category::recipeBorder),
            RecipeLayout.CODEC.fieldOf("recipe_layout").forGetter(Category::recipeLayout),
            JsonRenderComponent.CODEC.listOf().optionalFieldOf("rendering", List.of()).forGetter(Category::renderComponents)
    ).apply(instance, Category::new));
}

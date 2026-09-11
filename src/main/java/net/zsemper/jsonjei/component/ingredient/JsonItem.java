package net.zsemper.jsonjei.component.ingredient;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.zsemper.jsonjei.JsonJei;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public record JsonItem(String key, int x, int y, boolean hasBackground) implements JsonIngredient {
    public static final MapCodec<JsonItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(JsonItem::key),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("x", 0).forGetter(JsonItem::x),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("y", 0).forGetter(JsonItem::y),
            Codec.BOOL.optionalFieldOf("has_background", false).forGetter(JsonItem::hasBackground)
    ).apply(instance, JsonItem::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("item");
    }

    @Override
    public void place(IRecipeSlotBuilder builder, @UnknownNullability JsonElement recipe, boolean isInput) {
        if (hasBackground) {
            builder.setStandardSlotBackground();
        }

        if (recipe != null) {
            if (isInput) {
                JsonJei.parseCodec(SizedIngredient.FLAT_CODEC, recipe, ingredient -> builder
                        .addIngredients(VanillaTypes.ITEM_STACK, List.of(ingredient.getItems()))
                );
            } else {
                JsonJei.parseCodec(ItemStack.CODEC, recipe, stack -> builder
                        .addIngredient(VanillaTypes.ITEM_STACK, stack)
                );
            }
        }
    }
}

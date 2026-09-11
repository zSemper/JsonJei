package net.zsemper.jsonjei.jei.codecs;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.zsemper.jsonjei.JsonJei;
import org.jetbrains.annotations.Nullable;

public final class Icon {
    public static final Codec<Either<ItemStack, Drawable>> CODEC = Codec.either(
            NeoForgeExtraCodecs.withAlternative(
                    BuiltInRegistries.ITEM.byNameCodec().xmap(ItemStack::new, ItemStack::getItem),
                    RecordCodecBuilder.create(instance -> instance.group(
                            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
                            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
                    ).apply(instance, (item, components) -> new ItemStack(
                            BuiltInRegistries.ITEM.wrapAsHolder(item), 1, components
                    )))
            ),
            Drawable.CODEC
    );

    @Nullable
    public static IDrawable createIcon(Either<ItemStack, Drawable> icon, IGuiHelper guiHelper) {
        return icon.map(
                stack -> stack.isEmpty() ? null : guiHelper.createDrawableItemStack(stack),
                drawable -> drawable.isTextureEmpty() ? null : guiHelper.drawableBuilder(
                        JsonJei.validateTexture(drawable.texture()),
                        drawable.u(), drawable.v(),
                        drawable.width(), drawable.height()
                ).setTextureSize(
                        drawable.textureWidth(),
                        drawable.textureHeight()
                ).build()
        );
    }
}

package net.zsemper.jsonjei.jei.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.zsemper.jsonjei.JsonJei;

public record Drawable(ResourceLocation texture, int width, int height, int u, int v, int textureWidth, int textureHeight) {
    private static final ResourceLocation EMPTY = JsonJei.id("empty");

    public static final Codec<Drawable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("texture", EMPTY).forGetter(Drawable::texture),
            ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(Drawable::width),
            ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(Drawable::height),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("u", 0).forGetter(Drawable::u),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("v", 0).forGetter(Drawable::v),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("texture_width", 256).forGetter(Drawable::textureWidth),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("texture_height", 256).forGetter(Drawable::textureHeight)
    ).apply(instance, Drawable::new));

    public boolean isTextureEmpty() {
        return texture.equals(EMPTY);
    }
}

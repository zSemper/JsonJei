package net.zsemper.jsonjei.component.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.component.reference.ReferenceCodecs;
import net.zsemper.jsonjei.component.reference.ReferenceValue;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record TimeRenderComponent(
        String key,
        Either<ItemStack, Icon> texture,
        ReferenceValue<Integer> x,
        ReferenceValue<Integer> y,
        ReferenceValue<Integer> time,
        Optional<JsonElement> values
) implements JsonRenderComponent {
    private static final NumberFormat nf = NumberFormat.getInstance();

    private record Icon(ResourceLocation texture, int u, int v, int textureWidth, int textureHeight) {
        private static final Codec<Icon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Icon::texture),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("u", 0).forGetter(Icon::u),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("v", 0).forGetter(Icon::v),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("texture_width", 256).forGetter(Icon::textureWidth),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("texture_height", 256).forGetter(Icon::textureHeight)
        ).apply(instance, Icon::new));
    }

    public static final MapCodec<TimeRenderComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(TimeRenderComponent::key),
            Codec.either(
                    NeoForgeExtraCodecs.withAlternative(
                            BuiltInRegistries.ITEM.byNameCodec().xmap(ItemStack::new, ItemStack::getItem),
                            RecordCodecBuilder.create(instance1 -> instance1.group(
                                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
                                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
                            ).apply(instance1, (item, components) -> new ItemStack(
                                    BuiltInRegistries.ITEM.wrapAsHolder(item), 1, components
                            )))
                    ),
                    Icon.CODEC
            ).optionalFieldOf("icon", Either.left(new ItemStack(Items.CLOCK))).forGetter(TimeRenderComponent::texture),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("x", new ReferenceValue.Literal<>(0)).forGetter(TimeRenderComponent::x),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.optionalFieldOf("y", new ReferenceValue.Literal<>(0)).forGetter(TimeRenderComponent::y),
            ReferenceCodecs.NON_NEGATIVE_INT_CODEC.fieldOf("time").forGetter(TimeRenderComponent::time),
            ExtraCodecs.JSON.optionalFieldOf("values").forGetter(TimeRenderComponent::values)
    ).apply(instance, TimeRenderComponent::new));

    @Override
    public ResourceLocation type() {
        return JsonJei.id("time");
    }

    @Override
    public void render(GuiGraphics guiGraphics, @Nullable JsonElement override, JsonObject recipeValues, int mouseX, int mouseY) {
        if (override != null) {
            JsonJei.parseCodec(CODEC.codec(), override, component -> component.render(guiGraphics, null, recipeValues, mouseX, mouseY));
            return;
        }

        int dx = decode(Codec.INT, "x", () -> x.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dy = decode(Codec.INT, "y", () -> y.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));
        int dTime = decode(Codec.INT, "time", () -> time.resolve(key -> GsonHelper.getAsInt(recipeValues, key)));

        if (dTime != 0) {

            texture.ifLeft(stack -> guiGraphics.renderItem(stack, dx, dy))
                   .ifRight(icon -> guiGraphics.blit(
                           JsonJei.validateTexture(icon.texture),
                           dx, dy, icon.u(), icon.v(), 16, 16,
                           icon.textureWidth(), icon.textureHeight()
                   ));

            if (mouseX > dx && mouseX < dx + 16 && mouseY > dy && mouseY < dy + 16) {
                guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, buildTooltip(dTime), mouseX, mouseY);
            }
        }
    }


    private static List<Component> buildTooltip(int ticks) {
        List<Component> tooltip = new ArrayList<>();
        double totalSeconds = ticks / 20.0;
        int minutes = (int) totalSeconds / 60;
        int seconds = (int) totalSeconds % 60;
        double tick = totalSeconds % 1;

        tooltip.add(Component.translatable("json_jei.gui.time"));
        tooltip.add(Component.literal((minutes != 0 ? nf.format(minutes) + "m " : "") + (seconds != 0 ? nf.format(seconds + tick) + "s" : "")).withColor(0xAAAAAA));
        return tooltip;
    }
}

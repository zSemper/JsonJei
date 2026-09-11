package net.zsemper.jsonjei;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.zsemper.jsonjei.component.JsonRegisterEvent;
import net.zsemper.jsonjei.component.ingredient.*;
import net.zsemper.jsonjei.component.render.*;
import net.zsemper.jsonjei.jei.loader.RecipeLoader;

import java.util.Optional;
import java.util.function.Consumer;

@Mod(JsonJei.MOD_ID)
public class JsonJei {
    public static final String MOD_ID = "json_jei";
    private static final String ext = ".png";

    public JsonJei(IEventBus eventBus, ModContainer container) {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    private void onRegisterReloadListener(AddReloadListenerEvent event) {
        event.addListener(RecipeLoader.INSTANCE);
    }

    @SubscribeEvent
    private void onRegisterJson(JsonRegisterEvent event) {
        event.registerIngredient(id("item"), JsonItem.CODEC);
        event.registerIngredient(id("fluid"), JsonFluid.CODEC);
        event.registerIngredient(id("energy"), JsonEnergy.CODEC);
        event.registerIngredient(id("experience"), JsonExperience.CODEC);

        event.registerRenderComponent(id("text"), TextRenderComponent.CODEC);
        event.registerRenderComponent(id("tooltip"), TooltipRenderComponent.CODEC);
        event.registerRenderComponent(id("texture"), TextureRenderComponent.CODEC);
        event.registerRenderComponent(id("item"), ItemRenderComponent.CODEC);
        event.registerRenderComponent(id("sprite"), SpriteRenderComponent.CODEC);
        event.registerRenderComponent(id("time"), TimeRenderComponent.CODEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Checks if a given resource location to a texture is valid
     *
     * @param location The resource location to check
     * @return         The validated resource location, able to be rendered
     * @implNote       This method only checks for a valid path and not if the
     *                 texture exists
     */
    public static ResourceLocation validateTexture(ResourceLocation location) {
        if (!location.getPath().startsWith("texture")) {
            return location;
        }
        if (location.getPath().endsWith(ext)) {
            return location;
        }
        return location.withPath(path -> path + ext);
    }

    /**
     * Helper method to parse a given codec
     *
     * @param codec   The codec to parse
     * @param element The given json element
     * @return        The optional field, present if parsing was successful,
     *                empty when parsing failed
     */
    public static <T> Optional<T> parseCodec(Codec<T> codec, JsonElement element) {
        return codec.parse(JsonOps.INSTANCE, element).result();
    }

    /**
     * Helper method to parse a given codec
     *
     * @param codec   The codec to parse
     * @param element The given json element
     * @param action  The action when the parsing was successful
     */
    public static <T> void parseCodec(Codec<T> codec, JsonElement element, Consumer<T> action) {
        codec.parse(JsonOps.INSTANCE, element).result().ifPresent(action);
    }

    /**
     * Helper method for rendering something on top of everything
     *
     * @param render The call what to render
     */
    public static void renderOver(GuiGraphics guiGraphics, Consumer<GuiGraphics> render) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 200);
        render.accept(guiGraphics);
        poseStack.popPose();
    }
}

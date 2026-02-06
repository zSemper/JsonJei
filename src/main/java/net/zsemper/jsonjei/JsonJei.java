package net.zsemper.jsonjei;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.zsemper.jsonjei.loader.RecipeLoader;

import org.slf4j.Logger;

@Mod(JsonJei.MOD_ID)
public class JsonJei {
    public static final String MOD_ID = "json_jei";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JsonJei(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onRegisterReloadListener(AddReloadListenerEvent event) {
        event.addListener(RecipeLoader.getInstance());
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}

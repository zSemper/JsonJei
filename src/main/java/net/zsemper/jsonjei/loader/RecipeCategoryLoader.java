package net.zsemper.jsonjei.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;

import mezz.jei.api.helpers.IGuiHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.jei.JeiRecipeCategory;
import net.zsemper.jsonjei.jei.JsonJeiPlugin;
import net.zsemper.jsonjei.utils.GsonUtils;
import net.zsemper.jsonjei.utils.JsonKey;
import net.zsemper.jsonjei.utils.Utils;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads all JSONs under the {@code data/[id]/jei_category} path and
 * stores them in a {@code Map<String, JsonObject> categories} HashMap.
 * Is only triggered once per server startup and cannot be retriggered using {@code /reload}
 */
@EventBusSubscriber(modid = JsonJei.MOD_ID)
public class RecipeCategoryLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, JsonObject> categories = new HashMap<>();

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void loadCategories(ServerStartingEvent event) {
        ResourceManager manager = event.getServer().getResourceManager();
        Map<ResourceLocation, Resource> resources = manager.listResources(
                "jei_category",
                path -> path.getPath().endsWith(".json")
        );

        for (var entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().open()) {
                JsonObject object = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                ResourceLocation id = Utils.validate(GsonUtils.getAsString(object, JsonKey.UID, null));

                if (id != null) {
                    if (categories.containsKey(id)) {
                        LOGGER.warn("Failed to load category. Category with uid {} already exists", id);
                        continue;
                    }

                    categories.put(id, object);
                } else {
                    LOGGER.error("Failed to load jei category {} because 'uid' is null", entry.getKey());
                }

            } catch (IOException ex) {
                LOGGER.error("Failed to load jei category {}", entry.getKey(), ex);
            }
        }
    }

    public static void create(IGuiHelper guiHelper) {
        for (var entry : categories.entrySet()) {
            JeiRecipeCategory category = new JeiRecipeCategory(entry.getValue(), guiHelper);
            JsonJeiPlugin.addCategory(entry.getKey(), category);
        }
    }
}

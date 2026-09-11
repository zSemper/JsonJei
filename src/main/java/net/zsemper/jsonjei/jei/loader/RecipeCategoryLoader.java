package net.zsemper.jsonjei.jei.loader;

import com.google.gson.JsonElement;
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
import net.zsemper.jsonjei.jei.codecs.Category;
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
    private static final Map<ResourceLocation, Category> categories = new HashMap<>();

    @SubscribeEvent
    public static void loadCategories(ServerStartingEvent event) {
        ResourceManager manager = event.getServer().getResourceManager();
        Map<ResourceLocation, Resource> resources = manager.listResources(
                "jei_category",
                path -> path.getPath().endsWith(".json")
        );

        for (var entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().open()) {
                JsonElement element = JsonParser.parseReader(new InputStreamReader(stream));

                var opt = JsonJei.parseCodec(Category.CODEC, element);
                if (opt.isPresent()) {
                    Category category = opt.get();
                    if (categories.containsKey(category.uid())) {
                        LOGGER.warn("Jei Category with id '{}' already exists and will be skipped", category.uid());
                        continue;
                    }
                    categories.put(category.uid(), category);
                } else {
                    LOGGER.error("Failed to load jei category '{}'", entry.getKey());
                }

            } catch (IOException ex) {
                LOGGER.error("Failed to load jei category {}", entry.getKey(), ex);
            }
        }
    }

    public static void create(IGuiHelper guiHelper, Map<ResourceLocation, JeiRecipeCategory> jeiCategories) {
        for (var entry : categories.entrySet()) {
            JeiRecipeCategory category = new JeiRecipeCategory(entry.getValue(), guiHelper);
            jeiCategories.put(entry.getKey(), category);
        }
    }
}

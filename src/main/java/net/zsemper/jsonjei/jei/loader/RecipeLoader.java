package net.zsemper.jsonjei.jei.loader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.jei.JeiRecipe;
import org.slf4j.Logger;

import java.util.Map;

/**
 * Loads all jsons under the {@code data/[namespace]/jei_recipe} path and
 * registers them as {@link JeiRecipe}'s, to be used as recipes for the
 * individual jei categories.
 * <p>
 * The recipes are loaded via a {@link SimpleJsonResourceReloadListener}
 * and are reloadable on using {@code /reload}
 */
public class RecipeLoader extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final RecipeLoader INSTANCE = new RecipeLoader();

    private RecipeLoader() {
        super(new Gson(), "jei_recipe");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> recipes, ResourceManager resourceManager, ProfilerFiller profiler) {
        JeiRecipe.clearRecipe();
        for (var entry : recipes.entrySet()) {
            JsonElement element = entry.getValue();

            var opt = JsonJei.parseCodec(ResourceLocation.CODEC.fieldOf("type").codec(), element);
            if (opt.isPresent()) {
                JeiRecipe.addRecipe(opt.get(), entry.getKey(), element.getAsJsonObject());
            } else {
                LOGGER.error("Failed to load recipe '{}', because field 'type' is missing", entry.getKey());
            }
        }
    }
}

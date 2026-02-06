package net.zsemper.jsonjei.loader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.zsemper.jsonjei.jei.JeiRecipe;
import net.zsemper.jsonjei.utils.GsonUtils;
import net.zsemper.jsonjei.utils.JsonKey;
import net.zsemper.jsonjei.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Map;

/**
 * <p>Loads all JSONs under the {@code data/[id]/jei_recipe} path and
 * registers them as {@link JeiRecipe}'s, to be used as recipes for the
 * individual jei categories.</p>
 *
 * <p>The recipes are loaded via a {@link SimpleJsonResourceReloadListener}
 * and are reloadable on using /reload</p>
 */
public class RecipeLoader extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static RecipeLoader INSTANCE = null;

    public static RecipeLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RecipeLoader();
        }
        return INSTANCE;
    }

    private RecipeLoader() {
        super(new Gson(), "jei_recipe");
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> recipes, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        JeiRecipe.clearRecipe();
        for (var entry : recipes.entrySet()) {
            JsonObject object = entry.getValue().getAsJsonObject();
            ResourceLocation uid = Utils.validate(GsonUtils.getAsString(object, JsonKey.TYPE, null));

            if (uid != null) {
                JeiRecipe.addRecipe(uid, entry.getKey(), object);
            } else {
                LOGGER.error("Failed to load recipe {} because 'uid' in null", entry.getKey());
            }
        }
    }
}

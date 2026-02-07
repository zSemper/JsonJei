package net.zsemper.jsonjei.jei;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.zsemper.jsonjei.utils.JsonKey;
import net.zsemper.jsonjei.utils.GsonUtils;
import net.zsemper.jsonjei.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class JeiRecipeCategory implements IRecipeCategory<JeiRecipe> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation uid;
    private final Component title;
    private final IDrawable icon;
    private final IDrawable background;
    private final boolean recipeBorder;
    private final JsonArray recipeItems;
    private final JsonObject recipe;
    private final JsonArray rendering;

    public JeiRecipeCategory(JsonObject object, IGuiHelper guiHelper) {
        this.uid = Utils.validate(GsonUtils.getAsString(object, JsonKey.UID));
        this.title = Component.translatable(GsonUtils.getAsString(object, JsonKey.TITLE, "json_jei.default.title"));
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, getIcon(object));
        JsonObject backgroundObject = GsonUtils.getAsJsonObject(object, JsonKey.BACKGROUND, Utils.backgroundDefault());
        this.background = guiHelper.createDrawable(
                Utils.validateFile(GsonUtils.getAsString(backgroundObject, JsonKey.TEXTURE, "json_jei:textures/gui/default"), "png"),
                GsonUtils.getAsInt(backgroundObject, JsonKey.U, 0),
                GsonUtils.getAsInt(backgroundObject, JsonKey.V, 0),
                GsonUtils.getAsInt(backgroundObject, JsonKey.WIDTH, 64),
                GsonUtils.getAsInt(backgroundObject, JsonKey.HEIGHT, 64)
        );
        this.recipeBorder = GsonUtils.getAsBoolean(object, JsonKey.RECIPE_BORDER, true);
        this.recipeItems = GsonUtils.getAsJsonArray(object, JsonKey.RECIPE_ITEMS, null);
        this.recipe = GsonUtils.getAsJsonObject(object, JsonKey.RECIPE, null);
        this.rendering = GsonUtils.getAsJsonArray(object, JsonKey.RENDERING, null);
    }

    @Override
    public @NotNull RecipeType<JeiRecipe> getRecipeType() {
        return new RecipeType<>(uid, JeiRecipe.class);
    }

    public List<JeiRecipe> getRecipes() {
        return JeiRecipe.getRecipes(uid);
    }

    public Item[] getRecipeItems() {
        if (recipeItems != null) {
            Item[] items = new Item[recipeItems.size()];
            for (int i = 0; i < recipeItems.size(); i++) {
                items[i] = Utils.getItem(recipeItems.get(i).getAsString());
            }
            return items;
        } else {
            return new Item[0];
        }
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull JeiRecipe jeiRecipe, @NotNull IFocusGroup focuses) {
        ResourceLocation id = jeiRecipe.getId();
        if (recipe != null){
            JsonArray inputs = GsonUtils.getAsJsonArray(recipe, JsonKey.INPUT, null);

            if (inputs != null) {
                for (JsonElement inputEntry : inputs) {
                    JsonObject object = inputEntry.getAsJsonObject();

                    String key = GsonUtils.getAsString(object, JsonKey.KEY, null);
                    int x = GsonUtils.getAsInt(object, JsonKey.X, 0);
                    int y = GsonUtils.getAsInt(object, JsonKey.Y, 0);

                    if (key != null) {
                        String type = GsonUtils.getAsString(object, JsonKey.TYPE, null);
                        switch (type) {
                            case JsonKey.ITEM -> {
                                List<ItemStack> output = jeiRecipe.getItemInputs(key);
                                if (!output.isEmpty()) {
                                    builder.addInputSlot(x, y).addIngredients(VanillaTypes.ITEM_STACK, output);
                                }
                            }
                            case JsonKey.FLUID -> {
                                int capacity = GsonUtils.getAsInt(object, JsonKey.CAPACITY, 1);
                                boolean showTooltip = GsonUtils.getAsBoolean(object, JsonKey.SHOW_TOOLTIP, false);
                                int width = GsonUtils.getAsInt(object, JsonKey.WIDTH, 16);
                                int height = GsonUtils.getAsInt(object, JsonKey.HEIGHT, 16);

                                List<FluidStack> output = jeiRecipe.getFluidInputs(key);
                                if (!output.isEmpty()) {
                                    builder.addInputSlot(x, y).addIngredients(NeoForgeTypes.FLUID_STACK, output).setFluidRenderer(capacity, showTooltip, width, height);
                                }
                            }
                            case null -> LOGGER.warn("Missing ingredient type for input slot {} in recipe {}", key, id);
                            default ->
                                    LOGGER.warn("Unknown ingredient type {} for input slot {} in recipe {}", type, key, id);
                        }
                    } else {
                        LOGGER.warn("Failed to create input ingredient slot because 'key' is null.");
                    }
                }
            }

            JsonArray outputs = GsonUtils.getAsJsonArray(recipe, JsonKey.OUTPUT, null);
            if (outputs != null) {
                for (JsonElement outputEntry : outputs) {
                    JsonObject object = outputEntry.getAsJsonObject();

                    String key = GsonUtils.getAsString(object, JsonKey.KEY, null);
                    int x = GsonUtils.getAsInt(object, JsonKey.X, 0);
                    int y = GsonUtils.getAsInt(object, JsonKey.Y, 0);

                    if (key != null) {
                        String type = GsonUtils.getAsString(object, JsonKey.TYPE, null);
                        switch (type) {
                            case JsonKey.ITEM -> {
                                ItemStack output = jeiRecipe.getItemOutput(key);
                                if (output != ItemStack.EMPTY) {
                                    builder.addOutputSlot(x, y).addIngredient(VanillaTypes.ITEM_STACK, output);
                                }
                            }
                            case JsonKey.FLUID -> {
                                int capacity = GsonUtils.getAsInt(object, JsonKey.CAPACITY, 1);
                                boolean showTooltip = GsonUtils.getAsBoolean(object, JsonKey.SHOW_TOOLTIP, false);
                                int width = GsonUtils.getAsInt(object, JsonKey.WIDTH, 16);
                                int height = GsonUtils.getAsInt(object, JsonKey.HEIGHT, 16);

                                FluidStack output = jeiRecipe.getFluidOutput(key);
                                if (output != FluidStack.EMPTY) {
                                    builder.addOutputSlot(x, y).addIngredient(NeoForgeTypes.FLUID_STACK, output).setFluidRenderer(capacity, showTooltip, width, height);
                                }

                            }
                            case null ->
                                    LOGGER.warn("Missing ingredient type for output slot {} in recipe {}", key, id);
                            default ->
                                    LOGGER.warn("Unknown ingredient type {} for output slot {} in recipe {}", type, key, id);
                        }
                    } else {
                        LOGGER.warn("Failed to create output ingredient slot because 'key' is null.");
                    }
                }
            }
        }
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(JeiRecipe recipe) {
        return recipe.getId();
    }

    @Override
    public void draw(@NotNull JeiRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        ResourceLocation id = recipe.getId();

        if (rendering != null) {
            for (JsonElement renderEntry : rendering) {
                JsonObject object = renderEntry.getAsJsonObject();

                switch (GsonUtils.getAsString(object, JsonKey.TYPE)) {
                    case JsonKey.TEXT -> {
                        if (GsonUtils.containsAll(object, JsonKey.DEFAULT, JsonKey.KEY)) {
                            if (recipe.hasRenderComponent(object, RenderComponent.TEXT)) {
                                drawString(guiGraphics, recipe.getRenderComponent(object, RenderComponent.TEXT), id);
                            } else {
                                drawString(guiGraphics, GsonUtils.getAsJsonObject(object, JsonKey.DEFAULT), id);
                            }
                        } else if (object.has(JsonKey.DEFAULT)) {
                            drawString(guiGraphics, GsonUtils.getAsJsonObject(object, JsonKey.DEFAULT), id);
                        } else if (object.has(JsonKey.KEY)) {
                            drawString(guiGraphics, recipe.getRenderComponent(object, RenderComponent.TEXT), id);
                        }
                    }
                    case JsonKey.TOOLTIP -> {
                        if (GsonUtils.containsAll(object, JsonKey.DEFAULT, JsonKey.KEY)) {
                            if (recipe.hasRenderComponent(object, RenderComponent.TOOLTIP)) {
                                drawTooltip(guiGraphics, recipe.getRenderComponent(object, RenderComponent.TOOLTIP), id, mouseX, mouseY);
                            } else {
                                drawTooltip(guiGraphics, GsonUtils.getAsJsonObject(object, JsonKey.DEFAULT), id, mouseX, mouseY);
                            }
                        } else if (object.has(JsonKey.DEFAULT)) {
                            drawTooltip(guiGraphics, GsonUtils.getAsJsonObject(object, JsonKey.DEFAULT), id, mouseX, mouseY);
                        } else if (object.has(JsonKey.KEY)) {
                            drawTooltip(guiGraphics, recipe.getRenderComponent(object, RenderComponent.TOOLTIP), id, mouseX, mouseY);
                        }
                    }
                    case JsonKey.TEXTURE -> {
                        if (GsonUtils.containsAll(object, JsonKey.DEFAULT, JsonKey.KEY)) {
                            if (recipe.hasRenderComponent(object, RenderComponent.TEXTURE)) {
                                drawTexture(guiGraphics, recipe.getRenderComponent(object, RenderComponent.TEXTURE), id);
                            } else {
                                drawTexture(guiGraphics, GsonUtils.getAsJsonObject(object, JsonKey.DEFAULT), id);
                            }
                        } else if (object.has(JsonKey.DEFAULT)) {
                            drawTexture(guiGraphics, GsonUtils.getAsJsonObject(object, JsonKey.DEFAULT), id);
                        } else if (object.has(JsonKey.KEY)) {
                            drawTexture(guiGraphics, recipe.getRenderComponent(object, RenderComponent.TEXTURE), id);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean needsRecipeBorder() {
        return recipeBorder;
    }

    private ItemStack getIcon(JsonObject object) {
        ResourceLocation itemKey = ResourceLocation.parse(GsonUtils.getAsString(object, JsonKey.ICON, "minecraft:barrier"));
        return new ItemStack(Objects.requireNonNullElse(BuiltInRegistries.ITEM.get(itemKey), Items.BARRIER));
    }

    private void drawString(GuiGraphics guiGraphics, JsonObject object, ResourceLocation id) {
        if (object != null) {
            Font font = Minecraft.getInstance().font;

            Component text = Component.translatable(GsonUtils.getAsString(object, JsonKey.TEXT));
            int x = GsonUtils.getAsInt(object, JsonKey.X);
            int y = GsonUtils.getAsInt(object, JsonKey.Y);
            int color;
            try {
                color = Integer.decode(GsonUtils.getAsString(object, JsonKey.COLOR, "0xffffff"));
            } catch (NumberFormatException ignored) {
                color = 0xffffff;
            }
            boolean shadow = GsonUtils.getAsBoolean(object, JsonKey.SHADOW, true);

            guiGraphics.drawString(font, text, x, y, color, shadow);
        } else {
            warnRender("Failed to draw text component in recipe '{}', as it does not provide all required properties", id);
        }
    }

    private void drawTooltip(GuiGraphics guiGraphics, JsonObject object, ResourceLocation id, double mouseX, double mouseY) {
        if (object != null) {
            Font font = Minecraft.getInstance().font;

            List<Component> lines = new ArrayList<>();
            for (JsonElement element : GsonUtils.getAsJsonArray(object, JsonKey.TOOLTIP)) {
                lines.add(Component.translatable(element.getAsString()));
            }

            int x = GsonUtils.getAsInt(object, JsonKey.X, 0);
            int y = GsonUtils.getAsInt(object, JsonKey.Y, 0);
            int width = GsonUtils.getAsInt(object, JsonKey.WIDTH);
            int height = GsonUtils.getAsInt(object, JsonKey.HEIGHT);

            if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height) {
                guiGraphics.renderComponentTooltip(font, lines, (int) mouseX, (int) mouseY);
            }
        } else {
            warnRender("Failed to draw tooltip component in recipe '{}', as it does not provide all required properties", id);
        }
    }

    private void drawTexture(GuiGraphics guiGraphics, JsonObject object, ResourceLocation id) {
        if (object != null) {
            ResourceLocation texture = Utils.validateFile(GsonUtils.getAsString(object, JsonKey.TEXTURE), ".png");
            int width = GsonUtils.getAsInt(object, JsonKey.WIDTH);
            int height = GsonUtils.getAsInt(object, JsonKey.HEIGHT);
            int u = GsonUtils.getAsInt(object, JsonKey.U);
            int v = GsonUtils.getAsInt(object, JsonKey.V);
            int x = GsonUtils.getAsInt(object, JsonKey.X, 0);
            int y = GsonUtils.getAsInt(object, JsonKey.Y, 0);

            if (object.has(JsonKey.TIME)) {
                int time = GsonUtils.getAsInt(object, JsonKey.TIME);
                String startDirection = GsonUtils.getAsString(object, JsonKey.START_DIRECTION, "left");
                boolean inverted = GsonUtils.getAsBoolean(object, JsonKey.INVERTED, false);

                Utils.blitAnimated(
                        guiGraphics, texture, width, height,
                        u, v, x, y, time, startDirection, inverted
                );
            } else {
                guiGraphics.blit(texture, x, y, u, v, width, height);
            }
        } else {
            warnRender("Failed to draw texture component in recipe '{}', as it does not provide all required properties", id);
        }
    }

    // Prints out a warning only every few seconds, to not spam the logs
    private int counter = 0;
    private void warnRender(String text, Object... args) {
        if (counter == 500) {
            LOGGER.warn(text, args);
            counter = 0;
        } else {
            counter++;
        }
    }
}

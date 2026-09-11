package net.zsemper.jsonjei.jei;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.zsemper.jsonjei.JsonJei;
import net.zsemper.jsonjei.component.ingredient.JsonIngredient;
import net.zsemper.jsonjei.component.ingredient.JsonRenderedIngredient;
import net.zsemper.jsonjei.component.render.JsonRenderComponent;
import net.zsemper.jsonjei.jei.codecs.Category;
import net.zsemper.jsonjei.jei.codecs.Drawable;
import net.zsemper.jsonjei.jei.codecs.Icon;
import net.zsemper.jsonjei.jei.codecs.RecipeLayout;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JeiRecipeCategory implements IRecipeCategory<JeiRecipe> {
    private final ResourceLocation uid;
    private final Component title;
    @Nullable
    private final IDrawable icon;
    private final IDrawable background;
    private final boolean recipeBorder;
    private final List<Item> catalysts;
    private final List<JsonIngredient> inputs;
    private final List<JsonIngredient> outputs;
    private final List<JsonRenderComponent> renderComponents;

    public JeiRecipeCategory(Category category, IGuiHelper guiHelper) {
        uid = category.uid();
        title = Component.translatableWithFallback(category.title(), category.title());
        icon = Icon.createIcon(category.icon(), guiHelper);
        Drawable drawable = category.background();
        if (drawable.isTextureEmpty()) {
            background = guiHelper.createBlankDrawable(drawable.width(), drawable.height());
        } else {
            background = guiHelper
                    .drawableBuilder(JsonJei.validateTexture(drawable.texture()), drawable.u(), drawable.v(), drawable.width(), drawable.height())
                    .setTextureSize(drawable.textureWidth(), drawable.textureHeight())
                    .build();
        }
        recipeBorder = category.recipeBorder();
        catalysts = category.catalysts();
        RecipeLayout layout = category.recipeLayout();
        inputs = layout.input();
        outputs = layout.output();
        renderComponents = category.renderComponents();
    }

    @Override
    public RecipeType<JeiRecipe> getRecipeType() {
        return new RecipeType<>(uid, JeiRecipe.class);
    }

    public List<JeiRecipe> getRecipes() {
        return JeiRecipe.getRecipes(uid);
    }

    public Item[] getCatalysts() {
        return catalysts.toArray(Item[]::new);
    }

    @Override
    public Component getTitle() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, JeiRecipe jeiRecipe, IFocusGroup focuses) {
        for (JsonIngredient input : inputs) {
            JsonElement element = jeiRecipe.getElement(input.key());
            if (input.placeNullable() || element != null) {
                input.place(builder.addInputSlot(input.x(), input.y()), element, true);
            }
        }

        for (JsonIngredient output : outputs) {
            JsonElement element = jeiRecipe.getElement(output.key());
            if (output.placeNullable() || element != null) {
                output.place(builder.addOutputSlot(output.x(), output.y()), element, false);
            }
        }
    }

    @Override
    public void draw(JeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        try {
            for (JsonRenderComponent renderComponent : renderComponents) {
                String key = renderComponent.key();
                JsonElement element = recipe.getElement(key);
                if (element != null && element.isJsonObject()) {
                    JsonObject object = element.getAsJsonObject();
                    if (!object.has("key")) {
                        object.addProperty("key", key);
                        renderComponent.render(guiGraphics, object, recipe.getValues(key), (int) mouseX, (int) mouseY);
                    }
                } else {
                    renderComponent.render(guiGraphics, null, recipe.getValues(key), (int) mouseX, (int) mouseY);
                }
            }
        } catch (Exception e) {
            throw new JsonParseException("Expected key '" + e.getMessage() + "' is recipe '" + recipe.getId() + "', but it's missing");
        }


        for (JsonIngredient input : inputs) {
            if (input instanceof JsonRenderedIngredient rendered) {
                rendered.render(guiGraphics, (int) mouseX, (int) mouseY);
            }
        }
        for (JsonIngredient output : outputs) {
            if (output instanceof JsonRenderedIngredient rendered) {
                rendered.render(guiGraphics, (int) mouseX, (int) mouseY);
            }
        }
    }

    @Override
    public boolean needsRecipeBorder() {
        return recipeBorder;
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(JeiRecipe recipe) {
        return recipe.getId();
    }
}

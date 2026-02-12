# Json Jei

A mod about adding custom jei categories and recipes via data- and resource-packs. Useful for modpack creator to show custom functionalities or recipes inside jei.

---

## Categories

Jei uses categories to show different recipes. To create a category the JSON file must be placed in a specific path: `data/[namespace]/jei_category`.
The JSON for a category is made up of three different parts

1. [Category](docs/entries/category.md)

2. [Recipe Layout](docs/entries/recipe_layout.md)

3. [Rendering](docs/entries/rendering.md)

Even though the `jei_category` folder is inside the `data` folder structure, the JSONs inside are not affected by the `/reload` command.
To reload the categories, you have to restart Minecraft.

## Recipes

Recipes for the categories are not placed inside the `recipe` folder like normal recipes. These custom recipes have to be placed inside the `data/[namespace]/jei_recipe`
folder to be registered by the mod.

[Recipes](docs/entries/recipes.md)

Like normal recipes, custom recipes can also be placed in subfolder inside the `jei_recipe` folder and can be reloaded using the `/reload` command.

---
## Example

Under `docs/example` is a working data- and resource-pack that can be used as reference alongside the documentation.

[Example](docs/example)

---
## Feature request

Feature requests are always welcome to expand this mod.
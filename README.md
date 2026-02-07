# Json Jei

JsonJei is a mod allowing you to add custom jei categories and recipes via data and resource packs.

---

## Categories

Jei uses categories to show different recipes. To create categories the JSON files have to be placed in a specific path: `data/[id]/jei_category`.
A JSON for a category is made up of three different parts

1. [Category](docs/entries/category.md)

2. [Recipe Layout](docs/entries/recipe_layout.md)

3. [Rendering](docs/entries/rendering.md)

Even tho the `jei_category` folder is inside the `data` folder structure, the JSONs inside are not affected by the `/reload` command.
To reload the categories you have to restart Minecraft.

## Recipes

Recipe for the categories are not placed inside the `recipe` folder like normal recipes. These custom recipes have to be placed inside the `data/[id]/jei_recipe`
folder to be registered by the mod. And like normal recipes, custom recipes can also be placed in subfolder inside the `jei_recipe` folder.

[Recipes](docs/entries/recipes.md)

Like normal recipes, category specific recipes inside the `jei_recipe` can be reloaded be the `/reload` command.

---
## Example

Under `docs/example` is a working data and resource pack that can be used as reference alongside the documentation.

[Example](docs/example)

---
## Feature request

Feature requests are always welcome to expand this mod.
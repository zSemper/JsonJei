# Category

A jei category requires a few general values to be created and work. The most important value is the `uid`. It is used to differentiate categories from one another, thus it has to be **unique**. 
The `uid` is also used inside recipes to set which category the recipe belongs to. The game **will** crash, if a category does not contain a `uid`.

| Value           | Explanation                                                                                                                          | Optional                         | Example                                                     |
|-----------------|--------------------------------------------------------------------------------------------------------------------------------------|----------------------------------|-------------------------------------------------------------|
| `title`         | The title of the category. This can either be plain text or a translation key.                                                       | Manditory.                       | `"title": "Example category"`                               |
| `icon`          | The item icon that is displayed at the top.                                                                                          | Manditory.                       | `"icon": "minecraft:diamond_block"`                         |
| `background`    | Holds information abount the category background. More info below.                                                                   | Manditory.                       | `"background": {...}`                                       |
| `recipe_border` | Controls if jei renders an extra border for each recipe. Should only be set to `false`, if the background contains a visable border. | Optional. Default: `true`        | `"recipe_border: true"`                                     |
| `recipe_items`  | Holds the items that will be shown at the side of the category, e.g.: smelting has a furnace, crafting a crafting table.             | Optional. Default: no side items | `recipe_items: [ "minecraft:diamond", "minecraft:beacon" ]` |

---
## Background

| Value     | Explanation                                                  | Example                                        |
|-----------|--------------------------------------------------------------|------------------------------------------------|
| `texture` | The texture that is used as a background.                    | `"texture": "example:textures/gui/background"` |
| `u`       | The x position where the texture starts in the texture file. | `"u": 10`                                      |
| `v`       | The y position where the texture starts in the texture file. | `"v": 50`                                      |
| `width`   | The width of the texture in the texture file.                | `"width": 80`                                  |
| `height`  | The height of the texture in the texture file.               | `"height": 40`                                 |

---
## Full Example

```json
{
  "uid": "example:example_category",
  "title": "Example Category",
  "icon": "minecraft:diamond_block",
  "background": {
    "texture": "example:textures/gui/background",
    "u": 10,
    "y": 50,
    "width": 80,
    "height": 40
  },
  "recipe_border": true,
  "recipe_items": [
    "minecraft:diamond",
    "minecraft:beacon"
  ]
}
```
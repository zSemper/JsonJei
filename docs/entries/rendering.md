# Rendering

Rendering is not required for the creation of a category. 

Every rendering component consist of 3 different values: the `type`, `key`, `default` value.

The `type` value is mandatory, as it describes which component should be rendered. 
The `default` value is used to set the data of the component for the entire category.
The `key` value gives the component a **unique** id (that should be **different** to any recipe keys) that can be used in a recipe to override the `default` value. 
Either the `key` or `default` can be left out, tho if the `default` value is left out every recipe needs to implement the `key`.

```js
{
  "rendering": [
    {
      "type": "text",
      "key": "useful_text",
      "default": {
        // default values
      }
    }
  ]
}
```

To override a component, add the `key` inside the recipe and specify the new values of the component.

Example with the key `"key": "example_key"` inside the recipe:
```js
{
  "example_key": {
    // values of the new component 
  }
}
```

---
## Text

The text component renders a single text line.

| Value    | Explanation                                                                               | Optional?                      | Example                  |
|----------|-------------------------------------------------------------------------------------------|--------------------------------|--------------------------|
| `text`   | Holds the text that will be rendered. This can either be plain text or a translation key. | Mandatory.                     | `"text": "Example text"` |
| `x`      | Describes where on the x axis the text is rendered.                                       | Mandatory.                     | `"x": 10`                |
| `y`      | Describes where on the y axis the text is rendered.                                       | Mandatory.                     | `"y": 10`                |
| `color`  | Allows for changing the color of the text, in form of a hex color.                        | Optional. Default: `"#ffffff"` | `"color": "#00ffff"`     |
| `shadow` | Renders the underlying shadow of the text                                                 | Optional. Default `true`       | `"shadow": false`        |

Example:
```json
{
  "text": "Example text",
  "x": 10,
  "y": 10,
  "color": "#00ffff",
  "shadow": false
}
```

---
## Tooltip

The tooltip component is an invisible field that can be hovered over to show a single or multiple lines of text.

| Value     | Explanation                                                                                              | Optional?  | Example                             |
|-----------|----------------------------------------------------------------------------------------------------------|------------|-------------------------------------|
| `tooltip` | Holds the lines that get shown when hovering over it. They can either be plain text or translation keys. | Mandatory. | `"tooltip": [ "Line 1", "Line 2" ]` |
| `x`       | Describes where on the x axis the tooltip field is placed.                                               | Mandatory. | `"x": 10`                           |
| `y`       | Describes where on the y axis the tooltip field is placed.                                               | Mandatory. | `"y": 10`                           |
| `width`   | Describes how wide the tooltip field is.                                                                 | Mandatory. | `"widht": 16`                       |
| `height`  | Describes how large the tooltip field is.                                                                | Mandatory. | `"height": 16`                      |

Example:
```json
{
  "tooltip": [
    "Line 1",
    "Line 2"
  ],
  "x": 10,
  "y": 10,
  "width": 16,
  "height": 16
}
```

---
## Texture

The texture component is used to render extra static or animated textures, like progress arrows, onto the recipes. 
The texture file has to be `256 x 256` pixel in size to properly render.

| Value             | Explanation                                                                                               | Optional?                   | Example                                      |
|-------------------|-----------------------------------------------------------------------------------------------------------|-----------------------------|----------------------------------------------|
| `texture`         | The actual texture that is rendered.                                                                      | Mandatory.                  | `"texture": "example:textures/gui/progress"` |
| `width`           | The width of the texture in the texture file.                                                             | Mandatory.                  | `"width": 32`                                |
| `height`          | The height of the texture in the texture file.                                                            | Mandatory.                  | `"height": 32`                               |
| `u`               | The x position where the texture starts in the texture file.                                              | Mandatory.                  | `"u": 16`                                    |
| `v`               | The y position where the texture starts in the texture file.                                              | Mandatory.                  | `"v": 16`                                    |
| `x`               | The x position where the texture is rendered in the recipe.                                               | Optional. Default: `0`      | `"x": 64`                                    |
| `y`               | The y position where the texture is rendered in the recipe.                                               | Optional. Default: `0`      | `"x": 64`                                    |
| `time`            | Controls if the texture is animated or static. The time in ticks the texture needs to finished one cycle. | Optional.                   | `"time": 100`                                |
| `start_direction` | Contols where the animated texture starts from. Possible values: `"left"`, `"right"`, `"top"`, `"bottom"` | Optional. Default: `"left"` | `"start_direction": "right"`                 |
| `inverted`        | If true, texture will render full and disapear over time.                                                 | Optional. Default: `false`  | `"inverted": true`                           |

Example:
```json
{
  "texture": "example:textures/gui/progress",
  "width": 32,
  "height": 32,
  "u": 16,
  "v": 16,
  "x": 64,
  "y": 64,
  "time": 100,
  "start_direction": "right",
  "inverted": true
}
```

---
## Full example

```json
{
  "rendering": [
    {
      "type": "text",
      "key": "text_key",
      "default": {
        "text": "Example text",
        "x": "16",
        "y": 16
      }
    },
    {
      "type": "tooltip",
      "key": "important_tooltip",
      "default": {
        "tooltip": [
          "Important line",
          "Extra important line"
        ],
        "x": 32,
        "y": 32,
        "width": 16,
        "height": 16
      }
    },
    {
      "type": "text",
      "key": "progress",
      "default": {
        "texture": "example:textures/gui/progress",
        "width": 23,
        "height": 18,
        "u": 0,
        "v": 0,
        "x": 38,
        "y": 17,
        "time": 200
      }
    }
  ]
}
```
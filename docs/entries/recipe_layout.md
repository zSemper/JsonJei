# Recipe Layout

This set's the structure of recipes and the placement of items and fluids in the category.

The `recipe` object is split between `input` and `output`.

```js
{
  "recipe": {
    "input": [
      // slot placement
    ],
    "output": [
      // slot placement
    ]
  }
}
```

The `input` and `output` slots use the same syntax, so everything that appies to the input slots also applies to the output slots.

---
## Slot placement

Each slot has a `"type"` that is either an `"item"` or a `"fluid"` slot. They also require a **unique** `key`.

| Value          | Explanation                                                          | Optional                                          | Example                |
|----------------|----------------------------------------------------------------------|---------------------------------------------------|------------------------|
| `x`            | Sets the x position of the slot.                                     | Manditory.                                        | `"x": 10`              |
| `y`            | Sets the y position of the slot.                                     | Manditory.                                        | `"y": 10`              |
| `capacity`     | Sets the capacity of the fluid slot.                                 | Optional. Only used by fluid slots.               | `"capacity": 8000`     |
| `show_tooltip` | Shows extra tooltip info for the fluid slot, like 1.000mn / 8.000mB. | Optional. Only used by fluid slots.               | `"show_tooltip": true` |
| `width`        | Sets the width of the fluid slot.                                    | Optional. Only used by fluid slots. Default: `16` | `"width": 16`          |
| `height`       | Sets the height of the fluid slot.                                   | Optional. Only used by fluid slots. Default: `16` | `"height": 32`         |

---
## Example

```json
{
  "recipe": {
    "input": [
      {
        "type": "item",
        "key": "item_in",
        "x": 1,
        "y": 19
      }
    ],
    "output": [
      {
        "type": "fluid",
        "key": "fluid_out",
        "x": 73,
        "y": 19,
        "height": 32,
        "capacity": 4000
      }
    ]
  }
}
```
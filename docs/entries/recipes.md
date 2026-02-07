# Recipes

The structure of custom recipes is similar to the normal recipe structure, meaning it requires a `"type"` value, 
which is the unique id set in the category.

To set the ingredients and outputs of the recipe the set `key` in category JSON is used to set which item/fluid each slot holds.

Items use `"item"` to set the item and optionally `"count"` to specify the amount of items, if left out the amount defaults to 1.
Fluids use `"fluid"` to set the fluid and same as item use `"amount"` to set the amount or 1mB if left out.

Both input item and fluids can use `"tag"` as their input, instead of `"item"` or `"fluid"`.

If a `key` is not set or spelled wrong in the recipe, it will not show in jei. 

---
## Example

Example with `"example_category"` as the type, `"item_in"` as a specified item input and `"fluid_out"` as a fluid output.

```json
{
  "type": "example:example_category",
  "item_in": {
    "tag": "minecraft:logs",
    "count": 4
  },
  "fluid_out": {
    "fluid": "minecraft:water",
    "amount": 2000
  }
}
```

---
## Overriding render components

Overring render component works the same as setting item/fluid inputs/outputs.

In this example the text component with the `"key": "useful_text"` is overridden.

```json
{
  "useful_text": {
    "text": "Useful Text",
    "x": 10,
    "y": 10
  }
}
```
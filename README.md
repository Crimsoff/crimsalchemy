Crim's Alchemy is a data driven minecraft Java Forge 1.20.1 mod that adds modular potion brewing to the game.

modrinth page: https://modrinth.com/mod/crims-alchemy  
curseforge page: https://www.curseforge.com/minecraft/mc-mods/crims-alchemy  

## Data formats
### data/crimsalchemy/tags/blocks/heats_cauldron.json
Blocks in this tag will heat the alchemical cauldron above them.
### data/crimsalchemy/tags/items/fills_heated_cauldron.json
Items in this category will melt and fill a heated alchemical cauldron.
### data/crimsalchemy/alchemy_recipes/
#### Potion
This recipe type adds an effect to the cauldron.  
If the effect already exists, it adds another level but decreases the duration by a lot.
```
{
"type": "potion",
"input": "namespace:item_id",
"effect": "namespace:potion_id",
"amplifier": "0",
"duration": "4800",
"progress_required": 20,
"capacity_requirement": 1
}
```
#### Amplifier
This recipe increases the level of an effect but cuts the duration in half.
```
{
"type": "amplifier",
"input": "namespace:item_id",
"amplifier": 1,
"apply_to_all": false,
"progress_required": 1,
"capacity_requirement": 1
}
```
#### Capacity
This recipe increases the capacity of a cauldron.  
Applying to all only applies to previous effects.
```
{
"type": "capacity",
"input": "namespace:item_id",
"capacity": 1,
"progress_required": 1,
"capacity_requirement": 0
}
```
#### Duration
This recipe multiplies the duration of an effect  
Applying to all only applies to previous effects.
```
{
"type": "duration",
"input": "namespace:item_id",
"multiplier": 2.0,
"apply_to_all": false,
"progress_required": 1,
"capacity_requirement": 1
}
```
#### Dye
This recipe will change the color of the final potion. Must be last to not be overwritten.
```
{
"type": "dye",
"input": "namespace:item_id",
"color": "FFFFFF",
"progress_required": 5
}
```
#### Change Item
This recipe will change the final item. Used to change potions into splash or lingering potiosn.
```
{
"type": "change",
"input": "namespace:item_id",
"output": "namespace:item_id",
"progress_required": 1
}
```

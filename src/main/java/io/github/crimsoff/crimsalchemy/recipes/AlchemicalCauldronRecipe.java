package io.github.crimsoff.crimsalchemy.recipes;

import io.github.crimsoff.crimsalchemy.blockentities.AlchemicalCauldronBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface AlchemicalCauldronRecipe {
    boolean applyToCauldron(AlchemicalCauldronBlockEntity cauldron);
    int getProgressRequirement();
    int capacityRequirement();
    boolean isAllowed(AlchemicalCauldronBlockEntity cauldron);
    String getType();
    Item getInput();
    ItemStack exampleOutput();
}

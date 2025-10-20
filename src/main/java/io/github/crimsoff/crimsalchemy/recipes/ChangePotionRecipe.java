package io.github.crimsoff.crimsalchemy.recipes;

import io.github.crimsoff.crimsalchemy.blockentities.AlchemicalCauldronBlockEntity;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ChangePotionRecipe implements AlchemicalCauldronRecipe {
    private final Item input;
    private final Item output;
    private final int progress_required;
    private final int capacity_requirement;

    public ChangePotionRecipe(Item input, Item output, int progressRequired, int capacityRequirement) {
        this.input = input;
        this.output = output;
        progress_required = progressRequired;
        capacity_requirement = capacityRequirement;
    }


    @Override
    public boolean applyToCauldron(AlchemicalCauldronBlockEntity cauldron) {
        cauldron.output = output;
        cauldron.capacity -= capacity_requirement;
        return true;
    }

    @Override
    public int getProgressRequirement() {
        return progress_required;
    }

    @Override
    public int capacityRequirement() {
        return capacity_requirement;
    }

    @Override
    public boolean isAllowed(AlchemicalCauldronBlockEntity cauldron) {
        return true;
    }

    @Override
    public String getType() {
        return "change";
    }

    @Override
    public Item getInput() {
        return input;
    }

    @Override
    public ItemStack exampleOutput() {
        ItemStack item = new ItemStack(output, 1);
        item.setHoverName(Component.translatable("item.crimsalchemy.potion").withStyle(style -> style.withItalic(false)));
        return item;
    }
}

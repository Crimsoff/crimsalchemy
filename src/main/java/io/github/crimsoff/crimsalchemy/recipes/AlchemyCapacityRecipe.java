package io.github.crimsoff.crimsalchemy.recipes;

import com.mojang.blaze3d.shaders.Effect;
import io.github.crimsoff.crimsalchemy.blockentities.AlchemicalCauldronBlockEntity;
import io.github.crimsoff.crimsalchemy.blocks.AlchemicalCauldronBlock;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;

public class AlchemyCapacityRecipe implements AlchemicalCauldronRecipe {
    private final Item input;
    private final int capacity;
    private final int progress_required;
    private final int capacity_requirement;

    public AlchemyCapacityRecipe(Item input, int capacity, int progressRequired, int capacityRequirement) {
        this.input = input;
        this.capacity = capacity;
        progress_required = progressRequired;
        capacity_requirement = capacityRequirement;
    }


    @Override
    public boolean applyToCauldron(AlchemicalCauldronBlockEntity cauldron) {
        if (cauldron.hasIngredient(input)) {
            return false;
        }
        cauldron.capacity += capacity - capacity_requirement;
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
        return !cauldron.hasIngredient(input);
    }

    @Override
    public String getType() {
        return "capacity";
    }

    @Override
    public Item getInput() {
        return input;
    }

    @Override
    public ItemStack exampleOutput() {
        ItemStack item = new ItemStack(CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get(), 1);
        item.setHoverName(Component.translatable("recipe.output.capacity").append(""+capacity).withStyle(style -> style.withItalic(false)));
        return item;
    }

}

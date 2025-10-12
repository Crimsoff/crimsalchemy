package io.github.crimsoff.crimsalchemy.recipes;

import io.github.crimsoff.crimsalchemy.blockentities.AlchemicalCauldronBlockEntity;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class AlchemyDyeRecipe implements AlchemicalCauldronRecipe {
    private final Item input;
    private final int color;
    private final int progress_required;

    public AlchemyDyeRecipe(Item input, int color, int progressRequired) {
        this.input = input;
        this.color = color;
        progress_required = progressRequired;
    }

    @Override
    public boolean applyToCauldron(AlchemicalCauldronBlockEntity cauldron) {
        cauldron.color = 0xFF000000 | color;
        return true;
    }

    @Override
    public int getProgressRequirement() {
        return progress_required;
    }

    @Override
    public int capacityRequirement() {
        return 0;
    }

    @Override
    public boolean isAllowed(AlchemicalCauldronBlockEntity cauldron) {
        return cauldron.color != (0xFF000000 | color);
    }

    @Override
    public String getType() {
        return "dye";
    }

    @Override
    public Item getInput() {
        return input;
    }

    @Override
    public ItemStack exampleOutput() {
        ItemStack item = new ItemStack(CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get(), 1);
        item.setHoverName(Component.translatable("recipe.output.color").withStyle(style -> style.withItalic(false).withColor(color)));
        return item;
    }
}

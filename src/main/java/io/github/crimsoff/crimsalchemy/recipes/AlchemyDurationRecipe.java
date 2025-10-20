package io.github.crimsoff.crimsalchemy.recipes;

import io.github.crimsoff.crimsalchemy.blockentities.AlchemicalCauldronBlockEntity;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class AlchemyDurationRecipe implements AlchemicalCauldronRecipe {
    private final Item input;
    private final float multiplier;
    private final int progress_required;
    private final boolean apply_to_all;
    private final int capacity_requirement;

    public AlchemyDurationRecipe(Item input, float multiplier, int progressRequired, boolean applyToAll, int capacityRequirement) {
        this.input = input;
        this.multiplier = multiplier;
        progress_required = progressRequired;
        apply_to_all = applyToAll;
        capacity_requirement = capacityRequirement;
    }


    @Override
    public boolean applyToCauldron(AlchemicalCauldronBlockEntity cauldron) {
        if (cauldron.effects.isEmpty()) {
            return false;
        }
        if (apply_to_all) {
            for (int i = 0; i < cauldron.effects.size(); ++i) {
             MobEffectInstance effect = cauldron.effects.get(i);
             MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), (int) (effect.getDuration() * multiplier), effect.getAmplifier());
             cauldron.effects.set(i, newEffect);
            }
        } else {
            int lastIndex = cauldron.effects.size() - 1;
            MobEffectInstance effect = cauldron.effects.get(lastIndex);
            MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), (int) (effect.getDuration() * multiplier), effect.getAmplifier());
            cauldron.effects.set(lastIndex, newEffect);
        }
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
        return "duration";
    }

    @Override
    public Item getInput() {
        return input;
    }

    @Override
    public ItemStack exampleOutput() {
        ItemStack item = new ItemStack(CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get(), 1);
        if (apply_to_all) {
            item.setHoverName(Component.translatable("recipe.output.duration_all", multiplier).withStyle(style -> style.withItalic(false)));
        } else {
            item.setHoverName(Component.translatable("recipe.output.duration", multiplier).withStyle(style -> style.withItalic(false)));
        }
        return item;
    }
}

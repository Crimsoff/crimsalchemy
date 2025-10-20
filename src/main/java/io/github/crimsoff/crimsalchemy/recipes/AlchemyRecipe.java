package io.github.crimsoff.crimsalchemy.recipes;

import io.github.crimsoff.crimsalchemy.blockentities.AlchemicalCauldronBlockEntity;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class AlchemyRecipe implements AlchemicalCauldronRecipe {
    private final Item input;
    private final MobEffect effect;
    private final int effectDuration;
    private final int progress_required;
    private final int capacity_requirement;
    private final int amplifier;

    public AlchemyRecipe(Item input, ResourceLocation effect, int effectDuration, int amplifier, int progressRequired, int capacityRequirement) {
        this.input = input;
        this.effect = ForgeRegistries.MOB_EFFECTS.getValue(effect);
        this.effectDuration = effectDuration;
        this.amplifier = amplifier;
        progress_required = progressRequired;
        capacity_requirement = capacityRequirement;
    }

    @Override
    public boolean applyToCauldron(AlchemicalCauldronBlockEntity cauldron) {
        if (cauldron.effects.isEmpty()) {
            cauldron.effects.add(new MobEffectInstance(effect, effectDuration, amplifier));
            cauldron.capacity -= capacity_requirement;
            return true;
        } else {
            for (int i = 0; i < cauldron.effects.size(); ++i) {
                MobEffectInstance effectInstance = cauldron.effects.get(i);
                if (effectInstance.getEffect() == effect) {
                    MobEffectInstance newEffect = new MobEffectInstance(effectInstance.getEffect(), (effectInstance.getDuration() + (effectDuration) / (effectInstance.getDuration() + effectDuration + 1)) / 4, effectInstance.getAmplifier() + (amplifier + 1));
                    cauldron.effects.set(i, newEffect);
                    cauldron.capacity -= capacity_requirement;
                    return true;
                }
            }
            cauldron.effects.add(new MobEffectInstance(effect, effectDuration, amplifier));
            cauldron.capacity -= capacity_requirement;
            return true;
        }
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
        return "potion";
    }

    @Override
    public Item getInput() {
        return input;
    }

    public ItemStack exampleOutput() {
        ItemStack item = new ItemStack(Items.POTION, 1);
        ArrayList<MobEffectInstance> list = new ArrayList<>();
        list.add(new MobEffectInstance(effect, effectDuration, amplifier));
        PotionUtils.setCustomEffects(item, list);
        item.setHoverName(Component.translatable("item.output.potion").withStyle(style -> style.withItalic(false)));
        item.getOrCreateTag().putInt("CustomPotionColor", effect.getColor());
        return item;
    }
}

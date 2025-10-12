package io.github.crimsoff.crimsalchemy.items;

import io.github.crimsoff.crimsalchemy.blocks.AlchemicalCauldronBlock;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AnointingPasteItem extends Item {

    public AnointingPasteItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState block = level.getBlockState(pos);
        Player player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();

        if (block.is(Blocks.CAULDRON)) {
            if (!level.isClientSide) {
                level.setBlock(pos, CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get().defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS);
                ((ServerLevel) level).sendParticles(ParticleTypes.WITCH,pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 1, 1, 1, 0.1);
            }
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}

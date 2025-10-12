package io.github.crimsoff.crimsalchemy.blocks;

import io.github.crimsoff.crimsalchemy.blockentities.AlchemicalCauldronBlockEntity;
import io.github.crimsoff.crimsalchemy.blockentities.CrimsBlockEntityRegister;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public class AlchemicalCauldronBlock extends BaseEntityBlock implements LiquidBlockContainer, BucketPickup {
    public final VoxelShape shape = Stream.of(
            Block.box(2, 0, 2, 14, 1, 14),
            Block.box(1, 1, 1, 15, 2, 15),
            Block.box(2, 2, 1, 14, 16, 2),
            Block.box(1, 14, 0, 15, 16, 1),
            Block.box(1, 14, 15, 15, 16, 16),
            Block.box(1, 2, 15, 15, 13, 16),
            Block.box(1, 2, 0, 15, 13, 1),
            Block.box(2, 2, 14, 14, 16, 15),
            Block.box(1, 2, 1, 2, 16, 15),
            Block.box(15, 14, 1, 16, 16, 15),
            Block.box(0, 14, 1, 1, 16, 15),
            Block.box(15, 2, 1, 16, 13, 15),
            Block.box(0, 2, 1, 1, 13, 15),
            Block.box(14, 2, 1, 15, 16, 15),
            Block.box(13, 2, 13, 14, 16, 14),
            Block.box(13, 2, 2, 14, 16, 3),
            Block.box(2, 2, 13, 3, 16, 14),
            Block.box(2, 2, 2, 3, 16, 3)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected AlchemicalCauldronBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AlchemicalCauldronBlockEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof AlchemicalCauldronBlockEntity) {
                ((AlchemicalCauldronBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof AlchemicalCauldronBlockEntity)) {
            return InteractionResult.FAIL;
        }
        AlchemicalCauldronBlockEntity cauldron = ((AlchemicalCauldronBlockEntity) blockEntity);
        ItemStack item = pPlayer.getItemInHand(pHand);

        if (cauldron.getFluidAmount() >= 1000 && cauldron.hasNoItem() && item.getItem() == Items.GLASS_BOTTLE) {
            if (!pLevel.isClientSide) {
                ItemUtils.createFilledResult(item, pPlayer, cauldron.getFilledBottle());
                pLevel.playSound(null, pPos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof AlchemicalCauldronBlockEntity)) {
            return false;
        }
        AlchemicalCauldronBlockEntity cauldron = ((AlchemicalCauldronBlockEntity) blockEntity);
        return cauldron.canFill(pFluid);
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof AlchemicalCauldronBlockEntity)) {
            return false;
        }
        AlchemicalCauldronBlockEntity cauldron = ((AlchemicalCauldronBlockEntity) blockEntity);
        cauldron.fillTank(pFluidState.getType());
        return true;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof AlchemicalCauldronBlockEntity)) {
            return ItemStack.EMPTY;
        }
        AlchemicalCauldronBlockEntity cauldron = ((AlchemicalCauldronBlockEntity) blockEntity);

        if (cauldron.getFluidAmount() < 1000 || cauldron.hasIngredients()) {
            return ItemStack.EMPTY;
        }
        ItemStack filledItem = new ItemStack(cauldron.getFluidStack().getFluid().getBucket(), 1);
        cauldron.drainTank(1000);
        return filledItem;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, CrimsBlockEntityRegister.ALCHEMICAL_CAULDRON.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }
}

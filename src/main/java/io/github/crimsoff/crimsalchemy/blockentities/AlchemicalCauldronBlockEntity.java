package io.github.crimsoff.crimsalchemy.blockentities;

import io.github.crimsoff.crimsalchemy.recipes.AlchemicalCauldronRecipe;
import io.github.crimsoff.crimsalchemy.recipes.CrimsData;
import io.github.crimsoff.crimsalchemy.recipes.CrimsRecipeLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class AlchemicalCauldronBlockEntity extends BlockEntity {
    private static final TagKey<Block> HEATS_CAULDRON = BlockTags.create(ResourceLocation.fromNamespaceAndPath("crimsalchemy", "heats_cauldron"));
    private static final TagKey<Item> FILLS_HEATED_CAULDRON = ItemTags.create(ResourceLocation.fromNamespaceAndPath("crimsalchemy", "fills_heated_cauldron"));
    private int progress = 0;
    public int capacity = 1;
    private int max_progress = 0;
    private ListTag ingredients = new ListTag();

    public ArrayList<MobEffectInstance> effects = new ArrayList<>();
    public int color = 0xFF385DC6;

    private final ItemStackHandler itemStackHandler = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (!itemStackHandler.getStackInSlot(0).isEmpty()) {
                updateFromRecipe(itemStackHandler.getStackInSlot(0).getItem());
            }
            meltItem();
            fillBottle();
            setChanged();
            AlchemicalCauldronBlockEntity.this.sendUpdate();
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return isItemAllowed(stack);
        }
    };

    private void sendUpdate() {
        if (level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private boolean isItemAllowed(@NotNull ItemStack stack) {
        return (stack.getTags().anyMatch((tag) -> tag == FILLS_HEATED_CAULDRON) && (fluidTank.getCapacity() > fluidTank.getFluidAmount())) || (fluidTank.getFluidAmount() == fluidTank.getCapacity() && ((stack.getItem() == Items.GLASS_BOTTLE) || (itemStackHandler.getStackInSlot(0).isEmpty()) && (hasRecipe(stack) && (CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.get(stack.getItem()).capacityRequirement() <= capacity) && (CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.get(stack.getItem()).isAllowed(this)))));
    }

    private boolean hasRecipe(@NotNull ItemStack stack) {
        return (CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.containsKey(stack.getItem()));
    }

    Predicate<ItemEntity> allowedItemEntity = (entity) -> isItemAllowed(entity.getItem());
    Predicate<ItemEntity> allowedFillItemEntity = (entity) -> entity.getItem().getTags().anyMatch((tag) -> tag == FILLS_HEATED_CAULDRON);


    private final FluidTank fluidTank = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            fillBottle();
            setChanged();
            AlchemicalCauldronBlockEntity.this.sendUpdate();
            super.onContentsChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }
    };

    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemStackHandler);
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidTank);

    public boolean canFill(Fluid pFluid) {
        return (pFluid == Fluids.WATER) && (fluidTank.getSpace() > 0);
    }

    public void fillTank(Fluid pFluid) {
        FluidStack stack = new FluidStack(pFluid, fluidTank.getCapacity());
        fluidTank.fill(stack, IFluidHandler.FluidAction.EXECUTE);
    }

    public void drainTank(int amount) {
        fluidTank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
        if (fluidTank.getFluidAmount() == 0) {
            color = 0xFF385DC6;
        }
    }

    public int getFluidAmount() {
        return fluidTank.getFluidAmount();
    }

    public void emptyTank() {
        fluidTank.drain(fluidTank.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
        capacity = 1;
        color = 0xFF385DC6;
    }

    private void meltItem() {
        if (level != null && this.level.getBlockState(this.worldPosition.below()).getTags().anyMatch((tag) -> tag == HEATS_CAULDRON)) {
            if (itemStackHandler.getStackInSlot(0).getTags().anyMatch((tag) -> tag == FILLS_HEATED_CAULDRON)) {
                itemStackHandler.getStackInSlot(0).shrink(1);
                fillTank(Fluids.WATER);
                level.playSound(null, worldPosition, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS);
            }
        }

    }

    public FluidStack getFluidStack() {
        return this.fluidTank.getFluid();
    }

    public int getFluidCapacity() {
        return fluidTank.getCapacity();
    }

    public boolean isEmpty() {
        return fluidTank.isEmpty();
    }

    public boolean hasNoItem() {
        return itemStackHandler.getStackInSlot(0).isEmpty();
    }

    public boolean hasIngredients() {
        return !ingredients.isEmpty();
    }
    public boolean hasIngredient(Item ingredient) {
        return ingredients.contains(StringTag.valueOf(ForgeRegistries.ITEMS.getKey(ingredient).toString()));
    }
    public void addIngredient(Item ingredient) {
        ingredients.add(StringTag.valueOf(ForgeRegistries.ITEMS.getKey(ingredient).toString()));
    }

    private void fillBottle() {
        if (fluidTank.getFluidAmount() == fluidTank.getCapacity() && itemStackHandler.getStackInSlot(0).getItem() == Items.GLASS_BOTTLE) {
            itemStackHandler.setStackInSlot(0, getFilledBottle());
        }
    }

    public ItemStack getFilledBottle() {
        ItemStack filledBottle = new ItemStack(Items.POTION, 1);

        filledBottle.setHoverName(Component.translatable("item.crimsalchemy.potion").withStyle(style -> style.withItalic(false)));
        PotionUtils.setCustomEffects(filledBottle, effects);
        filledBottle.getOrCreateTag().putInt("CustomPotionColor", color);
        emptyTank();
        effects.clear();
        capacity = 1;
        ingredients.clear();
        return filledBottle;
    }


    public AlchemicalCauldronBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(CrimsBlockEntityRegister.ALCHEMICAL_CAULDRON.get(), pPos, pBlockState);
    }





    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (itemStackHandler.getStackInSlot(0).isEmpty() || !hasRecipe(itemStackHandler.getStackInSlot(0))) {
                return lazyItemHandler.cast();
            }
        }
        else if (cap == ForgeCapabilities.FLUID_HANDLER && !hasIngredients()) {
            return lazyFluidHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("progress",progress);
        pTag.putInt("color", color);
        pTag.putInt("capacity", capacity);
        pTag.put("inventory",itemStackHandler.serializeNBT());
        pTag.put("ingredients", ingredients);
        fluidTank.writeToNBT(pTag);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        progress = pTag.getInt("progress");
        capacity = pTag.getInt("capacity");
        fluidTank.readFromNBT(pTag);
        itemStackHandler.deserializeNBT(pTag.getCompound("inventory"));
        ingredients = pTag.getList("ingredients", StringTag.TAG_STRING);
        applyRecipes();
        color = pTag.getInt("color");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            inventory.setItem(i, itemStackHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void updateFromRecipe(Item input) {
        if (CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.containsKey(input)) {
            max_progress = CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.get(input).getProgressRequirement();
        }
    }
    private void applyRecipe() {
        if (CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.containsKey(itemStackHandler.getStackInSlot(0).getItem())) {
            AlchemicalCauldronRecipe recipe = CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.get(itemStackHandler.getStackInSlot(0).getItem());
            recipe.applyToCauldron(this);
            if (!recipe.getType().equals("dye")) {
                color = 0xFF000000 | PotionUtils.getColor(effects);
                addIngredient(itemStackHandler.getStackInSlot(0).getItem());
            }
        }
    }

    private void applyRecipes() {
        effects.clear();
        for (int i = 0; i < ingredients.size(); ++i) {
            String resource = ingredients.getString(i);
            Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(resource));
            if (CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.containsKey(item)) {
                CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.get(item).applyToCauldron(this);
            }
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(pLevel.isClientSide()) {
            return;
        }
        BlockState blockBelow = pLevel.getBlockState(pPos.below());
        if (level != null && blockBelow.getTags().anyMatch((tag) -> tag == HEATS_CAULDRON)) {
            Vec3 center = pPos.getCenter();
            AABB area = new AABB(center.subtract(0.375, 0.375, 0.375), center.add(0.375, 0.5, 0.375));


            if (fluidTank.getFluidAmount() < fluidTank.getCapacity()) {
                // Pull in items here
                List<ItemEntity> itemsAtCauldron = level.getEntitiesOfClass(ItemEntity.class, area, allowedFillItemEntity);
                if (!itemsAtCauldron.isEmpty()) {
                    ItemEntity firstItemEntity = itemsAtCauldron.get(0);
                    if (!firstItemEntity.getItem().isEmpty()) {
                        firstItemEntity.getItem().shrink(1);
                        level.playSound(null, pPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS);
                        fillTank(Fluids.WATER);
                    }
                }

            } else {
                // Particles
                ((ServerLevel) level).sendParticles(ParticleTypes.BUBBLE_POP,pPos.getX() + 0.5, pPos.getY() + 0.9375, pPos.getZ() + 0.5, 1, 0.15, 0, 0.15, 0);
                if (ingredients.size() > 64) {
                    // Just don't allow more than 64 ingredients
                    return;
                }

                // Pull in items here
                if (itemStackHandler.getStackInSlot(0).isEmpty()) {

                    List<ItemEntity> itemsAtCauldron = level.getEntitiesOfClass(ItemEntity.class, area, allowedItemEntity);
                    if (!itemsAtCauldron.isEmpty()) {
                        ItemEntity firstItemEntity = itemsAtCauldron.get(0);
                        if (!firstItemEntity.getItem().isEmpty()) {
                            ItemStack newItem = new ItemStack(firstItemEntity.getItem().getItem(), 1);
                            firstItemEntity.getItem().shrink(1);
                            itemStackHandler.insertItem(0, newItem, false);
                            level.playSound(null, pPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);
                            updateFromRecipe(itemStackHandler.getStackInSlot(0).getItem());
                        }
                    }
                }
                // Process items
                if (itemStackHandler.getStackInSlot(0).isEmpty()) {
                    progress = 0;
                } else {
                    progress += 1;
                    ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE,pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, 3, 0.2, 0.15, 0.2, 0);
                    System.out.println(max_progress);
                    if (progress >= max_progress) {
                        applyRecipe();
                        itemStackHandler.extractItem(0, 1, false);
                        progress = 0;
                        level.playSound(null, pPos, SoundEvents.GENERIC_SWIM, SoundSource.BLOCKS, 1.0f, 0.5f);
                    }
                }
            }
        }
        if (itemStackHandler.getStackInSlot(0).getItem() == Items.POTION && !pLevel.isClientSide) {
            ItemStack item = itemStackHandler.extractItem(0, 1, false);
            ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, item);
            itemEntity.setDeltaMovement(pLevel.random.nextFloat() * 0.2 - 0.1, 0.25, pLevel.random.nextFloat() * 0.2 - 0.1);
            pLevel.addFreshEntity(itemEntity);
        }

    }
}

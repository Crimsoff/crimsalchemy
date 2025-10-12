package io.github.crimsoff.crimsalchemy.blockentities;

import io.github.crimsoff.crimsalchemy.CrimsAlchemy;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CrimsBlockEntityRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CrimsAlchemy.MOD_ID);
    public static final RegistryObject<BlockEntityType<AlchemicalCauldronBlockEntity>> ALCHEMICAL_CAULDRON = BLOCK_ENTITIES.register("alchemical_cauldron_block_entity", () -> BlockEntityType.Builder.of(AlchemicalCauldronBlockEntity::new, CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get()).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}

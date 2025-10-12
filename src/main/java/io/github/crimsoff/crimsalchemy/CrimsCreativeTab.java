package io.github.crimsoff.crimsalchemy;

import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import io.github.crimsoff.crimsalchemy.items.CrimsItemRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CrimsCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrimsAlchemy.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CRIMS_ALCHEMY_TAB = CREATIVE_MODE_TABS.register("crimsalchemy_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get()))
                    .title(Component.translatable("creativetab.crimsalchemy_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(CrimsItemRegister.ANOINTING_PASTE.get());
                        pOutput.accept(CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get());

                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

package io.github.crimsoff.crimsalchemy.items;

import io.github.crimsoff.crimsalchemy.CrimsAlchemy;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class CrimsItemRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrimsAlchemy.MOD_ID);
    public static final RegistryObject<Item> ANOINTING_PASTE = ITEMS.register("anointing_paste", () -> new AnointingPasteItem(new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}

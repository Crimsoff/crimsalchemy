package io.github.crimsoff.crimsalchemy.recipes;

import io.github.crimsoff.crimsalchemy.CrimsAlchemy;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrimsAlchemy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrimsData {

    public static final CrimsRecipeLoader RECIPE_LOADER = new CrimsRecipeLoader();

    @SubscribeEvent
    public static void onReloadListeners(AddReloadListenerEvent event) {
        event.addListener(RECIPE_LOADER);
    }

}

package net.kyrptonaught.quickshulker.gui;

import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.gui.screen.BundleItemScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = QuickShulker.MOD_ID, value = Dist.CLIENT)
public class MenuScreens {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event){
        event.register(MenuTypes.BUNDLE_ITEM.get(), BundleItemScreen::new);
    }
}

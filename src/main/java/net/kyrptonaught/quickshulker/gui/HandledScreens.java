package net.kyrptonaught.quickshulker.gui;

import net.kyrptonaught.quickshulker.gui.screen.BundleItemScreen;

public class HandledScreens {

    public static void registerHandledScreens(){
        net.minecraft.client.gui.screen.ingame.HandledScreens.register(ScreenHandlers.BUNDLE_ITEM, BundleItemScreen::new);
    }
}

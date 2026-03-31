package net.kyrptonaught.quickshulker.gui;

import net.kyrptonaught.quickshulker.gui.screen.BundleItemScreen;

public class MenuScreens {

    public static void registerMenuScreens(){
        net.minecraft.client.gui.screens.MenuScreens.register(MenuTypes.BUNDLE_ITEM, BundleItemScreen::new);
    }
}

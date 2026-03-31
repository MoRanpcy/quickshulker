package net.kyrptonaught.quickshulker.gui;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.gui.screen.BundleItemMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

public class MenuTypes {
    public static final MenuType<BundleItemMenu> BUNDLE_ITEM = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "bundle_item"),
            new MenuType<>(BundleItemMenu::new, FeatureFlagSet.of())
    );

    public static void registerMenuTypes(){

    }
}

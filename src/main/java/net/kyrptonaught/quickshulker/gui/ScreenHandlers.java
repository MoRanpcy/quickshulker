package net.kyrptonaught.quickshulker.gui;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.gui.screen.BundleItemScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlers {
    public static final ScreenHandlerType<BundleItemScreenHandler> BUNDLE_ITEM = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(QuickShulkerMod.MOD_ID, "bundle_item"),
            new ScreenHandlerType<>(BundleItemScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );

    public static void registerScreenHandlers(){

    }
}

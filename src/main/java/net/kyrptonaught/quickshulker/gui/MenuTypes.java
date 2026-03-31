package net.kyrptonaught.quickshulker.gui;

import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.gui.screen.BundleItemMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(
            BuiltInRegistries.MENU,
            QuickShulker.MOD_ID
    );

    public static final Supplier<MenuType<BundleItemMenu>> BUNDLE_ITEM = MENU_TYPES.register("bundle_item", () -> new MenuType<>(BundleItemMenu::new, FeatureFlags.DEFAULT_FLAGS));
}

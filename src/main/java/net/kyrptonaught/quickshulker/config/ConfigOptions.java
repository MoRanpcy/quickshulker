package net.kyrptonaught.quickshulker.config;

import blue.endless.jankson.Comment;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;
import net.kyrptonaught.kyrptconfig.keybinding.CustomKeyBinding;
import net.kyrptonaught.quickshulker.QuickShulkerMod;

public class ConfigOptions implements AbstractConfigFile {
    public static String defualtKeybind = "key.keyboard.k";
    @Comment("Activation key")
    public CustomKeyBinding keybinding = CustomKeyBinding.configDefault(QuickShulkerMod.MOD_ID, "key.keyboard.k");
    @Comment("Right Clicking with shulker in hand opens it")
    public boolean rightClickToOpen = true;
    @Comment("Hitting the keybind with shulker in hand opens it")
    public boolean keybind = true;
    @Comment("Hitting the keybind while hovering over shulker in inv opens it")
    public boolean keybingInInv = true;
    @Comment("Right Clicking a shulker in your inv opens it")
    public boolean rightClickInv = true;

    @Comment("Right Clicking the opened shulker in your inv closes it")
    public boolean rightClickClose = false;
    @Comment("Right Clicking a shulker with an item inserts it")
    public boolean supportsBundlingInsert = true;
    @Comment("Right Clicking an item with a shulker inserts it")
    public boolean supportsBundlingPickup = true;
    @Comment("Right Clicking an empty slot with a shulker extracts an item")
    public boolean supportsBundlingExtract = true;

    @Comment("Enable opening Shulker Boxes")
    public boolean quickShulkerBox = true;
    @Comment("Enable opening Crafting Tables")
    public boolean quickCraftingTables = true;
    @Comment("Enable opening Stonecutter")
    public boolean quickStonecutter = true;
    @Comment("Enable opening EnderChest")
    public boolean quickEChest = true;

}
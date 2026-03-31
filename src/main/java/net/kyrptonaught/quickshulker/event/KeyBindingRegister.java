package net.kyrptonaught.quickshulker.event;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class KeyBindingRegister {
    public static final KeyMapping.Category MAIN = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "main"));

    public static void register(){
        KeyMappingHelper.registerKeyMapping(new DisplayOnlyKeyBind(
                "key.quickshulker.config.openSettingGui",
                MAIN,
                QuickShulkerMod.getConfig().openSettingGui,
                setKey -> QuickShulkerMod.config.save()
        ));
        KeyMappingHelper.registerKeyMapping(new DisplayOnlyKeyBind(
                "key.quickshulker.config.keybinding",
                MAIN,
                QuickShulkerMod.getConfig().keybinding,
                setKey -> QuickShulkerMod.config.save()
        ));
    }
}

package net.kyrptonaught.quickshulker.event;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;

public class KeyBindingRegister {
    public static final KeyBinding.Category MAIN = KeyBinding.Category.create(Identifier.of(QuickShulkerMod.MOD_ID, "main"));

    public static void register(){
        KeyBindingHelper.registerKeyBinding(new DisplayOnlyKeyBind(
                "key.quickshulker.config.openSettingGui",
                MAIN,
                QuickShulkerMod.getConfig().openSettingGui,
                setKey -> QuickShulkerMod.config.save()
        ));
        KeyBindingHelper.registerKeyBinding(new DisplayOnlyKeyBind(
                "key.quickshulker.config.keybinding",
                MAIN,
                QuickShulkerMod.getConfig().keybinding,
                setKey -> QuickShulkerMod.config.save()
        ));
    }
}

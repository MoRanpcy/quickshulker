package net.kyrptonaught.quickshulker.event;

import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.kyrptonaught.quickshulker.QuickShulker;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;

@EventBusSubscriber(modid = QuickShulker.MOD_ID, value = Dist.CLIENT)
public class KeyBindingRegister {

    public static final KeyMapping.Category MAIN = new KeyMapping.Category(Identifier.fromNamespaceAndPath(QuickShulker.MOD_ID, "main"));

    private static final Lazy<KeyMapping> OPEN_SETTING_GUI = Lazy.of(() ->
            new DisplayOnlyKeyBind(
                    "key.quickshulker.config.openSettingGui",
                    MAIN,
                    QuickShulker.getConfig().openSettingGui,
                    setKey -> QuickShulker.config.save()
            ));
    private static final Lazy<KeyMapping> KEYBINDING = Lazy.of(() ->
            new DisplayOnlyKeyBind(
                    "key.quickshulker.config.keybinding",
                    MAIN,
                    QuickShulker.getConfig().keybinding,
                    setKey -> QuickShulker.config.save()
            ));

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event){
        event.registerCategory(MAIN);
        event.register(OPEN_SETTING_GUI.get());
        event.register(KEYBINDING.get());
    }
}

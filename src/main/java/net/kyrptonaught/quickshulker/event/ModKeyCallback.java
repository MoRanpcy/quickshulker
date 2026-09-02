package net.kyrptonaught.quickshulker.event;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.config.ModConfigMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;

public class ModKeyCallback {

    public static void onKeyPressed(ClientLevel clientWorld){
        Minecraft mc = Minecraft.getInstance();
        if(mc.screen == null){
            handleKeyPressed(mc);
        }
    }

    private static void handleKeyPressed(Minecraft mc){
        ConfigOptions configs = QuickShulkerMod.getConfig();
        if (configs.openSettingGui.isKeybindPressed()){
            mc.setScreen(ModConfigMenu.getModConfigMenu(mc.screen));
        }
        if (configs.keybinding.isKeybindPressed()) {
            Player player = mc.player;
            if (QuickShulkerMod.getConfig().keybind && player != null && !player.isSpectator()) {
                if (player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty())
                    ClientUtil.CheckAndSend(player.getOffhandItem(), 45);
                else
                    ClientUtil.CheckAndSend(player.getMainHandItem(), 36 + player.getInventory().getSelectedSlot());
            }
        }
    }
}

package net.kyrptonaught.quickshulker.event;

import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.config.ModConfigMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;

public class ModKeyCallback {

    public static void onKeyPressed(ClientLevel clientWorld){
        Minecraft mc = Minecraft.getInstance();
        ConfigOptions configs = QuickShulker.getConfig();
        if(configs.openSettingGui.wasPressed()){
            mc.gui.setScreen(ModConfigMenu.getModConfigMenu(mc.gui.screen()));
        }
        if (configs.keybinding.isKeybindPressed()) {
            Player player = mc.player;
            if (mc.gui.screen() == null && QuickShulker.getConfig().keybind && player != null && !player.isSpectator()) {
                if (player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty())
                    ClientUtil.CheckAndSend(player.getOffhandItem(), 45);
                else
                    ClientUtil.CheckAndSend(player.getMainHandItem(), 36 + player.getInventory().getSelectedSlot());
            }
        }
    }
}

package net.kyrptonaught.quickshulker.event;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.config.ModConfigMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

public class ModKeyCallback {

    public static void onKeyPressed(ClientWorld clientWorld){
        MinecraftClient mc = MinecraftClient.getInstance();
        ConfigOptions configs = QuickShulkerMod.getConfig();
        if(configs.openSettingGui.wasPressed()){
            mc.setScreen(ModConfigMenu.getModConfigMenu(mc.currentScreen));
        }
        if (configs.keybinding.isKeybindPressed()) {
            PlayerEntity player = mc.player;
            if (mc.currentScreen == null && QuickShulkerMod.getConfig().keybind && player != null && !player.isSpectator()) {
                if (player.getMainHandStack().isEmpty() && !player.getOffHandStack().isEmpty())
                    ClientUtil.CheckAndSend(player.getOffHandStack(), 45);
                else
                    ClientUtil.CheckAndSend(player.getMainHandStack(), 36 + player.getInventory().selectedSlot);
            }
        }
    }
}

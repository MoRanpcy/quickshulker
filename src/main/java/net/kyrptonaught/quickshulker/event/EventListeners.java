package net.kyrptonaught.quickshulker.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.util.EnderChestSyncHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;

public class EventListeners {

    public static void registerEventListeners() {

        // Log in
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            EnderChestSyncHandler.syncEnderChestContent(handler.player);
        });

        // Respawn
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            EnderChestSyncHandler.syncEnderChestContent(newPlayer);
        });

        // Change dimension
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> {
            EnderChestSyncHandler.syncEnderChestContent(player);
        });

    }

    // Open container
    public static void containerOpenedListener(ServerPlayer player) {
        if(player.containerMenu instanceof ChestMenu chestMenu && chestMenu.getContainer() == player.getEnderChestInventory()) {
            EnderChestSyncHandler.syncOnContainerOpened(player, chestMenu);
        }
    }

    public static void containerClosedListener(ServerPlayer player){
        if(QuickShulkerMod.getConfig().playSound) {
            int selectedSlot = ((ItemInventoryContainer) player.containerMenu).getUsedSlotInPlayerInv();
            if (selectedSlot != -1) Util.playCloseSound(player, selectedSlot);
        }
    }
}

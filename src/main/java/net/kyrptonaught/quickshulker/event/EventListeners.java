package net.kyrptonaught.quickshulker.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.util.EnderChestSyncHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

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
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            EnderChestSyncHandler.syncEnderChestContent(player);
        });

    }

    // Open inventory
    public static void containerOpenedListener(ServerPlayerEntity player) {
        if (player.currentScreenHandler instanceof GenericContainerScreenHandler chestMenu && chestMenu.getInventory() == player.getEnderChestInventory()) {
            EnderChestSyncHandler.syncOnContainerOpened(player, chestMenu);
        }
    }

    public static void containerClosedListener(ServerPlayerEntity player){
        if(QuickShulkerMod.getConfig().playSound) {
            int selectedSlot = ((ItemInventoryContainer) player.currentScreenHandler).getUsedSlotInPlayerInv();
            if (selectedSlot != -1) Util.playCloseSound(player, selectedSlot);
        }
    }
}

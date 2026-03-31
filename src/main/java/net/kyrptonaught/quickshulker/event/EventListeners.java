package net.kyrptonaught.quickshulker.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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

    // Open inventory
    public static void containerOpenedListener(ServerPlayer player, ChestMenu chestMenu) {
        EnderChestSyncHandler.syncOnContainerOpened(player, chestMenu);
    }

}

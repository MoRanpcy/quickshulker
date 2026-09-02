package net.kyrptonaught.quickshulker.event;

import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.api.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.util.EnderChestSyncHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = QuickShulker.MOD_ID)
public class EventListeners {

    // Log in
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        EnderChestSyncHandler.syncEnderChestContent((ServerPlayer) event.getEntity());
    }

    // Respawn
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        EnderChestSyncHandler.syncEnderChestContent((ServerPlayer) event.getEntity());
    }

    // Change dimension
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        EnderChestSyncHandler.syncEnderChestContent((ServerPlayer) event.getEntity());
    }

    // Open container
    public static void containerOpenedListener(ServerPlayer player) {
        if(player.containerMenu instanceof ChestMenu chestMenu && chestMenu.getContainer() == player.getEnderChestInventory()) {
            EnderChestSyncHandler.syncOnContainerOpened(player, chestMenu);
        }
    }

//    // Open container (It is also effective, but I want the code to be the same as Fabric's)
//    @SubscribeEvent
//    public static void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
//        if(event.getContainer() instanceof ChestMenu chestMenu && chestMenu.getContainer() == event.getEntity().getEnderChestInventory()){
//            EnderChestSyncHandler.syncOnContainerOpened((ServerPlayer) event.getEntity(), chestMenu);
//        }
//    }

    public static void containerClosedListener(ServerPlayer player){
        if(QuickShulker.getConfig().playSound) {
            int selectedSlot = ((ItemInventoryContainer) player.containerMenu).getUsedSlotInPlayerInv();
            if (selectedSlot != -1) Util.playCloseSound(player, selectedSlot);
        }
    }
}

package net.kyrptonaught.quickshulker.util;

import net.kyrptonaught.quickshulker.network.EnderChestS2CSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class EnderChestSyncHandler {

    public static void syncOnContainerOpened(ServerPlayer player, ChestMenu chestMenu){
        syncEnderChestContent(player);
        chestMenu.addSlotListener(new ContainerListener(){
            @Override
            public void slotChanged(AbstractContainerMenu handler, int slotId, ItemStack stack) {
                Slot slot = handler.getSlot(slotId);
                if(slot.container == player.getEnderChestInventory()){
                    EnderChestS2CSyncPacket.S2CEChestSlotPacket.send(player, slot.getContainerSlot(), stack);
                }
            }
            @Override
            public void dataChanged(AbstractContainerMenu handler, int property, int value) {

            }
        });
    }

    public static void syncEnderChestContent(ServerPlayer player) {
        EnderChestS2CSyncPacket.S2CEChestContentPacket.send(player, player.getEnderChestInventory().getItems());
    }

    public static void setEnderChestContent(Player player, List<ItemStack> itemStacks){
        SimpleContainer enderChestInventory = player.getEnderChestInventory();
        // safeguard against mods only changing ender chest size on one side
        int size = Math.min(itemStacks.size(), enderChestInventory.getContainerSize());
        for(int i = 0; i < size; i++){
            enderChestInventory.setItem(i, itemStacks.get(i));
        }
    }

}

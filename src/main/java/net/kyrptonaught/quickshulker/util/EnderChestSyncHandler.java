package net.kyrptonaught.quickshulker.util;

import net.kyrptonaught.quickshulker.network.EnderChestS2CSyncPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class EnderChestSyncHandler {

    public static void syncOnContainerOpened(ServerPlayerEntity player, GenericContainerScreenHandler chestMenu){
        syncEnderChestContent(player);
        chestMenu.addListener(new ScreenHandlerListener(){
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                Slot slot = handler.getSlot(slotId);
                if(slot.inventory == player.getEnderChestInventory()){
                    EnderChestS2CSyncPacket.S2CEChestSlotPacket.send(player, slot.getIndex(), stack);
                }
            }
            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

            }
        });
    }

    public static void syncEnderChestContent(ServerPlayerEntity player) {
        EnderChestS2CSyncPacket.S2CEChestContentPacket.send(player, player.getEnderChestInventory().getHeldStacks());
    }

    public static void setEnderChestContent(PlayerEntity player, List<ItemStack> itemStacks){
        SimpleInventory enderChestInventory = player.getEnderChestInventory();
        // safeguard against mods only changing ender chest size on one side
        int size = Math.min(itemStacks.size(), enderChestInventory.size());
        for(int i = 0; i < size; i++){
            enderChestInventory.setStack(i, itemStacks.get(i));
        }
    }

}

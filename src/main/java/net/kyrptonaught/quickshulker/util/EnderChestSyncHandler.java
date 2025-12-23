package net.kyrptonaught.quickshulker.util;

import net.kyrptonaught.quickshulker.mixin.SimpleInventoryAccessor;
import net.kyrptonaught.quickshulker.network.EnderChestS2CSyncPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

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
        resizeInventory(enderChestInventory, itemStacks.size());
        for(int i = 0; i < enderChestInventory.size(); i++){
            ItemStack stack = i < itemStacks.size() ? itemStacks.get(i) : ItemStack.EMPTY;
            enderChestInventory.setStack(i, stack);
        }
    }

    public static void ensureInventoryCapacity(SimpleInventory inventory, int minSize) {
        if (minSize > inventory.size()) {
            resizeInventory(inventory, minSize);
        }
    }

    private static void resizeInventory(SimpleInventory inventory, int newSize) {
        if (newSize < 0 || inventory.size() == newSize) return;
        SimpleInventoryAccessor accessor = (SimpleInventoryAccessor) inventory;
        DefaultedList<ItemStack> oldStacks = accessor.getHeldStacks();
        DefaultedList<ItemStack> newStacks = DefaultedList.ofSize(newSize, ItemStack.EMPTY);
        int copyAmount = Math.min(oldStacks.size(), newSize);
        for (int i = 0; i < copyAmount; i++) {
            newStacks.set(i, oldStacks.get(i));
        }
        accessor.setHeldStacks(newStacks);
        accessor.setSize(newSize);
    }

}

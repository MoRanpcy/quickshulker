package net.kyrptonaught.quickshulker.api;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;

public class Util {

    public static void openItem(PlayerEntity player, int invSlot) {
        if (invSlot < 0) {
            System.out.println("[QuickShulker]: unknown slot opened");
            //return; //not preventing the crash might make it easier to debug a fix.
        }
        openItem(player, invSlot, player.currentScreenHandler.slots.get(invSlot).getIndex());
    }

    public static void openItem(PlayerEntity player, int invSlot, int playerInvIndex) {
        if (QuickShulkerMod.getConfig().rightClickClose && playerInvIndex == ((ItemInventoryContainer) player.currentScreenHandler).getUsedSlotInPlayerInv()) {
            ((ServerPlayerEntity) player).closeHandledScreen();
            OpenInventoryPacket.send((ServerPlayerEntity) player);
            return;
        }
        ItemStack stack = player.getInventory().getStack(playerInvIndex);
        //stack.removeSubNbt(QuickShulkerMod.MOD_ID);
        QuickShulkerData qsData = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsData != null) {
            qsData.openConsumer.accept(player, stack);
            ((ItemInventoryContainer) player.currentScreenHandler).setUsedSlot(playerInvIndex);
            player.currentScreenHandler.addListener(forceCloseScreenIfNotPresent(player, playerInvIndex, stack.copy()));
        }
    }

    public static Boolean isOpenableItem(ItemStack stack) {
        QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsdata == null) return false;
        return qsdata.ignoreSingleStackCheck || stack.getCount() <= 1;
    }

    public static Inventory getQuickItemInventory(PlayerEntity player, ItemStack stack) {
        QuickShulkerData qsData = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsData != null) {
            if (qsData.supportsBundleing)
                return qsData.getInventory(player, stack);
        }
        return null;
    }

    public static boolean canOpenInHand(ItemStack stack) {
        QuickShulkerData qsData = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsData != null) {
            return qsData.canOpenInHand;
        }
        return false;
    }

    public static boolean areItemsEqualExactly(ItemStack stack, ItemStack otherStack) {
//        return ItemStack.areItemsEqual(stack, otherStack) && ItemStack.areEqual(stack, otherStack) && stack.getCount() == otherStack.getCount();
        return ItemStack.areEqual(stack, otherStack);
    }

    public static boolean areItemsEqualOnlyType(ItemStack stack, ItemStack otherStack){
        return stack == otherStack || ItemStack.areItemsEqual(stack, otherStack);
    }

    public static ScreenHandlerListener forceCloseScreenIfNotPresent(PlayerEntity player, int slotID, ItemStack stack) {
        return new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                isValid();
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
                isValid();
            }

            public void isValid() {
                ItemStack stackInSlot = player.getInventory().getStack(slotID);
                if (!areItemsEqualOnlyType(stack, stackInSlot)
                        || (!QuickOpenableRegistry.getQuickie(stack.getItem()).ignoreSingleStackCheck && stackInSlot.getCount() != 1)) {
                    ((ServerPlayerEntity) player).closeHandledScreen();
                }
            }
        };
    }
}
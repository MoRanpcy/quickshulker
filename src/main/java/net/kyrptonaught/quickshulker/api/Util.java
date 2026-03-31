package net.kyrptonaught.quickshulker.api;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class Util {

    public static void openItem(Player player, int invSlot) {
        if (invSlot < 0) {
            System.out.println("[QuickShulker]: unknown slot opened");
            //return; //not preventing the crash might make it easier to debug a fix.
        }
        openItem(player, invSlot, player.containerMenu.slots.get(invSlot).getContainerSlot());
    }

    public static void openItem(Player player, int invSlot, int playerInvIndex) {
        if (QuickShulkerMod.getConfig().rightClickClose && playerInvIndex == ((ItemInventoryContainer) player.containerMenu).getUsedSlotInPlayerInv()) {
            ((ServerPlayer) player).closeContainer();
            OpenInventoryPacket.send((ServerPlayer) player);
            return;
        }
        ItemStack stack = player.getInventory().getItem(playerInvIndex);
        //stack.removeSubNbt(QuickShulkerMod.MOD_ID);
        QuickShulkerData qsData = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsData != null) {
            qsData.openConsumer.accept(player, stack);
            ((ItemInventoryContainer) player.containerMenu).setUsedSlot(playerInvIndex);
            player.containerMenu.addSlotListener(forceCloseScreenIfNotPresent(player, playerInvIndex, stack.copy()));
        }
    }

    public static Boolean isOpenableItem(ItemStack stack) {
        QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsdata == null) return false;
        return qsdata.ignoreSingleStackCheck || stack.getCount() <= 1;
    }

    public static Container getQuickItemInventory(Player player, ItemStack stack) {
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
//        return ItemStack.isSameItem(stack1, stack2) && ItemStack.matches(stack1, stack2) && stack1.getCount() == stack2.getCount();
        return ItemStack.isSameItemSameComponents(stack, otherStack);
    }

    public static boolean areItemsEqualOnlyType(ItemStack stack, ItemStack otherStack){
        return stack == otherStack || ItemStack.isSameItem(stack, otherStack);
    }

    public static ContainerListener forceCloseScreenIfNotPresent(Player player, int slotID, ItemStack stack) {
        return new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu handler, int slotId, ItemStack stack) {
                isValid();
            }

            @Override
            public void dataChanged(AbstractContainerMenu handler, int property, int value) {
                isValid();
            }

            public void isValid() {
                ItemStack stackInSlot = player.getInventory().getItem(slotID);
                if (!areItemsEqualOnlyType(stack, stackInSlot)
                        || (!QuickOpenableRegistry.getQuickie(stack.getItem()).ignoreSingleStackCheck && stackInSlot.getCount() != 1)) {
                    ((ServerPlayer) player).closeContainer();
                }
            }
        };
    }
}
package net.kyrptonaught.quickshulker.util;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.QuickShulkerData;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class BundleHelper {
    public static boolean shouldAttemptBundle(PlayerEntity player, ClickType clickType, ItemStack hostStack, ItemStack insertStack, boolean enabledInConfig) {
        return (enabledInConfig && clickType == ClickType.RIGHT && Util.isOpenableItem(hostStack) && isAcceptedInsertItem(insertStack) && Util.getQuickItemInventory(player, hostStack) != null);
    }

    public static boolean shouldAttemptUnBundle(PlayerEntity player, ClickType clickType, ItemStack hostStack, ItemStack insertStack, boolean enabledInConfig) {
        Inventory stackInv = Util.getQuickItemInventory(player, hostStack);
        if(stackInv != null){
            return (enabledInConfig && clickType == ClickType.RIGHT && hostStack.getCount() == 1 && !stackInv.isEmpty() && insertStack.isEmpty());
        }
        return false;
    }

    public static boolean shouldAttemptTransfer(PlayerEntity player, ClickType clickType, ItemStack hostStack, ItemStack insertStack, boolean enabledInConfig){
        return enabledInConfig && clickType == ClickType.RIGHT && ShulkerUtils.isShulkerItem(hostStack) && hostStack.getCount() == 1 && Util.getQuickItemInventory(player, hostStack) != null && isAcceptedTransferItem(player, insertStack);
    }

    private static boolean isAcceptedInsertItem(ItemStack insertStack) {
        return !insertStack.isEmpty() && !ShulkerUtils.isShulkerItem(insertStack);
    }

    private static boolean isAcceptedTransferItem(PlayerEntity player, ItemStack insertStack) {
        return ShulkerUtils.isShulkerItem(insertStack) && insertStack.getCount() == 1 && Util.getQuickItemInventory(player, insertStack) != null;
    }

    public static void bundleItemIntoStack(PlayerEntity player, ItemStack hostStack, ItemStack insertStack, CallbackInfoReturnable<Boolean> cir) {
        if (bundleItem(player, hostStack, insertStack) != null && cir != null)
            cir.setReturnValue(true);
    }

    public static void bundleItemIntoStack(PlayerEntity player, ItemStack hostStack, ItemStack insertStack, Slot slot, CallbackInfoReturnable<Boolean> cir){
        if(bundleItem(player, hostStack, insertStack, slot) != null && cir != null){
            cir.setReturnValue(true);
        }
    }

    public static void unbundleStackIntoSlot(PlayerEntity player, ItemStack hostStack, Slot unbundleSlot, CallbackInfoReturnable<Boolean> cir) {
        ItemStack output = unbundleItem(player, hostStack, unbundleSlot);
        if (output != null) {
            unbundleSlot.setStack(output);
            cir.setReturnValue(true);
        }
    }

    public static void transferItemsToShulker(PlayerEntity player, ItemStack hostStack, ItemStack insertStack, CallbackInfoReturnable<Boolean> cir){
        SimpleInventory source = (SimpleInventory) Util.getQuickItemInventory(player, insertStack);
        SimpleInventory target = (SimpleInventory) Util.getQuickItemInventory(player, hostStack);
        if(source != null && target != null) {
            int temp = 0;
            for (int i = source.size() - 1; i >= 0; i--) {
                ItemStack stack = source.getStack(i);
                if (!stack.isEmpty() && target.canInsert(stack)) {
                    ItemStack output = target.addStack(stack);
                    source.setStack(i, output);
                    temp++;
                }
            }
            if(temp > 0 && cir != null){
                target.onClose(player);
                source.onClose(player);
                cir.setReturnValue(true);
            }
        }
    }

    public static ItemStack unbundleItem(PlayerEntity player, ItemStack hostStack, Slot unbundleSlot) {
        Inventory inv = Util.getQuickItemInventory(player, hostStack);
        ItemStack output = null;
        for (int i = inv.size() - 1; i >= 0; i--) {
            output = inv.getStack(i);
            if (!output.isEmpty() && unbundleSlot.canInsert(output)) {
                output = inv.removeStack(i);
                inv.onClose(player);
                return output;
            }
        }
        return null;
    }

//    private static ItemStack bundleItem(PlayerEntity player, ItemStack hostStack, ItemStack insertStack) {
//        Inventory bundlingInv = Util.getQuickItemInventory(player, hostStack);
//        QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(hostStack.getItem());
//        if (bundlingInv != null && qsdata.canBundleInsertItem(player, bundlingInv, hostStack, insertStack)) {
//            try (Transaction transaction = Transaction.openOuter()) {
//                long amount = InventoryStorage.of(bundlingInv, null).insert(ItemVariant.of(insertStack), insertStack.getCount(), transaction);
//                if (amount == 0) return null;
//                transaction.commit();
//                insertStack.decrement((int) amount);
//                bundlingInv.onClose(player);
//                return insertStack;
//            }
//        }
//        return null;
//    }

    private static ItemStack bundleItem(PlayerEntity player, ItemStack hostStack, ItemStack insertStack) {
        Inventory bundlingInv = Util.getQuickItemInventory(player, hostStack);
        int amount = insertIntoInv(bundlingInv, player, hostStack, insertStack);
        if(amount != 0){
            insertStack.decrement(amount);
            return insertStack;
        }
        return null;
    }

    private static ItemStack bundleItem(PlayerEntity player, ItemStack hostStack, ItemStack insertStack, Slot slot) {
        if(!slot.canTakeItems(player)) return null;
        Inventory bundlingInv = Util.getQuickItemInventory(player, hostStack);
        int amount = insertIntoInv(bundlingInv, player, hostStack, insertStack);
        if(amount != 0){
            insertStack = slot.takeStackRange(amount, insertStack.getCount(), player);
            return insertStack;
        }
        return null;
    }

    private static int insertIntoInv(Inventory bundlingInv, PlayerEntity player, ItemStack hostStack, ItemStack insertStack) {
        QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(hostStack.getItem());
        int amount = 0;
        if(bundlingInv != null && qsdata.canBundleInsertItem(player, bundlingInv, hostStack, insertStack)){
            try (Transaction transaction = Transaction.openOuter()) {
                amount = (int) InventoryStorage.of(bundlingInv, null).insert(ItemVariant.of(insertStack), insertStack.getCount(), transaction);
                transaction.commit();
                bundlingInv.onClose(player);
                return amount;
            }
        }
        return amount;
    }
}

package net.kyrptonaught.quickshulker;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.QuickShulkerData;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
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

    private static boolean isAcceptedInsertItem(ItemStack insertStack) {
        return !insertStack.isEmpty() && !ShulkerUtils.isShulkerItem(insertStack);
    }

    public static void bundleItemIntoStack(PlayerEntity player, ItemStack hostStack, ItemStack insertStack, CallbackInfoReturnable<Boolean> cir) {
        if (bundleItem(player, hostStack, insertStack) != null && cir != null)
            cir.setReturnValue(true);
    }

    public static void unbundleStackIntoSlot(PlayerEntity player, ItemStack hostStack, Slot unbundleSlot, CallbackInfoReturnable<Boolean> cir) {
        ItemStack output = unbundleItem(player, hostStack);
        if (output != null) {
            unbundleSlot.setStack(output);
            cir.setReturnValue(true);
        }
    }

    public static ItemStack unbundleItem(PlayerEntity player, ItemStack hostStack) {
        Inventory inv = Util.getQuickItemInventory(player, hostStack);
        for (int i = inv.size() - 1; i >= 0; i--) {
            if (!inv.getStack(i).isEmpty()) {
                ItemStack output = inv.removeStack(i);
                inv.onClose(player);
                return output;
            }
        }
        return null;
    }

    private static ItemStack bundleItem(PlayerEntity player, ItemStack hostStack, ItemStack insertStack) {
            Inventory bundlingInv = Util.getQuickItemInventory(player, hostStack);
            QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(hostStack.getItem());
            if (bundlingInv != null && qsdata.canBundleInsertItem(player, bundlingInv, hostStack, insertStack)) {
                try (Transaction transaction = Transaction.openOuter()) {
                    long amount = InventoryStorage.of(bundlingInv, null).insert(ItemVariant.of(insertStack), insertStack.getCount(), transaction);
                    if (amount == 0) return null;
                    transaction.commit();
                    insertStack.decrement((int) amount);
                    bundlingInv.onClose(player);
                    return insertStack;
                }
            }
        return null;
    }
}

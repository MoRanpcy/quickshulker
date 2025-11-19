package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.util.BundleHelper;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.network.QuickBundlePacket;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    public void QS$onClicked(ItemStack hostStack, ItemStack insertStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (BundleHelper.shouldAttemptBundle(player, clickType, hostStack, insertStack, QuickShulkerMod.getConfig().supportsBundlingInsert)) {
            if (ShulkerUtils.isShulkerItem(hostStack) || !player.getWorld().isClient) {
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, cir);
            } else if (slot.inventory instanceof PlayerInventory && ClientUtil.isCreativeScreen(player)) {//stupid creative menu shiz
                QuickBundlePacket.sendPacket(ClientUtil.getPlayerInvSlot(player.currentScreenHandler, slot), insertStack);
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, cir);
            }
        } else if (BundleHelper.shouldAttemptTransfer(player, clickType, hostStack, insertStack, QuickShulkerMod.getConfig().supportsBundlingTransfer)) {
            BundleHelper.transferItemsToShulker(player, hostStack, insertStack, cir);
        }
    }

    @Inject(method = "onStackClicked", at = @At("HEAD"), cancellable = true)
    public void QS$onStackClicked(ItemStack hostStack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack insertStack = slot.getStack();
        if (BundleHelper.shouldAttemptBundle(player, clickType, hostStack, insertStack, QuickShulkerMod.getConfig().supportsBundlingPickup)) {//bundle stack into held item
            if (ShulkerUtils.isShulkerItem(hostStack) || !player.getWorld().isClient) {
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, slot, cir);
            } else if (slot.inventory instanceof PlayerInventory && ClientUtil.isCreativeScreen(player)) { //stupid creative menu shiz
                QuickBundlePacket.BundleIntoHeld.sendPacket(insertStack, hostStack, ClientUtil.getPlayerInvSlot(player.currentScreenHandler, slot));
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, slot, cir);
                //QuickBundlePacket.sendCreativeSlotUpdate(insertStack, slot); // It doesn't seem to be doing anything
            }
        } else if (BundleHelper.shouldAttemptUnBundle(player, clickType, hostStack, insertStack, QuickShulkerMod.getConfig().supportsBundlingExtract)) {//unbundle held stack into slot
            if (ShulkerUtils.isShulkerItem(hostStack) || !player.getWorld().isClient) {
                BundleHelper.unbundleStackIntoSlot(player, hostStack, slot, cir);
            } else if (slot.inventory instanceof PlayerInventory && ClientUtil.isCreativeScreen(player)) { //stupid creative menu shiz
                QuickBundlePacket.UnbundlePacket.sendPacket(ClientUtil.getPlayerInvSlot(player.currentScreenHandler, slot), hostStack);
                BundleHelper.unbundleStackIntoSlot(player, hostStack, slot, cir);
            }
        }
    }
}
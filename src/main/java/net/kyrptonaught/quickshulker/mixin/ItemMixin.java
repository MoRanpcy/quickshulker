package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.gui.screen.BundleContainer;
import net.kyrptonaught.quickshulker.util.BundleHelper;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.network.QuickBundlePacket;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    public void QS$onClicked(ItemStack hostStack, ItemStack insertStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (BundleHelper.shouldAttemptBundle(player, clickType, hostStack, insertStack, QuickShulker.getConfig().supportsBundlingInsert)) {
            if (ShulkerUtils.isShulkerItem(hostStack) || !player.level().isClientSide()) {
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, cir);
            } else if (slot.container instanceof Inventory && ClientUtil.isCreativeScreen(player)) {//stupid creative menu shiz
                QuickBundlePacket.sendPacket(ClientUtil.getPlayerInvSlot(player.containerMenu, slot), insertStack);
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, cir);
            }
        } else if (BundleHelper.shouldAttemptTransfer(player, clickType, hostStack, insertStack, QuickShulker.getConfig().supportsBundlingTransfer)) {
            BundleHelper.transferItemsToShulker(player, hostStack, insertStack, cir);
        }
    }

    @Inject(method = "overrideStackedOnOther", at = @At("HEAD"), cancellable = true)
    public void QS$onStackClicked(ItemStack hostStack, Slot slot, ClickAction clickType, Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack insertStack = slot.getItem();
        if (BundleHelper.shouldAttemptBundle(player, clickType, hostStack, insertStack, QuickShulker.getConfig().supportsBundlingPickup)) {//bundle stack into held item
            if (!ShulkerUtils.isShulkerItem(hostStack) && player.level().isClientSide() && slot.container instanceof Inventory && ClientUtil.isCreativeScreen(player)) { //stupid creative menu shiz
                QuickBundlePacket.BundleIntoHeld.sendPacket(insertStack, hostStack, ClientUtil.getPlayerInvSlot(player.containerMenu, slot));
                //QuickBundlePacket.sendCreativeSlotUpdate(insertStack, slot); // It doesn't seem to be doing anything
            }
            if(slot.container instanceof BundleContainer){
                cir.setReturnValue(false);
            }else {
                BundleHelper.bundleItemIntoStack(player, hostStack, insertStack, slot, cir);
            }
        } else if (BundleHelper.shouldAttemptUnBundle(player, clickType, hostStack, insertStack, QuickShulker.getConfig().supportsBundlingExtract)) {//unbundle held stack into slot
            if (!ShulkerUtils.isShulkerItem(hostStack) && player.level().isClientSide() && slot.container instanceof Inventory && ClientUtil.isCreativeScreen(player)) { //stupid creative menu shiz
                QuickBundlePacket.UnbundlePacket.sendPacket(ClientUtil.getPlayerInvSlot(player.containerMenu, slot), hostStack);
            }
            if(slot.container instanceof BundleContainer){
                cir.setReturnValue(false);
            }else {
                BundleHelper.unbundleStackIntoSlot(player, hostStack, slot, cir);
            }
        }
    }
}
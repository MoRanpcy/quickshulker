package net.kyrptonaught.quickshulker.api;

import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class QuickShulkerData {
    public BiConsumer<Player, ItemStack> openConsumer;
    BiFunction<Player, ItemStack, Container> bundleInvGetter;
    CanBundleInsertItemFunction canBundleInsertItem;

    public boolean supportsBundleing = false;
    public boolean ignoreSingleStackCheck = false;
    public boolean canOpenInHand = true;

    public QuickShulkerData() {

    }

    public QuickShulkerData(BiConsumer<Player, ItemStack> openConsumer, Boolean supportsBundleing) {
        this.openConsumer = openConsumer;
        this.supportsBundleing = supportsBundleing;
    }

    public QuickShulkerData(BiConsumer<Player, ItemStack> openConsumer, Boolean supportsBundleing, Boolean ignoreSingleStackCheck) {
        this.openConsumer = openConsumer;
        this.supportsBundleing = supportsBundleing;
        this.ignoreSingleStackCheck = ignoreSingleStackCheck;
    }

    public Container getInventory(Player player, ItemStack stack) {
        if (bundleInvGetter != null) return bundleInvGetter.apply(player, stack);
        return ShulkerUtils.getInventoryFromShulker(stack);
    }

    public boolean canBundleInsertItem(Player player, Container inventory, ItemStack hostStack, ItemStack insertStack) {
        if (canBundleInsertItem != null)
            return canBundleInsertItem.canBundleInsertItem(player, inventory, hostStack, insertStack);
        return !ShulkerUtils.isShulkerItem(insertStack);
    }

    public static class QuickEnderData extends QuickShulkerData {
        public QuickEnderData() {
            super();
            canBundleInsertItem = CanBundleInsertItemFunction.ALWAYS;
        }

        public QuickEnderData(BiConsumer<Player, ItemStack> openConsumer, Boolean supportsBundleing) {
            super(openConsumer, supportsBundleing);
            canBundleInsertItem = CanBundleInsertItemFunction.ALWAYS;
        }

        public QuickEnderData(BiConsumer<Player, ItemStack> openConsumer, Boolean supportsBundleing, Boolean ignoreSingleStackCheck) {
            super(openConsumer, supportsBundleing, ignoreSingleStackCheck);
            canBundleInsertItem = CanBundleInsertItemFunction.ALWAYS;
        }

        public Container getInventory(Player player, ItemStack stack) {
            if (!QuickShulker.getConfig().quickEChest)
                return null;
            return player.getEnderChestInventory();
        }
    }
}
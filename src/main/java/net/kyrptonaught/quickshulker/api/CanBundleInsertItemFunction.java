package net.kyrptonaught.quickshulker.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface CanBundleInsertItemFunction {
    CanBundleInsertItemFunction ALWAYS = (player, inventory, hostStack, insertStack) -> true;

    boolean canBundleInsertItem(PlayerEntity player, Inventory inventory, ItemStack hostStack, ItemStack insertStack);

}

package net.kyrptonaught.quickshulker.api;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface CanBundleInsertItemFunction {
    CanBundleInsertItemFunction ALWAYS = (player, inventory, hostStack, insertStack) -> true;

    boolean canBundleInsertItem(Player player, Container inventory, ItemStack hostStack, ItemStack insertStack);

}

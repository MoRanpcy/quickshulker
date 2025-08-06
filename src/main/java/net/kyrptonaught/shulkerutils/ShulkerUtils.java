package net.kyrptonaught.shulkerutils;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class ShulkerUtils {
    public static boolean isShulkerItem(ItemStack item) {
        return Block.getBlockFromItem(item.getItem()) instanceof ShulkerBoxBlock;
    }

    public static boolean shulkerContainsAny(Inventory shulkerInv, ItemStack stack) {
        for (int i = 0; i < shulkerInv.size(); i++) {
            if (shulkerInv.getStack(i).getItem().equals(stack.getItem()))
                return true;
        }
        return false;
    }

    public static ItemStack insertIntoShulker(SimpleInventory shulkerInv, ItemStack stack, PlayerEntity player) {
        if (isShulkerItem(stack) || !shulkerInv.canInsert(stack))
            return stack;
        ItemStack output = shulkerInv.addStack(stack);
        shulkerInv.onClose(player);
        return output;
    }

    public static ItemStackInventory getInventoryFromShulker(ItemStack stack) {
        Block shulker = ((BlockItem) stack.getItem()).getBlock();
        if (shulker instanceof UpgradableShulker) {
            return new ItemStackInventory(stack, ((UpgradableShulker) shulker).getInventorySize());
        }
        return new ItemStackInventory(stack, 27);
    }
}

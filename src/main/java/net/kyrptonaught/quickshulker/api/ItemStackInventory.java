package net.kyrptonaught.quickshulker.api;

import net.minecraft.item.ItemStack;

//Dummy to restore compat to mods already using this
public class ItemStackInventory extends net.kyrptonaught.shulkerutils.ItemStackInventory {
    public ItemStackInventory(ItemStack stack, int SIZE) {
        super(stack, SIZE);
    }
}

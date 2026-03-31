package net.kyrptonaught.shulkerutils;

import java.util.Objects;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;


public class ItemStackInventory extends SimpleContainer {
    protected final ItemStack itemStack;
    protected final int SIZE;

    public ItemStackInventory(ItemStack stack, int SIZE) {
        super(getStacks(stack, SIZE).toArray(new ItemStack[SIZE]));
        itemStack = stack;
        this.SIZE = SIZE;
    }

    public static NonNullList<ItemStack> getStacks(ItemStack usedStack, int SIZE) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        Objects.requireNonNull(usedStack.getComponents().get(DataComponents.CONTAINER)).copyInto(itemStacks);
        return itemStacks;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < getContainerSize(); i++) {
            itemStacks.set(i, getItem(i));
        }
        itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(itemStacks));
    }

    @Override
    public void stopOpen(ContainerUser user) {
        if (itemStack.getCount() > 1) {
            int count = itemStack.getCount();
            itemStack.setCount(1);
            ((Player) user).addItem(new ItemStack(itemStack.getItem(), count - 1));
        }
        setChanged();
    }
}
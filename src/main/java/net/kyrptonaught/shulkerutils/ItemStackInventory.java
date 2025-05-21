package net.kyrptonaught.shulkerutils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.Objects;


public class ItemStackInventory extends SimpleInventory {
    protected final ItemStack itemStack;
    protected final int SIZE;

    public ItemStackInventory(ItemStack stack, int SIZE) {
        super(getStacks(stack, SIZE).toArray(new ItemStack[SIZE]));
        itemStack = stack;
        this.SIZE = SIZE;
    }

    public static DefaultedList<ItemStack> getStacks(ItemStack usedStack, int SIZE) {
        DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
        Objects.requireNonNull(usedStack.getComponents().get(DataComponentTypes.CONTAINER)).copyTo(itemStacks);
        return itemStacks;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < size(); i++) {
            itemStacks.set(i, getStack(i));
        }
        itemStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(itemStacks));
    }

    @Override
    public void onClose(PlayerEntity playerEntity) {
        if (itemStack.getCount() > 1) {
            int count = itemStack.getCount();
            itemStack.setCount(1);
            playerEntity.giveItemStack(new ItemStack(itemStack.getItem(), count - 1));
        }
        markDirty();
    }
}
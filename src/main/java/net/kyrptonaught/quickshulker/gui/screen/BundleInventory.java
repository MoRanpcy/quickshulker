package net.kyrptonaught.quickshulker.gui.screen;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.ContainerUser;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.List;

public class BundleInventory extends SimpleInventory {
    protected final ItemStack itemStack;
    protected final int size;

    public BundleInventory(int size){
        super(size);
        this.itemStack = ItemStack.EMPTY;
        this.size = size;
    }

    public BundleInventory(ItemStack itemStack, int size) {
        super(getStacks(itemStack, size).toArray(new ItemStack[size]));
        this.itemStack = itemStack;
        this.size = size;
    }

    public static DefaultedList<ItemStack> getStacks(ItemStack usedStack, int SIZE) {
        DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
            BundleContentsComponent bundleContentsComponent = usedStack.get(DataComponentTypes.BUNDLE_CONTENTS);
            List<ItemStack> stacks = bundleContentsComponent.stream().toList();
            for(int i = 0; i < stacks.size(); i++){
                itemStacks.set(i, stacks.get(i));
            }
        return itemStacks;
    }

    public BundleContentsComponent getBundleContents(){
        return this.itemStack.get(DataComponentTypes.BUNDLE_CONTENTS);
    }

    public int countCanInsertToBundle(ItemStack insertStack){
        BundleContentsComponent contents = this.getBundleContents();
        if(contents != null){
            BundleContentsComponent.Builder builder =new BundleContentsComponent.Builder(contents);
            return builder.add(insertStack.copy());
        }
        return 0;
    }

    public static boolean isFull(ItemStack itemStack){
        BundleContentsComponent content = itemStack.get(DataComponentTypes.BUNDLE_CONTENTS);
        return content == null || content.getOccupancy().compareTo(Fraction.ONE) >= 0;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        this.heldStacks.stream().filter(stack -> !stack.isEmpty()).forEach(itemStacks::add);
        BundleContentsComponent bundleContentsComponent = new BundleContentsComponent(itemStacks);
        itemStack.set(DataComponentTypes.BUNDLE_CONTENTS, bundleContentsComponent);
    }

    @Override
    public void onClose(ContainerUser playerEntity) {
        markDirty();
    }
}

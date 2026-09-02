package net.kyrptonaught.quickshulker.gui.screen;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.ContainerUser;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.Arrays;
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
        super(getStacksArray(itemStack, size));
        this.itemStack = itemStack;
        this.size = size;
    }

    public static DefaultedList<ItemStack> getStacksList(ItemStack usedStack, int size) {
        DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
            BundleContentsComponent bundleContentsComponent = usedStack.get(DataComponentTypes.BUNDLE_CONTENTS);
            List<ItemStack> stacks = bundleContentsComponent.stream().toList();
            for(int i = 0; i < stacks.size(); i++){
                itemStacks.set(i, stacks.get(i));
            }
        return itemStacks;
    }

    public static ItemStack[] getStacksArray(ItemStack usedStack, int size){
        ItemStack[] itemStacks = new ItemStack[size];
        Arrays.fill(itemStacks, ItemStack.EMPTY);
        BundleContentsComponent bundleContents = usedStack.get(DataComponentTypes.BUNDLE_CONTENTS);
        ItemStack[] itemStacks1 = bundleContents.stream().toArray(ItemStack[]::new);
        System.arraycopy(itemStacks1, 0, itemStacks, 0, itemStacks1.length);
        return itemStacks;
    }

    //Only Server
    public BundleContentsComponent getBundleContents(){
        return this.itemStack.get(DataComponentTypes.BUNDLE_CONTENTS);
    }

    // Server and Client
    public BundleContentsComponent getBundleContentsByStacks(){
        return new BundleContentsComponent(this.heldStacks.stream().filter(itemStack -> !itemStack.isEmpty()).toList());
    }

    public int countCanInsertToBundle(ItemStack insertStack){
        BundleContentsComponent contents = this.getBundleContentsByStacks();
        if(contents != null){
            BundleContentsComponent.Builder builder =new BundleContentsComponent.Builder(contents);
            return builder.add(insertStack.copy());
        }
        return 0;
    }

    public static boolean isFull(ItemStack bundleItem){
        BundleContentsComponent content = bundleItem.get(DataComponentTypes.BUNDLE_CONTENTS);
        return content == null || content.getOccupancy().compareTo(Fraction.ONE) >= 0;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        this.heldStacks.stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy).forEach(itemStacks::add);
        BundleContentsComponent bundleContentsComponent = new BundleContentsComponent(itemStacks);
        itemStack.set(DataComponentTypes.BUNDLE_CONTENTS, bundleContentsComponent);
    }

    @Override
    public void onClose(ContainerUser playerEntity) {
        markDirty();
    }
}

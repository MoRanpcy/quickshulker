package net.kyrptonaught.quickshulker.gui.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;

import java.util.Arrays;
import java.util.List;

public class BundleContainer extends SimpleContainer {
    protected final ItemStack itemStack;
    protected final int size;

    public BundleContainer(int size){
        super(size);
        this.itemStack = ItemStack.EMPTY;
        this.size = size;
    }

    public BundleContainer(ItemStack itemStack, int size) {
        super(getItemsArray(itemStack, size));
        this.itemStack = itemStack;
        this.size = size;
    }

    public static NonNullList<ItemStack> getItemsList(ItemStack usedStack, int size) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(size, ItemStack.EMPTY);
            BundleContents bundleContents = usedStack.get(DataComponents.BUNDLE_CONTENTS);
            List<ItemStack> stacks = bundleContents.itemCopyStream().toList();
            for(int i = 0; i < stacks.size(); i++){
                itemStacks.set(i, stacks.get(i));
            }
        return itemStacks;
    }

    public static ItemStack[] getItemsArray(ItemStack usedStack, int size){
        ItemStack[] itemStacks = new ItemStack[size];
        Arrays.fill(itemStacks, ItemStack.EMPTY);
        BundleContents bundleContents = usedStack.get(DataComponents.BUNDLE_CONTENTS);
        ItemStack[] itemStacks1 = bundleContents.itemCopyStream().toArray(ItemStack[]::new);
        System.arraycopy(itemStacks1, 0, itemStacks, 0, itemStacks1.length);
        return itemStacks;
    }

    // Only Server
    public BundleContents getBundleContents(){
        return this.itemStack.get(DataComponents.BUNDLE_CONTENTS);
    }

    // Server and Client
    public BundleContents getBundleContentsByItems(){
        return new BundleContents(getItems().stream().filter(itemStack -> !itemStack.isEmpty()).map(ItemStackTemplate::fromNonEmptyStack).toList());
    }

    public int countCanInsertToBundle(ItemStack insertStack){
        BundleContents contents = this.getBundleContentsByItems();
        if(contents != null){
            BundleContents.Mutable builder =new BundleContents.Mutable(contents);
            return builder.tryInsert(insertStack.copy());
        }
        return 0;
    }

    public static boolean isFull(ItemStack bundleItem){
        BundleContents content = bundleItem.get(DataComponents.BUNDLE_CONTENTS);
        return content == null || content.weight().getOrThrow().compareTo(Fraction.ONE) >= 0;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        ImmutableList.Builder<ItemStackTemplate> builder = ImmutableList.builder();
        this.getItems().stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy).forEach(stack -> builder.add(ItemStackTemplate.fromNonEmptyStack(stack)));
        BundleContents bundleContents = new BundleContents(builder.build());
        itemStack.set(DataComponents.BUNDLE_CONTENTS, bundleContents);
    }

    @Override
    public void stopOpen(ContainerUser playerEntity) {
        setChanged();
    }
}

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
        super(getItems(itemStack, size).toArray(new ItemStack[size]));
        this.itemStack = itemStack;
        this.size = size;
    }

    public static NonNullList<ItemStack> getItems(ItemStack usedStack, int SIZE) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
            BundleContents bundleContents = usedStack.get(DataComponents.BUNDLE_CONTENTS);
            List<ItemStack> stacks = bundleContents.itemCopyStream().toList();
            for(int i = 0; i < stacks.size(); i++){
                itemStacks.set(i, stacks.get(i));
            }
        return itemStacks;
    }

    public BundleContents getBundleContents(){
        return this.itemStack.get(DataComponents.BUNDLE_CONTENTS);
    }

    public int countCanInsertToBundle(ItemStack insertStack){
        BundleContents contents = this.getBundleContents();
        if(contents != null){
            BundleContents.Mutable builder =new BundleContents.Mutable(contents);
            return builder.tryInsert(insertStack.copy());
        }
        return 0;
    }

    public static boolean isFull(ItemStack itemStack){
        BundleContents content = itemStack.get(DataComponents.BUNDLE_CONTENTS);
        return content == null || content.weight().getOrThrow().compareTo(Fraction.ONE) >= 0;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        ImmutableList.Builder<ItemStackTemplate> builder = ImmutableList.builder();
        this.items.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> builder.add(ItemStackTemplate.fromNonEmptyStack(stack)));
        BundleContents bundleContents = new BundleContents(builder.build());
        itemStack.set(DataComponents.BUNDLE_CONTENTS, bundleContents);
    }

    @Override
    public void stopOpen(ContainerUser playerEntity) {
        setChanged();
    }
}

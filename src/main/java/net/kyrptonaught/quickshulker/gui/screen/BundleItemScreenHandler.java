package net.kyrptonaught.quickshulker.gui.screen;

import net.kyrptonaught.quickshulker.gui.ScreenHandlers;
import net.kyrptonaught.quickshulker.mixin.SlotAccessor;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;

public class BundleItemScreenHandler extends ScreenHandler {
    private static final int INVENTORY_SIZE = 64;
    private final Inventory inventory;

    private static final int VISIBLE_ROWS = 5;
    private static final int ROWS = 8;
    private static final int CLOS = 8;

    public BundleItemScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new BundleInventory(INVENTORY_SIZE));
    }

    public BundleItemScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlers.BUNDLE_ITEM, syncId);
        checkSize(inventory, INVENTORY_SIZE);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        for(int i = 0; i < this.inventory.size(); i++){
            int col = i % CLOS;
            int row = (i - col) / CLOS;
            if(row < VISIBLE_ROWS){
                this.addSlot(new BundleSlot(inventory, i, 8 + col * 18, 18 + row * 18));
            }else{
                this.addSlot(new BundleSlot(inventory, i, Integer.MIN_VALUE, Integer.MIN_VALUE));
            }
        }

        this.addPlayerSlots(playerInventory, 8, 18 + VISIBLE_ROWS * 18 + 13);
    }

    public void scrollItems(float position){
        int r = this.getRow(position);

        for(int i = 0; i < this.inventory.size(); i++){
            int col = i % CLOS;
            int row = (i - col) / CLOS;

            Slot slot = this.getSlot(i);
            if(row < r || row >= r + VISIBLE_ROWS){
                ((SlotAccessor) slot).setX(Integer.MIN_VALUE);
                ((SlotAccessor) slot).setY(Integer.MIN_VALUE);
            }else{
                ((SlotAccessor) slot).setX(8 + col * 18);
                ((SlotAccessor) slot).setY(18 + (row - r) * 18);
            }
        }
    }

    public int countCanInsertToBundle(ItemStack insertStack){
        return ((BundleInventory) this.inventory).countCanInsertToBundle(insertStack);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if(slot < this.inventory.size()){
                if(!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true, slot2)){
                    return ItemStack.EMPTY;
                }
            }else{
                int count = countCanInsertToBundle(itemStack2);
                itemStack2.decrement(count);
                itemStack.setCount(count);
                if(!this.insertItem(itemStack, 0, this.inventory.size(), false)){
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    // Copied from ScreenHandler.insertItem(), and modified a little bit.
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast, Slot originSlot){
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
                Slot slot = this.slots.get(i);
                ItemStack itemStack = slot.getStack();
                if (!itemStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(stack, itemStack)) {
                    int j = itemStack.getCount() + stack.getCount();
                    int k = slot.getMaxItemCount(itemStack);
                    if (j <= k) {
                        stack = ItemStack.EMPTY;
                        originSlot.setStack(ItemStack.EMPTY);
                        itemStack.setCount(j);
                        slot.markDirty();
                        bl = true;
                    } else if (itemStack.getCount() < k) {
                        stack.decrement(k - itemStack.getCount());
                        itemStack.setCount(k);
                        slot.markDirty();
                        bl = true;
                    }
                }

                if (fromLast) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (fromLast) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (fromLast ? i >= startIndex : i < endIndex) {
                Slot slotx = this.slots.get(i);
                ItemStack itemStackx = slotx.getStack();
                if (itemStackx.isEmpty() && slotx.canInsert(stack)) {
                    int j = slotx.getMaxItemCount(stack);
                    if(stack.getCount() <= j){
                        slotx.setStack(stack);
                        originSlot.setStack(ItemStack.EMPTY);
                    }else {
                        slotx.setStack(stack.split(j));
                    }
                    slotx.markDirty();
                    bl = true;
                    break;
                }

                if (fromLast) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        return bl;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    protected int getOverflowRows(){
        return MathHelper.ceilDiv(this.inventory.size(), CLOS) - VISIBLE_ROWS;
    }

    protected float getScrollPosition(int row) {
        return MathHelper.clamp((float)row / this.getOverflowRows(), 0.0F, 1.0F);
    }

    protected float getScrollPosition(float current, double amount){
        return MathHelper.clamp(current - (float) (amount / this.getOverflowRows()), 0.0F, 1.0F);
    }

    protected int getRow(float scroll){
        return Math.max((int) (this.getOverflowRows() * scroll + 0.5F), 0);
    }

    public Inventory getInventory(){
        return this.inventory;
    }

    class BundleSlot extends Slot {
        public BundleSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            if(!BundleContentsComponent.canBeBundled(stack)){
                return false;
            }else{
                BundleContentsComponent contents = ((BundleInventory) BundleItemScreenHandler.this.inventory).getBundleContents();
                if(contents == null) return false;
                BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(contents);
                ItemStack stackInSlot = this.getStack();
                if(stackInSlot.isEmpty() || ItemStack.areItemsAndComponentsEqual(stackInSlot, stack)){
                    return builder.getMaxAllowed(stack) > 0;
                }else{
                    Fraction stack1 = calculateFraction(stackInSlot);
                    Fraction stack2 = calculateFraction(stack);
                    return Fraction.ONE.subtract(builder.getOccupancy()).add(stack1).compareTo(stack2) >= 0;
                }
            }
        }

        private static Fraction calculateFraction(ItemStack stack){
            return BundleContentsComponent.getOccupancy(stack).multiplyBy(Fraction.getFraction(stack.getCount(), 1));
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            int canInsert = BundleItemScreenHandler.this.countCanInsertToBundle(stack);
            return super.insertStack(stack, Math.min(count, canInsert));
        }
    }
}

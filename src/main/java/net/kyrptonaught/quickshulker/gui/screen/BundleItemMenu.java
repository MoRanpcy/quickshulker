package net.kyrptonaught.quickshulker.gui.screen;

import net.kyrptonaught.quickshulker.gui.MenuTypes;
import net.kyrptonaught.quickshulker.mixin.SlotAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BundleItemMenu extends AbstractContainerMenu {
    private static final int CONTAINER_SIZE = 64;
    private final Container container;

    private static final int VISIBLE_ROWS = 5;
    private static final int ROWS = 8;
    private static final int CLOS = 8;

    public BundleItemMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new BundleContainer(CONTAINER_SIZE));
    }

    public BundleItemMenu(int syncId, Inventory playerInventory, Container container) {
        super(MenuTypes.BUNDLE_ITEM, syncId);
        checkContainerSize(container, CONTAINER_SIZE);
        this.container = container;
        container.startOpen(playerInventory.player);

        for(int i = 0; i < this.container.getContainerSize(); i++){
            int col = i % CLOS;
            int row = (i - col) / CLOS;
            if(row < VISIBLE_ROWS){
                this.addSlot(new BundleSlot(container, i, 8 + col * 18, 18 + row * 18));
            }else{
                this.addSlot(new BundleSlot(container, i, Integer.MIN_VALUE, Integer.MIN_VALUE));
            }
        }

        this.addStandardInventorySlots(playerInventory, 8, 18 + VISIBLE_ROWS * 18 + 13);
    }

    public void scrollItems(float position){
        int r = this.getRow(position);

        for(int i = 0; i < this.container.getContainerSize(); i++){
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
        return ((BundleContainer) this.container).countCanInsertToBundle(insertStack);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if(slot < this.container.getContainerSize()){
                if(!this.moveItemStackTo(itemStack2, this.container.getContainerSize(), this.slots.size(), true)){
                    return ItemStack.EMPTY;
                }
            }else{
                int count = countCanInsertToBundle(itemStack2);
                itemStack2.shrink(count);
                itemStack.setCount(count);
                if(!this.moveItemStackTo(itemStack, 0, this.container.getContainerSize(), false)){
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    protected int getOverflowRows(){
        return Mth.positiveCeilDiv(this.container.getContainerSize(), CLOS) - VISIBLE_ROWS;
    }

    protected float getScrollPosition(int row) {
        return Mth.clamp((float)row / this.getOverflowRows(), 0.0F, 1.0F);
    }

    protected float getScrollPosition(float current, double amount){
        return Mth.clamp(current - (float) (amount / this.getOverflowRows()), 0.0F, 1.0F);
    }

    protected int getRow(float scroll){
        return Math.max((int) (this.getOverflowRows() * scroll + 0.5F), 0);
    }

    public Container getContainer(){
        return this.container;
    }

    class BundleSlot extends Slot {
        public BundleSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return BundleItemMenu.this.countCanInsertToBundle(stack) != 0;
        }

        @Override
        public ItemStack safeInsert(ItemStack stack, int count) {
            int canInsert = BundleItemMenu.this.countCanInsertToBundle(stack);
            return super.safeInsert(stack, Math.min(count, canInsert));
        }
    }
}

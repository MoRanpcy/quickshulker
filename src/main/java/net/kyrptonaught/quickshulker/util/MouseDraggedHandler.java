package net.kyrptonaught.quickshulker.util;

import com.google.common.collect.Sets;
import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.mixin.ScreenAccessor;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.mixin.HandledScreenInvoker;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.Set;

public class MouseDraggedHandler {
    private static DragMode dragMode;
    private static final Set<Slot> DRAGGED_SLOTS = Sets.<Slot>newHashSet();

    public static boolean canInsertIntoContainer(Player player, ItemStack hostStack, ItemStack insertStack){
        Container inv = Util.getQuickItemInventory(player, hostStack);
        if(inv == null) return false;
        for(int i = inv.getContainerSize() - 1; i >= 0; i--){
            ItemStack pickStack = inv.getItem(i);
            if(pickStack.isEmpty() || (ItemStack.isSameItemSameComponents(pickStack, insertStack) && pickStack.getCount() < pickStack.getMaxStackSize())) return true;
        }
        return false;
    }

    public static boolean isContainerEmpty(Player player, ItemStack hostStack){
        Container inv = Util.getQuickItemInventory(player, hostStack);
        if(inv != null){
            return inv.isEmpty();
        }
        return true;
    }

    public static boolean beforeMouseClick(AbstractContainerScreen<?> screen, MouseButtonEvent click){
        if(!QuickShulker.getConfig().supportsMouseDragged) return false;
        Slot slot = ((HandledScreenInvoker) screen).QS$getSlotAt(click.x(), click.y());
        if(slot != null && click.button() == 1){
            Minecraft client = ((ScreenAccessor) screen).getClient();
            ItemStack itemStack  = screen.getMenu().getCarried();
            Container inv = Util.getQuickItemInventory(client.player, itemStack);
            if(inv == null) return false;
            if(slot.hasItem()){
                dragMode = DragMode.BUNDLE;
            }else{
                dragMode = DragMode.UNBUNDLE;
            }
            DRAGGED_SLOTS.clear();
            return true;
        }
        return false;
    }

    public static boolean beforeMouseDragged(AbstractContainerScreen<?> screen, MouseButtonEvent click){
        if(!QuickShulker.getConfig().supportsMouseDragged) return false;
        boolean result = false;
        if(dragMode != null){
            Minecraft client = ((ScreenAccessor) screen).getClient();
            AbstractContainerMenu handler = screen.getMenu();
            ItemStack itemStack = handler.getCarried();
            if(click.button() != 1){
                dragMode = null;
                DRAGGED_SLOTS.clear();
                return false;
            }
            Slot slot = ((HandledScreenInvoker) screen).QS$getSlotAt(click.x(), click.y());
            if(slot != null && (handler.canDragTo(slot) || slot.mayPickup(client.player))){
                if(dragMode == DragMode.BUNDLE){
                    if(slot.hasItem() && canInsertIntoContainer(client.player, itemStack, slot.getItem()) && !ShulkerUtils.isShulkerItem(slot.getItem()) && !DRAGGED_SLOTS.contains(slot)){
                        DRAGGED_SLOTS.add(slot);
                        ((HandledScreenInvoker) screen).QS$onMouseClick(slot, slot.index, click.button(), ContainerInput.PICKUP);
                        result = true;
                    }
                }else{
                    if(!slot.hasItem() && !isContainerEmpty(client.player, itemStack) && !DRAGGED_SLOTS.contains(slot)){
                        DRAGGED_SLOTS.add(slot);
                        ((HandledScreenInvoker) screen).QS$onMouseClick(slot, slot.index, click.button(), ContainerInput.PICKUP);
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    public static boolean beforeMouseReleased(AbstractContainerScreen<?> screen, MouseButtonEvent click){
        if(!QuickShulker.getConfig().supportsMouseDragged) return false;
        if(dragMode != null){
            dragMode = null;
            if(click.button() == 1 && !DRAGGED_SLOTS.isEmpty()){
                DRAGGED_SLOTS.clear();
                return true;
            }
        }
        return false;
    }

    public static void beforeDrawForeground(AbstractContainerScreen<?> screen, GuiGraphicsExtractor context, int mouseX, int mouseY){
        AbstractContainerMenu handler = screen.getMenu();
        for(Slot slot : handler.slots){
            if(DRAGGED_SLOTS.contains(slot)){
                context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80FFFFFF);
            }
        }
    }

    private enum DragMode{
        BUNDLE,
        UNBUNDLE
    }
}

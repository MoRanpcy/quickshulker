package net.kyrptonaught.quickshulker.util;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.mixin.HandledScreenInvoker;
import net.kyrptonaught.shulkerutils.ShulkerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Set;

public class MouseDraggedHandler {
    private static DragMode dragMode;
    private static final Set<Slot> DRAGGED_SLOTS = Sets.<Slot>newHashSet();

    public static boolean canInsertIntoContainer(PlayerEntity player, ItemStack hostStack, ItemStack insertStack){
        Inventory inv = Util.getQuickItemInventory(player, hostStack);
        if(inv == null) return false;
        for(int i = inv.size() - 1; i >= 0; i--){
            ItemStack pickStack = inv.getStack(i);
            if(pickStack.isEmpty() || (ItemStack.areItemsAndComponentsEqual(pickStack, insertStack) && pickStack.getCount() < pickStack.getMaxCount())) return true;
        }
        return false;
    }

    public static boolean isContainerEmpty(PlayerEntity player, ItemStack hostStack){
        Inventory inv = Util.getQuickItemInventory(player, hostStack);
        if(inv != null){
            return inv.isEmpty();
        }
        return true;
    }

    public static boolean beforeMouseClick(HandledScreen<?> screen, double mouseX, double mouseY, int button){
        if(!QuickShulkerMod.getConfig().supportsMouseDragged) return false;
        Slot slot = ((HandledScreenInvoker) screen).QS$getSlotAt(mouseX, mouseY);
        if(slot != null && button == 1){
            MinecraftClient client = ((ScreenAccessor) screen).getClient();
            ItemStack itemStack  = screen.getScreenHandler().getCursorStack();
            Inventory inv = Util.getQuickItemInventory(client.player, itemStack);
            if(inv == null) return false;
            if(slot.hasStack()){
                dragMode = DragMode.BUNDLE;
            }else{
                dragMode = DragMode.UNBUNDLE;
            }
            DRAGGED_SLOTS.clear();
            return true;
        }
        return false;
    }

    public static boolean beforeMouseDragged(HandledScreen<?> screen, double mouseX, double mouseY, int button){
        if(!QuickShulkerMod.getConfig().supportsMouseDragged) return false;
        boolean result = false;
        if(dragMode != null){
            MinecraftClient client = ((ScreenAccessor) screen).getClient();
            ScreenHandler handler = screen.getScreenHandler();
            ItemStack itemStack = handler.getCursorStack();
            if(button != 1){
                dragMode = null;
                DRAGGED_SLOTS.clear();
                return false;
            }
            Slot slot = ((HandledScreenInvoker) screen).QS$getSlotAt(mouseX, mouseY);
            if(slot != null && (handler.canInsertIntoSlot(slot) || slot.canTakeItems(client.player))){
                if(dragMode == DragMode.BUNDLE){
                    if(slot.hasStack() && canInsertIntoContainer(client.player, itemStack, slot.getStack()) && !ShulkerUtils.isShulkerItem(slot.getStack()) && !DRAGGED_SLOTS.contains(slot)){
                        DRAGGED_SLOTS.add(slot);
                        ((HandledScreenInvoker) screen).QS$onMouseClick(slot, slot.id, button, SlotActionType.PICKUP);
                        result = true;
                    }
                }else{
                    if(!slot.hasStack() && !isContainerEmpty(client.player, itemStack) && !DRAGGED_SLOTS.contains(slot)){
                        DRAGGED_SLOTS.add(slot);
                        ((HandledScreenInvoker) screen).QS$onMouseClick(slot, slot.id, button, SlotActionType.PICKUP);
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    public static boolean beforeMouseReleased(HandledScreen<?> screen, double mouseX, double mouseY, int button){
        if(!QuickShulkerMod.getConfig().supportsMouseDragged) return false;
        if(dragMode != null){
            dragMode = null;
            if(button == 1 && !DRAGGED_SLOTS.isEmpty()){
                DRAGGED_SLOTS.clear();
                return true;
            }
        }
        return false;
    }

    public static void beforeDrawForeground(HandledScreen<?> screen, DrawContext context, int mouseX, int mouseY){
        ScreenHandler handler = screen.getScreenHandler();
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

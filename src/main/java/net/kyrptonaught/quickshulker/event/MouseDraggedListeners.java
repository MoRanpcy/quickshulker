package net.kyrptonaught.quickshulker.event;

import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.util.MouseDraggedHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = QuickShulker.MOD_ID, value = Dist.CLIENT)
public class MouseDraggedListeners {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenMouseClicked(ScreenEvent.MouseButtonPressed.Pre event){
        if(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen){
            if(MouseDraggedHandler.beforeMouseClick(containerScreen, event.getMouseButtonEvent())) event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenMouseReleased(ScreenEvent.MouseButtonReleased.Pre event){
        if(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen){
            if(MouseDraggedHandler.beforeMouseReleased(containerScreen, event.getMouseButtonEvent())) event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenMouseDragged(ScreenEvent.MouseDragged.Pre event){
        if(event.getScreen() instanceof AbstractContainerScreen<?> screen){
            if(MouseDraggedHandler.beforeMouseDragged(screen, event.getMouseButtonEvent())) event.setCanceled(true);
        }
    }
}

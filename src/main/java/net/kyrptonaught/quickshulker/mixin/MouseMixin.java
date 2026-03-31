package net.kyrptonaught.quickshulker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.util.MouseDraggedHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MouseHandler.class, priority = 1001)
@Environment(EnvType.CLIENT)
public class MouseMixin {

    @WrapOperation(
            method = "onButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"
            )
    )
    public boolean QS$mouseClicked(Screen screen, MouseButtonEvent click, boolean bl2, Operation<Boolean> original){
        boolean result = false;
        if(screen instanceof AbstractContainerScreen<?> handledScreen){
            result = MouseDraggedHandler.beforeMouseClick(handledScreen, click);
        }
        return result || original.call(screen, click, bl2);
    }

    @WrapOperation(
            method = "onButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;mouseReleased(Lnet/minecraft/client/input/MouseButtonEvent;)Z"
            )
    )

    public boolean QS$mouseReleased(Screen screen, MouseButtonEvent click, Operation<Boolean> original){
        boolean result = false;
        if(screen instanceof AbstractContainerScreen<?> handledScreen){
            result = MouseDraggedHandler.beforeMouseReleased(handledScreen, click);
        }
        return result || original.call(screen, click);
    }

    @WrapOperation(
            method = "handleAccumulatedMovement()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;mouseDragged(Lnet/minecraft/client/input/MouseButtonEvent;DD)Z"
            )
    )
    public boolean QS$mouseDragged(Screen screen, MouseButtonEvent click, double offsetX, double offsetY, Operation<Boolean> original){
        boolean result = false;
        if(screen instanceof AbstractContainerScreen<?> handledScreen){
            result = MouseDraggedHandler.beforeMouseDragged(handledScreen, click);
        }
        return result || original.call(screen, click, offsetX, offsetY);
    }
}

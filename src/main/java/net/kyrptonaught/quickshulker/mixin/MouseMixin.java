package net.kyrptonaught.quickshulker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.util.MouseDraggedHandler;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Mouse.class, priority = 1001)
@Environment(EnvType.CLIENT)
public class MouseMixin {

    @WrapOperation(
            method = "onMouseButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z"
            )
    )
    public boolean QS$mouseClicked(Screen screen, Click click, boolean bl2, Operation<Boolean> original){
        boolean result = false;
        if(screen instanceof HandledScreen<?> handledScreen){
            result = MouseDraggedHandler.beforeMouseClick(handledScreen, click);
        }
        return result || original.call(screen, click, bl2);
    }

    @WrapOperation(
            method = "onMouseButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(Lnet/minecraft/client/gui/Click;)Z"
            )
    )

    public boolean QS$mouseReleased(Screen screen, Click click, Operation<Boolean> original){
        boolean result = false;
        if(screen instanceof HandledScreen<?> handledScreen){
            result = MouseDraggedHandler.beforeMouseReleased(handledScreen, click);
        }
        return result || original.call(screen, click);
    }

    @WrapOperation(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;mouseDragged(Lnet/minecraft/client/gui/Click;DD)Z"
            )
    )
    public boolean QS$mouseDragged(Screen screen, Click click, double offsetX, double offsetY, Operation<Boolean> original){
        boolean result = false;
        if(screen instanceof HandledScreen<?> handledScreen){
            result = MouseDraggedHandler.beforeMouseDragged(handledScreen, click);
        }
        return result || original.call(screen, click, offsetX, offsetY);
    }
}

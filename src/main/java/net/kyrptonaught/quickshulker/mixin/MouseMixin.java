package net.kyrptonaught.quickshulker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.util.MouseDraggedHandler;
import net.minecraft.client.Mouse;
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
                    target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(DDI)Z"
            )
    )
    public boolean QS$mouseClicked(Screen screen, double mouseX, double mouseY, int button, Operation<Boolean> original){
        boolean result = false;
        if(screen instanceof HandledScreen<?> handledScreen){
            result = MouseDraggedHandler.beforeMouseClick(handledScreen, mouseX, mouseY, button);
        }
        return result || original.call(screen, mouseX, mouseY, button);
    }

    @WrapOperation(
            method = "onMouseButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z"
            )
    )

    public boolean QS$mouseReleased(Screen screen, double mouseX, double mouseY, int button, Operation<Boolean> original){
        boolean result = false;
        if(screen instanceof HandledScreen<?> handledScreen){
            result = MouseDraggedHandler.beforeMouseReleased(handledScreen, mouseX, mouseY, button);
        }
        return result || original.call(screen, mouseX, mouseY, button);
    }

    @WrapOperation(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;mouseDragged(DDIDD)Z"
            )
    )
    public boolean QS$mouseDragged(Screen screen, double mouseX, double mouseY, int button, double deltaX, double deltaY, Operation<Boolean> original){
        boolean result = false;
        if(screen instanceof HandledScreen<?> handledScreen){
            result = MouseDraggedHandler.beforeMouseDragged(handledScreen, mouseX, mouseY, button);
        }
        return result || original.call(screen, mouseX, mouseY, button, deltaX, deltaY);
    }
}

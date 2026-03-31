package net.kyrptonaught.quickshulker.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.util.MouseDraggedHandler;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mouse.class, priority = 999)
@Environment(EnvType.CLIENT)
public class MouseMixin {

    @Shadow
    private int activeButton;

    @Inject(
            method = "method_1611([ZLnet/minecraft/client/gui/screen/Screen;DDI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(DDI)Z"
            ),
            cancellable = true)
    private static void QS$mouseClicked(boolean[] bls, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci){
        if(screen instanceof HandledScreen<?> handledScreen){
            boolean result = MouseDraggedHandler.beforeMouseClick(handledScreen, mouseX, mouseY, button);
            if(result){
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "method_1605([ZLnet/minecraft/client/gui/screen/Screen;DDI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z"
            ),
            cancellable = true)
    private static void QS$mouseReleased(boolean[] bls, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci){
        if(screen instanceof HandledScreen<?> handledScreen){
            boolean result = MouseDraggedHandler.beforeMouseReleased(handledScreen, mouseX, mouseY, button);
            if(result){
                ci.cancel();
            }
        }
    }

    @Inject(method = "method_55795(Lnet/minecraft/client/gui/screen/Screen;DDDD)V", at = @At("HEAD"), cancellable = true)
    public void QS$mouseDragged(Screen screen, double mouseX, double mouseY, double deltaX, double deltaY, CallbackInfo ci){
        if(screen instanceof HandledScreen<?> handledScreen){
            boolean result = MouseDraggedHandler.beforeMouseDragged(handledScreen, mouseX, mouseY, this.activeButton);
            if(result){
                ci.cancel();
            }
        }
    }
}

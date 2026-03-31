package net.kyrptonaught.quickshulker.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.client.QuickShulkerModClient;
import net.kyrptonaught.quickshulker.util.MouseDraggedHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
@Environment(EnvType.CLIENT)
public abstract class ScreenMixin {
    @Shadow
    protected Slot focusedSlot;

    @Shadow
    @Final
    protected ScreenHandler handler;

    @Shadow private boolean cancelNextRelease;

    @Inject(method = "init", at = @At("TAIL"))
    private void fixMouse(CallbackInfo ci) {
        if (QuickShulkerMod.lastMouseX != 0 && QuickShulkerMod.lastMouseY != 0) {
            GLFW.glfwSetCursorPos(MinecraftClient.getInstance().getWindow().getHandle(), QuickShulkerMod.lastMouseX, QuickShulkerMod.lastMouseY);
            QuickShulkerMod.lastMouseY = 0;
            QuickShulkerMod.lastMouseX = 0;
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void QS$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (QuickShulkerMod.getConfig().keybingInInv) {
            if (QuickShulkerModClient.getKeybinding().matches(keyCode, InputUtil.Type.KEYSYM)) {
                if (handleTrigger())
                    cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void QS$mousePressed(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (QuickShulkerMod.getConfig().rightClickInv) {
            if (this.handler.getCursorStack().isEmpty() && button == 1 && this.focusedSlot != null && this.focusedSlot.getStack().getCount() == 1) {
                if (handleTrigger()) {
                    this.cancelNextRelease = true;
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
        if (QuickShulkerMod.getConfig().keybingInInv) {
            if (QuickShulkerModClient.getKeybinding().matches(button, InputUtil.Type.MOUSE)) {
                if (handleTrigger()) {
                    this.cancelNextRelease = true;
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V",
                    shift = At.Shift.AFTER
            )
    )
    private void QS$drawForeground(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci){
        HandledScreen<?> screen  = (HandledScreen<?>) (Object) this;
        MouseDraggedHandler.beforeDrawForeground(screen, context, mouseX, mouseY);
    }

    @Unique
    private boolean handleTrigger() {
        if (this.focusedSlot != null) {
            return isValid(this.focusedSlot.getStack(), ClientUtil.getSlotId(handler, this.focusedSlot));
        }
        return false;
    }

    @Unique
    private boolean isValid(ItemStack stack, int id) {
        if (this.focusedSlot.inventory instanceof PlayerInventory)
            if (ClientUtil.CheckAndSend(stack, id)) {
                QuickShulkerMod.lastMouseX = MinecraftClient.getInstance().mouse.getX();
                QuickShulkerMod.lastMouseY = MinecraftClient.getInstance().mouse.getY();
                return true;
            }
        return false;
    }
}

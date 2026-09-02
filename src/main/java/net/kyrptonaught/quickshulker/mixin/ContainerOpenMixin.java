package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.event.EventListeners;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ContainerOpenMixin {
    @Inject(method = "openHandledScreen", at = @At("TAIL"))
    private void onOpenHandledScreen(NamedScreenHandlerFactory factory, CallbackInfoReturnable<Boolean> cir){
        EventListeners.containerOpenedListener((ServerPlayerEntity) (Object) this);
    }

    @Inject(method = "onHandledScreenClosed()V", at = @At("HEAD"))
    private void onCloseHandledScreen(CallbackInfo ci) {
        EventListeners.containerClosedListener((ServerPlayerEntity) (Object) this);
    }
}

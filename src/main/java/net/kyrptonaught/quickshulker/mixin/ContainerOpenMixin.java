package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.event.EventListeners;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ContainerOpenMixin {
    @Inject(method = "openMenu", at = @At("TAIL"))
    private void onOpenHandledScreen(MenuProvider provider, CallbackInfoReturnable<Boolean> cir){
        EventListeners.containerOpenedListener((ServerPlayer) (Object) this);
    }

    @Inject(method = "doCloseContainer()V", at = @At("HEAD"))
    private void onCloseContainer(CallbackInfo ci){
        EventListeners.containerClosedListener((ServerPlayer) (Object) this);
    }
}

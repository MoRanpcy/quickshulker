package net.kyrptonaught.kyrptconfig.mixin.displaykeybind;

import net.kyrptonaught.kyrptconfig.keybinding.SpoofedKeysHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/option/GameOptions$3")
public class GameOptions$VisitorMixin {

    @Inject(method = "visitString", at = @At("HEAD"), cancellable = true)
    public void dontDoThat(String key, String current, CallbackInfoReturnable<String> cir) {
        if (SpoofedKeysHelper.spoofed_Keys.contains(key))
            cir.setReturnValue(current);
    }
}

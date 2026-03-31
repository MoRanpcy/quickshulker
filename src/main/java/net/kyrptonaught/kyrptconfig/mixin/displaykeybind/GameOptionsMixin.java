package net.kyrptonaught.kyrptconfig.mixin.displaykeybind;

import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.kyrptonaught.kyrptconfig.keybinding.SpoofedKeysHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class GameOptionsMixin {

    @Shadow
    @Final
    public KeyMapping[] keyMappings;


    @Inject(method = "processOptions", at = @At(value = "HEAD"))
    public void genSpoofedKeyBindList(Options.FieldAccess visitor, CallbackInfo ci) {
        SpoofedKeysHelper.spoofed_Keys.clear();
        for (KeyMapping keyBinding : this.keyMappings) {
            if (keyBinding instanceof DisplayOnlyKeyBind)
                SpoofedKeysHelper.spoofed_Keys.add("key_" + keyBinding.getName());
        }
    }
}

package net.kyrptonaught.kyrptconfig.mixin.displaykeybind;

import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.kyrptonaught.kyrptconfig.keybinding.SpoofedKeysHelper;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow
    @Final
    public KeyBinding[] allKeys;


    @Inject(method = "accept", at = @At(value = "HEAD"))
    public void genSpoofedKeyBindList(GameOptions.Visitor visitor, CallbackInfo ci) {
        SpoofedKeysHelper.spoofed_Keys.clear();
        for (KeyBinding keyBinding : this.allKeys) {
            if (keyBinding instanceof DisplayOnlyKeyBind)
                SpoofedKeysHelper.spoofed_Keys.add("key_" + keyBinding.getTranslationKey());
        }
    }
}

package net.kyrptonaught.quickshulker.mixin.compat.peek;

import de.maxhenkel.peek.Peek;
import de.maxhenkel.peek.data.DataStore;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.kyrptonaught.quickshulker.compat.ModIds;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.peek))
@Mixin(ShulkerBoxScreenHandler.class)
public class ShulkerBoxScreenHandlerMixin {
    @Inject(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/ScreenHandler;<init>(Lnet/minecraft/screen/ScreenHandlerType;I)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void QS$setDataStoreNull(int syncId, PlayerInventory playerInventory, Inventory inventory, CallbackInfo ci){
        if(Peek.CONFIG.showShulkerBoxBlockHint.get() && DataStore.lastOpenedShulkerBox != null){
            DataStore.lastOpenedShulkerBox = null;
        }
    }
}

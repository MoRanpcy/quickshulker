package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.StonecutterScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {CraftingScreenHandler.class, StonecutterScreenHandler.class})
public abstract class CraftingScreenHandlerMixin extends ScreenHandler {

    protected CraftingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void overrideCanUse(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (((ItemInventoryContainer) this).hasItem()) {
            ItemStack stack = player.getInventory().getStack(((ItemInventoryContainer) this).getUsedSlotInPlayerInv());
            if (Util.isOpenableItem(stack))
                cir.setReturnValue(true);
        }
    }
}

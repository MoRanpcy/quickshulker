package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.api.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {CraftingMenu.class, StonecutterMenu.class, ItemCombinerMenu.class})
public abstract class CraftingMenuMixin extends AbstractContainerMenu {

    protected CraftingMenuMixin(@Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "stillValid(Lnet/minecraft/world/entity/player/Player;)Z", at = @At("HEAD"), cancellable = true)
    public void overrideCanUse(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (((ItemInventoryContainer) this).hasItem()) {
            ItemStack stack = player.getInventory().getItem(((ItemInventoryContainer) this).getUsedSlotInPlayerInv());
            if (Util.isOpenableItem(stack))
                cir.setReturnValue(true);
        }
    }
}

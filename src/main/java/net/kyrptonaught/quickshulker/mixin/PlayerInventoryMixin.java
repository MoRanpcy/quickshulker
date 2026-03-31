package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.api.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory {
    @Shadow
    @Final
    public PlayerEntity player;

    @Shadow
    public abstract ItemStack getStack(int slot);

    @Inject(method = "canStackAddMore", at = @At("RETURN"), cancellable = true)
    public void QS$canStackAddMore(ItemStack existingStack, ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        ScreenHandler handler = player.currentScreenHandler;
        if((((ItemInventoryContainer) handler).hasItem())){
            if(cir.getReturnValue()
                    && existingStack == this.getStack(((ItemInventoryContainer) handler).getUsedSlotInPlayerInv())
                    && !QuickOpenableRegistry.getQuickie(stack.getItem()).ignoreSingleStackCheck){
                cir.setReturnValue(false);
            }
        }
    }
}

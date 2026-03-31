package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.api.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin implements Container {
    @Shadow
    @Final
    public Player player;

    @Shadow
    public abstract ItemStack getItem(int slot);

    @Inject(method = "hasRemainingSpaceForItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    public void QS$hasRemainingSpaceForItem(ItemStack existingStack, ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        AbstractContainerMenu handler = player.containerMenu;
        if((((ItemInventoryContainer) handler).hasItem())){
            if(cir.getReturnValue()
                    && existingStack == this.getItem(((ItemInventoryContainer) handler).getUsedSlotInPlayerInv())
                    && !QuickOpenableRegistry.getQuickie(stack.getItem()).ignoreSingleStackCheck){
                cir.setReturnValue(false);
            }
        }
    }
}

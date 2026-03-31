package net.kyrptonaught.quickshulker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kyrptonaught.quickshulker.api.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.ModContainerLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    public AnvilMenuMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

//    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
//    public void QS$canUse(BlockState state, CallbackInfoReturnable<Boolean> cir){
//        if(this.context instanceof ModScreenHandlerContext con && con.stack.isIn(ItemTags.ANVIL)){
//            cir.setReturnValue(true);
//        }
//    }

    @WrapOperation(
            method = "onTake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"
            )
    )
    public void QS$execute(ContainerLevelAccess instance, BiConsumer<Level, BlockPos> function, Operation<Void> original){
        if(instance instanceof ModContainerLevelAccess context){
            instance.execute((level, pos) -> {
                Player player = context.player;
                ItemStack stack = context.stack;
                int slotInInv = ((ItemInventoryContainer) player.containerMenu).getUsedSlotInPlayerInv();
                if (!player.isCreative() && stack.is(ItemTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
                    if(stack.getItem() == Items.ANVIL){
                        player.getInventory().setItem(slotInInv, new ItemStack(Items.CHIPPED_ANVIL));
                        level.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
                    }else if(stack.getItem() == Items.CHIPPED_ANVIL){
                        player.getInventory().setItem(slotInInv, new ItemStack(Items.DAMAGED_ANVIL));
                        level.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
                    }else{
                        player.getInventory().removeItemNoUpdate(slotInInv);
                        level.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, pos, 0);
                    }
                }else{
                    level.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
                }
            });
        }else{
            original.call(instance, function);
        }
    }
}

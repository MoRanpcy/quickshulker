package net.kyrptonaught.quickshulker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kyrptonaught.quickshulker.api.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.ModScreenHandlerContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

//    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
//    public void QS$canUse(BlockState state, CallbackInfoReturnable<Boolean> cir){
//        if(this.context instanceof ModScreenHandlerContext con && con.stack.isIn(ItemTags.ANVIL)){
//            cir.setReturnValue(true);
//        }
//    }

    @WrapOperation(
            method = "onTakeOutput",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"
            )
    )
    public void QS$run(ScreenHandlerContext instance, BiConsumer<World, BlockPos> function, Operation<Void> original){
        if(instance instanceof ModScreenHandlerContext context){
            instance.run((world, pos) -> {
                PlayerEntity player = context.player;
                ItemStack stack = context.stack;
                int slotInInv = ((ItemInventoryContainer) player.currentScreenHandler).getUsedSlotInPlayerInv();
                if (!player.isCreative() && stack.isIn(ItemTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
                    if(stack.getItem() == Items.ANVIL){
                        player.getInventory().setStack(slotInInv, new ItemStack(Items.CHIPPED_ANVIL));
                        world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
                    }else if(stack.getItem() == Items.CHIPPED_ANVIL){
                        player.getInventory().setStack(slotInInv, new ItemStack(Items.DAMAGED_ANVIL));
                        world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
                    }else{
                        player.getInventory().removeStack(slotInInv);
                        world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
                    }
                }else{
                    world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
                }
            });
        }else{
            original.call(instance, function);
        }
    }
}

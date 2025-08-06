package net.kyrptonaught.shulkerutils.mixin;

import net.kyrptonaught.shulkerutils.UpgradableShulker;
import net.minecraft.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerMixin implements UpgradableShulker {

    @Override
    public int getInventorySize() {
        return 27;
    }

}

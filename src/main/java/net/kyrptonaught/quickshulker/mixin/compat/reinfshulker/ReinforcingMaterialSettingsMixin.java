package net.kyrptonaught.quickshulker.mixin.compat.reinfshulker;

import atonkish.reinfshulker.util.ReinforcingMaterialSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.kyrptonaught.quickshulker.compat.ModIds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Restriction(require = @Condition(ModIds.reinfshulker))
@Mixin(ReinforcingMaterialSettings.class)
public class ReinforcingMaterialSettingsMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static Item.Settings itemSettings(Item.Settings itemSettings){
            return itemSettings.component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
    }

}

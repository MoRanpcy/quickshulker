package net.kyrptonaught.quickshulker.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface HandledScreenInvoker {
    @Invoker("getSlotAt")
    Slot QS$getSlotAt(double mouseX, double mouseY);

    @Invoker("onMouseClick")
    void QS$onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);
}

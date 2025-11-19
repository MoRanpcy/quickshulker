package net.kyrptonaught.quickshulker.compat.reinfshulker;

import atonkish.reinfcore.screen.ReinforcedStorageScreenHandler;
import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfshulker.block.ReinforcedShulkerBoxBlock;
import atonkish.reinfshulker.block.entity.ModBlockEntityType;
import net.kyrptonaught.quickshulker.api.ItemStackInventory;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;

public class ReinfshulkerOpenableRegistry {

    private static final BiConsumer<PlayerEntity, ItemStack> REINFORCED_SHULKER_BOX_CONSUMER = (PlayerEntity player, ItemStack stack) -> {
        ReinforcedShulkerBoxBlock block = (ReinforcedShulkerBoxBlock) ((BlockItem) stack.getItem()).getBlock();
        ReinforcingMaterial material = block.getMaterial();
        ItemStackInventory inventory = new ItemStackInventory(stack, material.getSize());
        String namespace = BlockEntityType.getId(ModBlockEntityType.REINFORCED_SHULKER_BOX_MAP.get(material)).getNamespace();

        ScreenHandlerFactory screenHandlerFactory = (int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) ->
                ReinforcedStorageScreenHandler.createShulkerBoxScreen(material, syncId, playerInventory, inventory);
        Text text = stack.getComponents().contains(
                DataComponentTypes.CUSTOM_NAME) ? stack.getName() : Text.translatable("container." + namespace + "." + material.getName() + "ShulkerBox"
        );

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(screenHandlerFactory, text));
    };

    public static void registerProviders() {
        new QuickOpenableRegistry.Builder()
                .setItem(ReinforcedShulkerBoxBlock.class)
                .supportsBundleing(true)
                .setOpenAction(REINFORCED_SHULKER_BOX_CONSUMER)
                .register();
    }
}

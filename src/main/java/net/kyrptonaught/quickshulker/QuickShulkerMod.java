package net.kyrptonaught.quickshulker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.kyrptonaught.quickshulker.api.*;
import net.kyrptonaught.quickshulker.compat.ModIds;
import net.kyrptonaught.quickshulker.compat.ModUtils;
import net.kyrptonaught.quickshulker.compat.reinfshulker.ReinfshulkerOpenableRegistry;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.event.EventListeners;
import net.kyrptonaught.quickshulker.network.EnderChestS2CSyncPacket;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.kyrptonaught.quickshulker.network.QuickBundlePacket;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QuickShulkerMod implements ModInitializer, RegisterQuickShulker {

    public static final String MOD_ID = "quickshulker";
    public static ConfigManager.SingleConfigManager config = new ConfigManager.SingleConfigManager(MOD_ID, new ConfigOptions());
    public static double lastMouseX, lastMouseY;
    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        config.load();
        OpenShulkerPacket.registerReceivePacket();
        QuickBundlePacket.registerReceivePacket();
        EventListeners.registerEventListeners();

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (!world.isClient) {
                if (QuickShulkerMod.getConfig().rightClickToOpen) {
                    if (Util.isOpenableItem(stack) && Util.canOpenInHand(stack)) {
                        if (hand == Hand.MAIN_HAND)
                            Util.openItem(player, 0, player.getInventory().selectedSlot);
                        else Util.openItem(player, 0, PlayerInventory.OFF_HAND_SLOT);

                        return ActionResult.SUCCESS_SERVER;
                    }
                }
            }
            return ActionResult.PASS;
        });

        PayloadTypeRegistry.playS2C().register(OpenInventoryPacket.OPEN_INV_ID, OpenInventoryPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(EnderChestS2CSyncPacket.S2CEChestContentPacket.S2C_ECHEST_CONTENT_PACKET_ID, EnderChestS2CSyncPacket.S2CEChestContentPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(EnderChestS2CSyncPacket.S2CEChestSlotPacket.S2C_ECHEST_SLOT_PACKET_ID, EnderChestS2CSyncPacket.S2CEChestSlotPacket.CODEC);

        FabricLoader.getInstance().getEntrypoints(MOD_ID, RegisterQuickShulker.class).forEach(RegisterQuickShulker::registerProviders);
    }

    public static ConfigOptions getConfig() {
        return (ConfigOptions) config.getConfig();
    }

    @Override
    public void registerProviders() {
        if (getConfig().quickShulkerBox)
            new QuickOpenableRegistry.Builder()
                    .setItem(ShulkerBoxBlock.class)
                    .supportsBundleing(true)
                    .setOpenAction(((player, stack) -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                            new ShulkerBoxScreenHandler(i, player.getInventory(), new ItemStackInventory(stack, 27)), stack.getComponents().contains(DataComponentTypes.CUSTOM_NAME) ? stack.getName() : Text.translatable("container.shulkerBox")))))
                    .register();

        if (getConfig().quickEChest)
            new QuickOpenableRegistry.Builder(new QuickShulkerData.QuickEnderData())
                    .setItem(EnderChestBlock.class)
                    .supportsBundleing(true)
                    .ignoreSingleStackCheck(true)
                    .setOpenAction(((player, stack) -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                            GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, player.getEnderChestInventory()), Text.translatable("container.enderchest")))))
                    .register();

        if (getConfig().quickCraftingTables)
            new QuickOpenableRegistry.Builder()
                    .setItem(CraftingTableBlock.class)
                    .ignoreSingleStackCheck(true)
                    .setOpenAction(((player, stack) -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                            new CraftingScreenHandler(i, playerInventory, ScreenHandlerContext.create(player.getEntityWorld(), player.getBlockPos())), Text.translatable("container.crafting")))))
                    .register();

        if (getConfig().quickStonecutter)
            new QuickOpenableRegistry.Builder()
                    .setItem(StonecutterBlock.class)
                    .ignoreSingleStackCheck(true)
                    .setOpenAction(((player, stack) -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                            new StonecutterScreenHandler(i, playerInventory, ScreenHandlerContext.create(player.getEntityWorld(), player.getBlockPos())), Text.translatable("container.stonecutter")))))
                    .register();

        if(ModUtils.isModLoad(ModIds.reinfshulker) && QuickShulkerMod.getConfig().quickShulkerBox) {
            ReinfshulkerOpenableRegistry.registerProviders();
        }
    }

}

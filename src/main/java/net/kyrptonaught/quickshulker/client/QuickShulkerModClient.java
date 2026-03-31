package net.kyrptonaught.quickshulker.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.kyrptconfig.keybinding.CustomKeyBinding;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.event.KeyBindingRegister;
import net.kyrptonaught.quickshulker.gui.MenuScreens;
import net.kyrptonaught.quickshulker.util.EnderChestSyncHandler;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulkerClient;
import net.kyrptonaught.quickshulker.event.ModKeyCallback;
import net.kyrptonaught.quickshulker.network.EnderChestS2CSyncPacket;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;

@Environment(EnvType.CLIENT)
public class QuickShulkerModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_LEVEL_TICK.register(ModKeyCallback::onKeyPressed);
        KeyBindingRegister.register();
        MenuScreens.registerMenuScreens();

        PayloadTypeRegistry.serverboundPlay().register(OpenInventoryPacket.OPEN_INV_ID, OpenInventoryPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(OpenInventoryPacket.OPEN_INV_ID, (payload, context) -> {
            context.client().setScreen(new InventoryScreen(context.player()));
        });

        PayloadTypeRegistry.serverboundPlay().register(EnderChestS2CSyncPacket.S2CEChestContentPacket.S2C_ECHEST_CONTENT_PACKET_ID, EnderChestS2CSyncPacket.S2CEChestContentPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(EnderChestS2CSyncPacket.S2CEChestContentPacket.S2C_ECHEST_CONTENT_PACKET_ID, (payload, context) -> {
            context.client().execute(() -> {
                EnderChestSyncHandler.setEnderChestContent(context.player(), payload.itemStacks());
            });
        });

        PayloadTypeRegistry.serverboundPlay().register(EnderChestS2CSyncPacket.S2CEChestSlotPacket.S2C_ECHEST_SLOT_PACKET_ID, EnderChestS2CSyncPacket.S2CEChestSlotPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(EnderChestS2CSyncPacket.S2CEChestSlotPacket.S2C_ECHEST_SLOT_PACKET_ID, (payload, context) -> {
            context.client().execute(() -> {
                PlayerEnderChestContainer enderChestInventory = context.player().getEnderChestInventory();
                enderChestInventory.setItem(payload.slotId(), payload.itemStack());
            });
        });

        FabricLoader.getInstance().getEntrypoints(QuickShulkerMod.MOD_ID + "_client", RegisterQuickShulkerClient.class).forEach(RegisterQuickShulkerClient::registerClient);
    }

    public static CustomKeyBinding getKeybinding() {
        return QuickShulkerMod.getConfig().keybinding;
    }
}
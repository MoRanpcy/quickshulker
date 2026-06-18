package net.kyrptonaught.quickshulker.client;

import net.kyrptonaught.kyrptconfig.keybinding.CustomKeyBinding;
import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulkerClient;
import net.kyrptonaught.quickshulker.config.ModConfigMenu;
import net.kyrptonaught.quickshulker.event.ModKeyCallback;
import net.kyrptonaught.quickshulker.network.EnderChestS2CSyncPacket;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.kyrptonaught.quickshulker.util.EnderChestSyncHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ServiceLoader;

@Mod(value = QuickShulker.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = QuickShulker.MOD_ID, value = Dist.CLIENT)
public class QuickShulkerClient {

    public QuickShulkerClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, screen) ->  ModConfigMenu.getModConfigMenu(screen));
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        ServiceLoader.load(RegisterQuickShulkerClient.class).stream().map(ServiceLoader.Provider::get).forEach(RegisterQuickShulkerClient::registerClient);
    }

    @SubscribeEvent
    public static void onKeyPressed(LevelTickEvent.Pre event){
        if(event.getLevel() instanceof  ClientLevel clientLevel){
            ModKeyCallback.onKeyPressed(clientLevel);
        }
    }

    @SubscribeEvent
    public static void registerReceivePacket(RegisterClientPayloadHandlersEvent event){
        event.register(
                OpenInventoryPacket.OPEN_INV_ID,
                (payload, context) -> Minecraft.getInstance().gui.setScreen(new InventoryScreen(context.player()))
        );
        event.register(
                EnderChestS2CSyncPacket.S2CEChestContentPacket.S2C_ECHEST_CONTENT_PACKET_ID,
                (payload, context) -> context.enqueueWork(() -> EnderChestSyncHandler.setEnderChestContent(context.player(), payload.itemStacks()))
        );
        event.register(
                EnderChestS2CSyncPacket.S2CEChestSlotPacket.S2C_ECHEST_SLOT_PACKET_ID,
                (payload, context) -> context.enqueueWork(() -> {
                    PlayerEnderChestContainer enderChestInventory = context.player().getEnderChestInventory();
                    enderChestInventory.setItem(payload.slotId(), payload.itemStack());
                })
        );
    }

    public static CustomKeyBinding getKeybinding() {
        return QuickShulker.getConfig().keybinding;
    }
}

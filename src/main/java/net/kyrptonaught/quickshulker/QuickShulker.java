package net.kyrptonaught.quickshulker;

import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.kyrptonaught.quickshulker.api.*;
import net.kyrptonaught.quickshulker.api.*;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.gui.MenuTypes;
import net.kyrptonaught.quickshulker.gui.screen.BundleContainer;
import net.kyrptonaught.quickshulker.gui.screen.BundleItemMenu;
import net.kyrptonaught.quickshulker.network.EnderChestS2CSyncPacket;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.kyrptonaught.quickshulker.network.QuickBundlePacket;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.apache.logging.log4j.LogManager;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;

@Mod(QuickShulker.MOD_ID)
public class QuickShulker {

    public static final String MOD_ID = "quickshulker";
    public static ConfigManager.SingleConfigManager config = new ConfigManager.SingleConfigManager(MOD_ID, new ConfigOptions());
    public static double lastMouseX, lastMouseY;
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public QuickShulker(IEventBus modEventBus, ModContainer modContainer) {
        config.load();
        modEventBus.register(OpenShulkerPacket.class);
        modEventBus.register(QuickBundlePacket.class);
        MenuTypes.MENU_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerReceivePacket);
        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ServiceLoader.load(RegisterQuickShulker.class).stream().map(ServiceLoader.Provider::get).forEach(RegisterQuickShulker::registerProviders);
    }

    @SubscribeEvent
    public void onPlayerUseItem(PlayerInteractEvent.RightClickItem event){
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        if(getConfig().rightClickToOpen){
            if(Util.isOpenableItem(stack) && Util.canOpenInHand(stack)){
                if(event.getLevel().isClientSide()){
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }else{
                    if(event.getHand() == InteractionHand.MAIN_HAND)
                        Util.openItem(player, 0, player.getInventory().getSelectedSlot());
                    else Util.openItem(player, 0, Inventory.SLOT_OFFHAND);
                    event.setCancellationResult(InteractionResult.SUCCESS_SERVER);
                }
                event.setCanceled(true);
                return;
            }
        }
        event.setCancellationResult(InteractionResult.PASS);
    }

    public void registerReceivePacket(RegisterPayloadHandlersEvent event){
        event.registrar("1").playToClient(OpenInventoryPacket.OPEN_INV_ID, OpenInventoryPacket.CODEC);
        event.registrar("1").playToClient(EnderChestS2CSyncPacket.S2CEChestContentPacket.S2C_ECHEST_CONTENT_PACKET_ID, EnderChestS2CSyncPacket.S2CEChestContentPacket.CODEC);
        event.registrar("1").playToClient(EnderChestS2CSyncPacket.S2CEChestSlotPacket.S2C_ECHEST_SLOT_PACKET_ID, EnderChestS2CSyncPacket.S2CEChestSlotPacket.CODEC);
    }

    public static ConfigOptions getConfig() {
        return (ConfigOptions) config.getConfig();
    }

    public static class ProvidersRegister implements RegisterQuickShulker{
        @Override
        public void registerProviders() {
            if (getConfig().quickShulkerBox)
                new QuickOpenableRegistry.Builder()
                        .setItem(ShulkerBoxBlock.class)
                        .supportsBundleing(true)
                        .setOpenAction(((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                                new ShulkerBoxMenu(i, player.getInventory(), new ItemStackInventory(stack, 27)), stack.getComponents().has(DataComponents.CUSTOM_NAME) ? stack.getHoverName() : Component.translatable("container.shulkerBox")))))
                        .register();

            if (getConfig().quickEChest)
                new QuickOpenableRegistry.Builder(new QuickShulkerData.QuickEnderData())
                        .setItem(EnderChestBlock.class)
                        .supportsBundleing(true)
                        .ignoreSingleStackCheck(true)
                        .setOpenAction(((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                                ChestMenu.threeRows(i, playerInventory, player.getEnderChestInventory()), Component.translatable("container.enderchest")))))
                        .register();

            if (getConfig().quickCraftingTables)
                new QuickOpenableRegistry.Builder()
                        .setItem(CraftingTableBlock.class)
                        .ignoreSingleStackCheck(true)
                        .setOpenAction(((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                                new CraftingMenu(i, playerInventory, ContainerLevelAccess.create(player.level(), player.blockPosition())), Component.translatable("container.crafting")))))
                        .register();

            if (getConfig().quickStonecutter)
                new QuickOpenableRegistry.Builder()
                        .setItem(StonecutterBlock.class)
                        .ignoreSingleStackCheck(true)
                        .setOpenAction(((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                                new StonecutterMenu(i, playerInventory, ContainerLevelAccess.create(player.level(), player.blockPosition())), Component.translatable("container.stonecutter")))))
                        .register();

            if(getConfig().quickBundle)
                new QuickOpenableRegistry.Builder()
                        .setItem(BundleItem.class)
                        .setOpenAction((playerEntity, stack) -> playerEntity.openMenu(new SimpleMenuProvider((i, playerInventory, player) ->
                                new BundleItemMenu(i, playerInventory, new BundleContainer(stack, 64)), stack.getComponents().has(DataComponents.CUSTOM_NAME) ? stack.getHoverName() : Component.translatable("item.minecraft.bundle"))))
                        .register();

            if(getConfig().quickAnvil)
                new QuickOpenableRegistry.Builder()
                        .setItem(AnvilBlock.class)
                        .setOpenAction((playerEntity, stack) -> playerEntity.openMenu(new SimpleMenuProvider((i, playerInventory, player) ->
                                new AnvilMenu(i, playerInventory, new ModContainerLevelAccess(playerEntity, stack)), Component.translatable("container.repair"))))
                        .register();
        }
    }
}

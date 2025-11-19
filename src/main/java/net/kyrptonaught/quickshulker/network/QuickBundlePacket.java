package net.kyrptonaught.quickshulker.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.util.BundleHelper;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.util.PacketUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.List;

public record QuickBundlePacket(int slotId, ItemStack stackToBundle) implements CustomPayload {

    private static final Identifier QUICK_BUNDLE_PACKET = Identifier.of(QuickShulkerMod.MOD_ID, "quick_bundle_packet");
    private static final Id<QuickBundlePacket> QUICK_BUNDLE_PACKET_ID = new Id<>(QUICK_BUNDLE_PACKET);
    private static final PacketCodec<RegistryByteBuf, QuickBundlePacket> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeInt(value.slotId);
                PacketUtils.writeItemStack(buf, value.stackToBundle);},
            buf -> new QuickBundlePacket(buf.readInt(), PacketUtils.readItemStack(buf)));

    public static void registerReceivePacket() {
        PayloadTypeRegistry.playS2C().register(QuickBundlePacket.QUICK_BUNDLE_PACKET_ID, QuickBundlePacket.CODEC);
        PayloadTypeRegistry.playC2S().register(QuickBundlePacket.QUICK_BUNDLE_PACKET_ID, QuickBundlePacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(QuickBundlePacket.QUICK_BUNDLE_PACKET_ID, (payload, context) -> {
            if (context.player().isCreative()) {
                context.server().execute(() -> BundleHelper.bundleItemIntoStack(context.player(), context.player().getInventory().getStack(payload.slotId), payload.stackToBundle, null));
            }
        });
        UnbundlePacket.registerReceivePacket();
        BundleIntoHeld.registerReceivePacket();
    }

    @Environment(EnvType.CLIENT)
    public static void sendPacket(int slotID, ItemStack stackToBundle) {
        ClientPlayNetworking.send(new QuickBundlePacket(slotID, stackToBundle.copy()));
    }

    public static void sendCreativeSlotUpdate(ItemStack output, Slot slot) {
        MinecraftClient.getInstance().interactionManager.clickCreativeStack(output, slot.id);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return QUICK_BUNDLE_PACKET_ID;
    }

    public record BundleIntoHeld(List<ItemStack> stackList, int slotId) implements CustomPayload{

        private static final Identifier QUICK_BUNDLEHELD_PACKET = Identifier.of(QuickShulkerMod.MOD_ID, "quick_bundleheld_packet");
        private static final Id<BundleIntoHeld> QUICK_BUNDLEHELD_PACKET_ID = new Id<>(QUICK_BUNDLEHELD_PACKET);
        private static final PacketCodec<RegistryByteBuf, BundleIntoHeld> CODEC = PacketCodec.tuple(ItemStack.OPTIONAL_LIST_PACKET_CODEC, BundleIntoHeld::stackList, PacketCodecs.INTEGER, BundleIntoHeld::slotId, BundleIntoHeld::new);

        public static void registerReceivePacket() {
            PayloadTypeRegistry.playS2C().register(BundleIntoHeld.QUICK_BUNDLEHELD_PACKET_ID, BundleIntoHeld.CODEC);
            PayloadTypeRegistry.playC2S().register(BundleIntoHeld.QUICK_BUNDLEHELD_PACKET_ID, BundleIntoHeld.CODEC);
            ServerPlayNetworking.registerGlobalReceiver(BundleIntoHeld.QUICK_BUNDLEHELD_PACKET_ID, (payload, context) -> {
                if (context.player().isCreative()) {
                    context.server().execute(() -> {
                        Slot slot = context.player().currentScreenHandler.getSlot(payload.slotId);
                        BundleHelper.bundleItemIntoStack(context.player(), payload.stackList.get(1), payload.stackList.get(0), slot, null);
                    });
                }
            });
        }

        @Environment(EnvType.CLIENT)
        public static void sendPacket(ItemStack stackToBundle, ItemStack bundleStack, int slotId) {
            ClientPlayNetworking.send(new BundleIntoHeld(List.of(stackToBundle.copy(), bundleStack), slotId));
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return QUICK_BUNDLEHELD_PACKET_ID;
        }
    }

    public record UnbundlePacket(int slotId, ItemStack unbundleStack) implements CustomPayload{

        private static final Identifier QUICK_UNBUNDLE_PACKET = Identifier.of(QuickShulkerMod.MOD_ID, "quick_unbundle_packet");
        private static final Id<UnbundlePacket> QUICK_UNBUNDLE_PACKET_ID = new Id<>(QUICK_UNBUNDLE_PACKET);
        private static final PacketCodec<RegistryByteBuf, UnbundlePacket> CODEC = PacketCodec.of(
                (value, buf) -> {
                    buf.writeInt(value.slotId); PacketUtils.writeItemStack(buf, value.unbundleStack);},
                buf -> new UnbundlePacket(buf.readInt(), PacketUtils.readItemStack(buf)));

        public static void registerReceivePacket() {
            PayloadTypeRegistry.playS2C().register(UnbundlePacket.QUICK_UNBUNDLE_PACKET_ID, UnbundlePacket.CODEC);
            PayloadTypeRegistry.playC2S().register(UnbundlePacket.QUICK_UNBUNDLE_PACKET_ID, UnbundlePacket.CODEC);
            ServerPlayNetworking.registerGlobalReceiver(UnbundlePacket.QUICK_UNBUNDLE_PACKET_ID, (payload, context) -> {
                if (context.player().isCreative()) {
                    int playerInvSlotID = payload.slotId;
                    ItemStack unBundleStack = payload.unbundleStack;
                    context.server().execute(() -> {
                        Slot unbundleSlot = context.player().currentScreenHandler.getSlot(payload.slotId);
                        ItemStack output = BundleHelper.unbundleItem(context.player(), unBundleStack, unbundleSlot);
                        if (output != null)
//                            context.player().getInventory().setStack(playerInvSlotID, output);
                            unbundleSlot.setStack(output);
                    });
                }
            });
        }

        @Environment(EnvType.CLIENT)
        public static void sendPacket(int slotID, ItemStack unBundleStack) {
            ClientPlayNetworking.send(new UnbundlePacket(slotID, unBundleStack));
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return QUICK_UNBUNDLE_PACKET_ID;
        }
    }
}

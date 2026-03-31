package net.kyrptonaught.quickshulker.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.util.BundleHelper;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.util.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public record QuickBundlePacket(int slotId, ItemStack stackToBundle) implements CustomPacketPayload {

    private static final Identifier QUICK_BUNDLE_PACKET = Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_bundle_packet");
    private static final Type<QuickBundlePacket> QUICK_BUNDLE_PACKET_ID = new Type<>(QUICK_BUNDLE_PACKET);
    private static final StreamCodec<RegistryFriendlyByteBuf, QuickBundlePacket> CODEC = StreamCodec.ofMember(
            (value, buf) -> {
                buf.writeInt(value.slotId);
                PacketUtils.writeItemStack(buf, value.stackToBundle);},
            buf -> new QuickBundlePacket(buf.readInt(), PacketUtils.readItemStack(buf)));

    public static void registerReceivePacket() {
        PayloadTypeRegistry.clientboundPlay().register(QuickBundlePacket.QUICK_BUNDLE_PACKET_ID, QuickBundlePacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(QuickBundlePacket.QUICK_BUNDLE_PACKET_ID, QuickBundlePacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(QuickBundlePacket.QUICK_BUNDLE_PACKET_ID, (payload, context) -> {
            if (context.player().isCreative()) {
                context.server().execute(() -> BundleHelper.bundleItemIntoStack(context.player(), context.player().getInventory().getItem(payload.slotId), payload.stackToBundle, null));
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
        Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(output, slot.index);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return QUICK_BUNDLE_PACKET_ID;
    }

    public record BundleIntoHeld(List<ItemStack> stackList, int slotId) implements CustomPacketPayload{

        private static final Identifier QUICK_BUNDLEHELD_PACKET = Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_bundleheld_packet");
        private static final Type<BundleIntoHeld> QUICK_BUNDLEHELD_PACKET_ID = new Type<>(QUICK_BUNDLEHELD_PACKET);
        private static final StreamCodec<RegistryFriendlyByteBuf, BundleIntoHeld> CODEC = StreamCodec.composite(ItemStack.OPTIONAL_LIST_STREAM_CODEC, BundleIntoHeld::stackList, ByteBufCodecs.INT, BundleIntoHeld::slotId, BundleIntoHeld::new);
        public static void registerReceivePacket() {
            PayloadTypeRegistry.clientboundPlay().register(BundleIntoHeld.QUICK_BUNDLEHELD_PACKET_ID, BundleIntoHeld.CODEC);
            PayloadTypeRegistry.serverboundPlay().register(BundleIntoHeld.QUICK_BUNDLEHELD_PACKET_ID, BundleIntoHeld.CODEC);
            ServerPlayNetworking.registerGlobalReceiver(BundleIntoHeld.QUICK_BUNDLEHELD_PACKET_ID, (payload, context) -> {
                if (context.player().isCreative()) {
                    context.server().execute(() -> {
                        Slot slot = context.player().containerMenu.getSlot(payload.slotId);
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
        public Type<? extends CustomPacketPayload> type() {
            return QUICK_BUNDLEHELD_PACKET_ID;
        }
    }

    public record UnbundlePacket(int slotId, ItemStack unbundleStack) implements CustomPacketPayload{

        private static final Identifier QUICK_UNBUNDLE_PACKET = Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_unbundle_packet");
        private static final Type<UnbundlePacket> QUICK_UNBUNDLE_PACKET_ID = new Type<>(QUICK_UNBUNDLE_PACKET);
        private static final StreamCodec<RegistryFriendlyByteBuf, UnbundlePacket> CODEC = StreamCodec.ofMember(
                (value, buf) -> {
                    buf.writeInt(value.slotId); PacketUtils.writeItemStack(buf, value.unbundleStack);},
                buf -> new UnbundlePacket(buf.readInt(), PacketUtils.readItemStack(buf)));

        public static void registerReceivePacket() {
            PayloadTypeRegistry.clientboundPlay().register(UnbundlePacket.QUICK_UNBUNDLE_PACKET_ID, UnbundlePacket.CODEC);
            PayloadTypeRegistry.serverboundPlay().register(UnbundlePacket.QUICK_UNBUNDLE_PACKET_ID, UnbundlePacket.CODEC);
            ServerPlayNetworking.registerGlobalReceiver(UnbundlePacket.QUICK_UNBUNDLE_PACKET_ID, (payload, context) -> {
                if (context.player().isCreative()) {
                    context.server().execute(() -> {
                        Slot unbundleSlot = context.player().containerMenu.getSlot(payload.slotId);
                        BundleHelper.unbundleItem(context.player(), payload.unbundleStack, unbundleSlot);
                    });
                }
            });
        }

        @Environment(EnvType.CLIENT)
        public static void sendPacket(int slotID, ItemStack unBundleStack) {
            ClientPlayNetworking.send(new UnbundlePacket(slotID, unBundleStack));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return QUICK_UNBUNDLE_PACKET_ID;
        }
    }
}

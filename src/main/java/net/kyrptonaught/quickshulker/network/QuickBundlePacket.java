package net.kyrptonaught.quickshulker.network;

import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.util.BundleHelper;
import net.kyrptonaught.quickshulker.util.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.List;

public record QuickBundlePacket(int slotId, ItemStack stackToBundle) implements CustomPacketPayload {

    private static final Identifier QUICK_BUNDLE_PACKET = Identifier.fromNamespaceAndPath(QuickShulker.MOD_ID, "quick_bundle_packet");
    private static final Type<QuickBundlePacket> QUICK_BUNDLE_PACKET_ID = new Type<>(QUICK_BUNDLE_PACKET);
    private static final StreamCodec<RegistryFriendlyByteBuf, QuickBundlePacket> CODEC = StreamCodec.ofMember(
            (value, buf) -> {
                buf.writeInt(value.slotId);
                PacketUtils.writeItemStack(buf, value.stackToBundle);},
            buf -> new QuickBundlePacket(buf.readInt(), PacketUtils.readItemStack(buf)));

    @SubscribeEvent
    public static void registerReceivePacket(RegisterPayloadHandlersEvent event){
        event.registrar("1").playToServer(
                QUICK_BUNDLE_PACKET_ID,
                CODEC,
                (payload, context) -> {
                    if(context.player().isCreative()) {
                        context.enqueueWork(() -> BundleHelper.bundleItemIntoStack(context.player(), context.player().getInventory().getItem(payload.slotId), payload.stackToBundle, null));
                    }
                }
        );
        BundleIntoHeld.registerReceivePacket(event);
        UnbundlePacket.registerReceivePacket(event);
    }

    public static void sendPacket(int slotID, ItemStack stackToBundle) {
        ClientPacketDistributor.sendToServer(new QuickBundlePacket(slotID, stackToBundle.copy()));
    }

    public static void sendCreativeSlotUpdate(ItemStack output, Slot slot) {
        Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(output, slot.index);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return QUICK_BUNDLE_PACKET_ID;
    }

    public record BundleIntoHeld(List<ItemStack> stackList, int slotId) implements CustomPacketPayload{

        private static final Identifier QUICK_BUNDLEHELD_PACKET = Identifier.fromNamespaceAndPath(QuickShulker.MOD_ID, "quick_bundleheld_packet");
        private static final Type<BundleIntoHeld> QUICK_BUNDLEHELD_PACKET_ID = new Type<>(QUICK_BUNDLEHELD_PACKET);
        private static final StreamCodec<RegistryFriendlyByteBuf, BundleIntoHeld> CODEC = StreamCodec.composite(ItemStack.OPTIONAL_LIST_STREAM_CODEC, BundleIntoHeld::stackList, ByteBufCodecs.INT, BundleIntoHeld::slotId, BundleIntoHeld::new);

        public static void registerReceivePacket(RegisterPayloadHandlersEvent event){
            event.registrar("1").playToServer(
                    QUICK_BUNDLEHELD_PACKET_ID,
                    CODEC,
                    (payload, context) -> {
                        if (context.player().isCreative()) {
                            context.enqueueWork(() -> {
                                Slot slot = context.player().containerMenu.getSlot(payload.slotId);
                                BundleHelper.bundleItemIntoStack(context.player(), payload.stackList.get(1), payload.stackList.get(0), slot, null);
                            });
                        }
                    }
            );
        }

        public static void sendPacket(ItemStack stackToBundle, ItemStack bundleStack, int slotId) {
            ClientPacketDistributor.sendToServer(new BundleIntoHeld(List.of(stackToBundle.copy(), bundleStack), slotId));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return QUICK_BUNDLEHELD_PACKET_ID;
        }
    }

    public record UnbundlePacket(int slotId, ItemStack unbundleStack) implements CustomPacketPayload{

        private static final Identifier QUICK_UNBUNDLE_PACKET = Identifier.fromNamespaceAndPath(QuickShulker.MOD_ID, "quick_unbundle_packet");
        private static final Type<UnbundlePacket> QUICK_UNBUNDLE_PACKET_ID = new Type<>(QUICK_UNBUNDLE_PACKET);
        private static final StreamCodec<RegistryFriendlyByteBuf, UnbundlePacket> CODEC = StreamCodec.ofMember(
                (value, buf) -> {
                    buf.writeInt(value.slotId); PacketUtils.writeItemStack(buf, value.unbundleStack);},
                buf -> new UnbundlePacket(buf.readInt(), PacketUtils.readItemStack(buf)));

        public static void registerReceivePacket(RegisterPayloadHandlersEvent event){
            event.registrar("1").playToServer(
                    QUICK_UNBUNDLE_PACKET_ID,
                    CODEC,
                    (payload, context) -> {
                        if (context.player().isCreative()) {
                            context.enqueueWork(() -> {
                                Slot unbundleSlot = context.player().containerMenu.getSlot(payload.slotId);
                                BundleHelper.unbundleItem(context.player(), payload.unbundleStack, unbundleSlot);
                            });
                        }
                    }
            );
        }

        public static void sendPacket(int slotID, ItemStack unBundleStack) {
            ClientPacketDistributor.sendToServer(new UnbundlePacket(slotID, unBundleStack));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return QUICK_UNBUNDLE_PACKET_ID;
        }
    }
}

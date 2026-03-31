package net.kyrptonaught.quickshulker.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenShulkerPacket(int invSlot) implements CustomPacketPayload {

    public static final Identifier OPEN_SHULKER_PACKET = Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "open_shulker_packet");

    public static final Type<OpenShulkerPacket> OPEN_SHULKER_PACKET_ID = new Type<>(OPEN_SHULKER_PACKET);

    public static final StreamCodec<FriendlyByteBuf, OpenShulkerPacket> CODEC = StreamCodec.ofMember((value, buf) -> buf.writeInt(value.invSlot), buf -> new OpenShulkerPacket(buf.readInt()));

    public static void registerReceivePacket() {
        PayloadTypeRegistry.serverboundPlay().register(OpenShulkerPacket.OPEN_SHULKER_PACKET_ID, OpenShulkerPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenShulkerPacket.OPEN_SHULKER_PACKET_ID, OpenShulkerPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenShulkerPacket.OPEN_SHULKER_PACKET_ID, (payload, context) -> context.server().execute(() -> Util.openItem(context.player(), payload.invSlot)));
    }

    @Environment(EnvType.CLIENT)
    public static void sendOpenPacket(int invSlot) {
        ClientPlayNetworking.send(new OpenShulkerPacket(invSlot));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return OPEN_SHULKER_PACKET_ID;
    }
}
package net.kyrptonaught.quickshulker.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenShulkerPacket(int invSlot) implements CustomPayload {

    public static final Identifier OPEN_SHULKER_PACKET = Identifier.of(QuickShulkerMod.MOD_ID, "open_shulker_packet");

    public static final Id<OpenShulkerPacket> OPEN_SHULKER_PACKET_ID = new Id<>(OPEN_SHULKER_PACKET);

    public static final PacketCodec<PacketByteBuf, OpenShulkerPacket> CODEC = PacketCodec.of((value, buf) -> buf.writeInt(value.invSlot), buf -> new OpenShulkerPacket(buf.readInt()));

    public static void registerReceivePacket() {
        PayloadTypeRegistry.playC2S().register(OpenShulkerPacket.OPEN_SHULKER_PACKET_ID, OpenShulkerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenShulkerPacket.OPEN_SHULKER_PACKET_ID, OpenShulkerPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenShulkerPacket.OPEN_SHULKER_PACKET_ID, (payload, context) -> context.server().execute(() -> Util.openItem(context.player(), payload.invSlot)));
    }

    @Environment(EnvType.CLIENT)
    public static void sendOpenPacket(int invSlot) {
        ClientPlayNetworking.send(new OpenShulkerPacket(invSlot));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return OPEN_SHULKER_PACKET_ID;
    }
}
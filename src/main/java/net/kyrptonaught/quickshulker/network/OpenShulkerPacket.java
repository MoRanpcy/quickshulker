package net.kyrptonaught.quickshulker.network;

import net.kyrptonaught.quickshulker.QuickShulker;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record OpenShulkerPacket(int invSlot) implements CustomPacketPayload {

    public static final Identifier OPEN_SHULKER_PACKET = Identifier.fromNamespaceAndPath(QuickShulker.MOD_ID, "open_shulker_packet");

    public static final Type<OpenShulkerPacket> OPEN_SHULKER_PACKET_ID = new Type<>(OPEN_SHULKER_PACKET);

    public static final StreamCodec<FriendlyByteBuf, OpenShulkerPacket> CODEC = StreamCodec.ofMember((value, buf) -> buf.writeInt(value.invSlot), buf -> new OpenShulkerPacket(buf.readInt()));

    @SubscribeEvent
    public static void registerReceivePacket(RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);
        registrar.playToServer(
                OPEN_SHULKER_PACKET_ID,
                CODEC,
                (payload, context) -> Util.openItem(context.player(), payload.invSlot)
        );
    }

    public static void sendOpenPacket(int invSlot) {
        ClientPacketDistributor.sendToServer(new OpenShulkerPacket(invSlot));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return OPEN_SHULKER_PACKET_ID;
    }
}
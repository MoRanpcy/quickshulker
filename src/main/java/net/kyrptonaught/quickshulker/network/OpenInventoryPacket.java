package net.kyrptonaught.quickshulker.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class OpenInventoryPacket implements CustomPacketPayload {

    public static final Identifier OPEN_INV = Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "open_inv");

    public static final Type<OpenInventoryPacket> OPEN_INV_ID = new CustomPacketPayload.Type<>(OPEN_INV);

    public static final StreamCodec<FriendlyByteBuf, OpenInventoryPacket> CODEC = StreamCodec.ofMember(OpenInventoryPacket::write, buf -> new OpenInventoryPacket());

    private void write(FriendlyByteBuf buf) {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return OPEN_INV_ID;
    }

    public static void send(ServerPlayer player) {
        ServerPlayNetworking.send(player, new OpenInventoryPacket());
    }

}

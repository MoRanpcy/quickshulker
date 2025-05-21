package net.kyrptonaught.quickshulker.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OpenInventoryPacket implements CustomPayload {

    public static final Identifier OPEN_INV = Identifier.of(QuickShulkerMod.MOD_ID, "open_inv");

    public static final Id<OpenInventoryPacket> OPEN_INV_ID = new CustomPayload.Id<>(OPEN_INV);

    public static final PacketCodec<PacketByteBuf, OpenInventoryPacket> CODEC = PacketCodec.of(OpenInventoryPacket::write, buf -> new OpenInventoryPacket());

    private void write(PacketByteBuf buf) {
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return OPEN_INV_ID;
    }

    public static void send(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new OpenInventoryPacket());
    }

}

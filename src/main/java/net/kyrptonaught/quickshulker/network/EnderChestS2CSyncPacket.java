package net.kyrptonaught.quickshulker.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.util.PacketUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class EnderChestS2CSyncPacket {

    public record S2CEChestContentPacket(List<ItemStack> itemStacks) implements CustomPacketPayload {

        public static final Type<S2CEChestContentPacket> S2C_ECHEST_CONTENT_PACKET_ID = new Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "s2c_echest_content_packet"));
        public static final StreamCodec<RegistryFriendlyByteBuf, S2CEChestContentPacket> CODEC = StreamCodec.composite(ItemStack.OPTIONAL_LIST_STREAM_CODEC, S2CEChestContentPacket::itemStacks, S2CEChestContentPacket::new);

        public static void send(ServerPlayer player, List<ItemStack> itemStacks) {
            ServerPlayNetworking.send(player, new S2CEChestContentPacket(itemStacks));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return S2C_ECHEST_CONTENT_PACKET_ID;
        }
    }

    public record S2CEChestSlotPacket(int slotId, ItemStack itemStack) implements CustomPacketPayload{

        public static final Type<S2CEChestSlotPacket> S2C_ECHEST_SLOT_PACKET_ID = new Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "s2c_echest_slot_packet"));
        public static final StreamCodec<RegistryFriendlyByteBuf, S2CEChestSlotPacket> CODEC = StreamCodec.ofMember(
                (value, buf) -> {
                    buf.writeInt(value.slotId);
                    PacketUtils.writeItemStack(buf, value.itemStack);},
                buf -> new S2CEChestSlotPacket(buf.readInt(), PacketUtils.readItemStack(buf)));

        public static void send(ServerPlayer player, int slotId, ItemStack itemStack) {
            ServerPlayNetworking.send(player, new S2CEChestSlotPacket(slotId, itemStack));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return S2C_ECHEST_SLOT_PACKET_ID;
        }
    }
}

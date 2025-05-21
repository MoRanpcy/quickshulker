package net.kyrptonaught.quickshulker.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.EnderChestSyncHandler;
import net.kyrptonaught.quickshulker.api.PacketUtils;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class EnderChestS2CSyncPacket {

    public static void registerReceivePacket(){
        S2CEChestContentPacket.registerReceivePacket();
        S2CEChestSlotPacket.registerReceivePacket();
    }

    public record S2CEChestContentPacket(List<ItemStack> itemStacks) implements CustomPayload {

        public static final Id<S2CEChestContentPacket> S2C_ECHEST_CONTENT_PACKET_ID = new Id<>(Identifier.of(QuickShulkerMod.MOD_ID, "s2c_echest_content_packet"));
        public static final PacketCodec<RegistryByteBuf, S2CEChestContentPacket> CODEC = PacketCodec.tuple(ItemStack.OPTIONAL_LIST_PACKET_CODEC, S2CEChestContentPacket::itemStacks, S2CEChestContentPacket::new);

        public static void registerReceivePacket() {
            PayloadTypeRegistry.playS2C().register(S2CEChestContentPacket.S2C_ECHEST_CONTENT_PACKET_ID, S2CEChestContentPacket.CODEC);
            ClientPlayNetworking.registerGlobalReceiver(S2CEChestContentPacket.S2C_ECHEST_CONTENT_PACKET_ID, (payload, context) -> {
                context.client().execute(() -> {
                    EnderChestSyncHandler.setEnderChestContent(context.player(), payload.itemStacks);
                });
            });
        }

        public static void send(ServerPlayerEntity player, List<ItemStack> itemStacks) {
            ServerPlayNetworking.send(player, new S2CEChestContentPacket(itemStacks));
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return S2C_ECHEST_CONTENT_PACKET_ID;
        }
    }

    public record S2CEChestSlotPacket(int slotId, ItemStack itemStack) implements CustomPayload{

        public static final Id<S2CEChestSlotPacket> S2C_ECHEST_SLOT_PACKET_ID = new Id<>(Identifier.of(QuickShulkerMod.MOD_ID, "s2c_echest_slot_packet"));
        public static final PacketCodec<RegistryByteBuf, S2CEChestSlotPacket> CODEC = PacketCodec.of(
                (value, buf) -> {
                    buf.writeInt(value.slotId);
                    PacketUtils.writeItemStack(buf, value.itemStack);},
                buf -> new S2CEChestSlotPacket(buf.readInt(), PacketUtils.readItemStack(buf)));

        public static void registerReceivePacket(){
            PayloadTypeRegistry.playS2C().register(S2CEChestSlotPacket.S2C_ECHEST_SLOT_PACKET_ID, S2CEChestSlotPacket.CODEC);
            ClientPlayNetworking.registerGlobalReceiver(S2CEChestSlotPacket.S2C_ECHEST_SLOT_PACKET_ID, (payload, context) -> {
                context.client().execute(() -> {
                    EnderChestInventory enderChestInventory = context.player().getEnderChestInventory();
                    enderChestInventory.setStack(payload.slotId, payload.itemStack);
                });
            });
        }

        public static void send(ServerPlayerEntity player, int slotId, ItemStack itemStack) {
            ServerPlayNetworking.send(player, new S2CEChestSlotPacket(slotId, itemStack));
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return S2C_ECHEST_SLOT_PACKET_ID;
        }
    }
}

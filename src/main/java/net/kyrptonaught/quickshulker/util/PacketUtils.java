package net.kyrptonaught.quickshulker.util;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;

public class PacketUtils{

    public static void writeItemStack(RegistryByteBuf buf, ItemStack itemStack) {
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, itemStack);
    }

    public static ItemStack readItemStack(RegistryByteBuf buf) {
        return ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
    }

}

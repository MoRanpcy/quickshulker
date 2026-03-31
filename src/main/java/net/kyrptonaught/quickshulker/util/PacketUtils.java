package net.kyrptonaught.quickshulker.util;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class PacketUtils{

    public static void writeItemStack(RegistryFriendlyByteBuf buf, ItemStack itemStack) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemStack);
    }

    public static ItemStack readItemStack(RegistryFriendlyByteBuf buf) {
        return ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    }

}

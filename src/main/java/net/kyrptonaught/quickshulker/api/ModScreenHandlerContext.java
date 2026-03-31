package net.kyrptonaught.quickshulker.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.BiFunction;

public class ModScreenHandlerContext implements ScreenHandlerContext {
    public ItemStack stack;
    public PlayerEntity player;
    public World world;
    public BlockPos pos;

    public ModScreenHandlerContext(PlayerEntity player, ItemStack itemStack){
        this.stack = itemStack;
        this.player = player;
        this.world = player.getWorld();
        this.pos = player.getBlockPos();
    }

    @Override
    public <T> Optional<T> get(BiFunction<World, BlockPos, T> getter) {
        return Optional.of(getter.apply(world, pos));
    }
}

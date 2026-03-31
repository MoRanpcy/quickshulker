package net.kyrptonaught.quickshulker.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.BiFunction;

public class ModContainerLevelAccess implements ContainerLevelAccess {
    public ItemStack stack;
    public Player player;
    public Level level;
    public BlockPos pos;

    public ModContainerLevelAccess(Player player, ItemStack itemStack){
        this.stack = itemStack;
        this.player = player;
        this.level = player.level();
        this.pos = player.blockPosition();
    }

    @Override
    public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> getter) {
        return Optional.of(getter.apply(level, pos));
    }
}

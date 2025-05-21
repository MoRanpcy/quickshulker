package net.kyrptonaught.quickshulker.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class QuickOpenableRegistry {
    private static final HashMap<Class<? extends ItemConvertible>, QuickShulkerData> quickies = new HashMap<>();

    public static QuickShulkerData getQuickie(ItemConvertible item) {
        if (item instanceof BlockItem) {
            if (quickies.containsKey(((BlockItem) item).getBlock().getClass()))
                return quickies.get(((BlockItem) item).getBlock().getClass());
        }
        return quickies.get(item.getClass());
    }

    public static void register(Class<? extends ItemConvertible> quickItem, QuickShulkerData quickShulkerData) {
        quickies.put(quickItem, quickShulkerData);
    }

    @Deprecated
    public static void register(Class<? extends ItemConvertible> quickItem, Boolean requiresSingularStack, Boolean supportsBundleing, BiConsumer<PlayerEntity, ItemStack> consumer) {
        register(quickItem, new QuickShulkerData(consumer, supportsBundleing));
    }

    @Deprecated
    public static void register(Class<? extends ItemConvertible> quickItem, Boolean supportsBundleing, BiConsumer<PlayerEntity, ItemStack> consumer) {
        register(quickItem, new QuickShulkerData(consumer, supportsBundleing));
    }

    @Deprecated
    public static void register(Class<? extends ItemConvertible> quickItem, BiConsumer<PlayerEntity, ItemStack> consumer) {
        register(quickItem, new QuickShulkerData(consumer, false));
    }

    @SafeVarargs
    @Deprecated
    public static void register(BiConsumer<PlayerEntity, ItemStack> consumer, Class<? extends ItemConvertible>... quickItems) {
        for (Class<? extends ItemConvertible> block : quickItems) {
            register(block, consumer);
        }
    }

    public static class Builder {
        private final List<Class<? extends ItemConvertible>> quickItems = new ArrayList<>();
        private final QuickShulkerData qsdata;

        public Builder() {
            qsdata = new QuickShulkerData();
        }

        public Builder(QuickShulkerData qsdata) {
            this.qsdata = qsdata;
        }

        public void register() {
            for (Class<? extends ItemConvertible> quickItem : quickItems)
                QuickOpenableRegistry.register(quickItem, qsdata);
        }

        @SafeVarargs
        public final Builder setItem(Class<? extends ItemConvertible>... quickItems) {
            this.quickItems.addAll(List.of(quickItems));
            return this;
        }

        public Builder setOpenAction(BiConsumer<PlayerEntity, ItemStack> openAction) {
            qsdata.openConsumer = openAction;
            return this;
        }

        public Builder supportsBundleing(Boolean supportsBundleing) {
            qsdata.supportsBundleing = supportsBundleing;
            return this;
        }

        public Builder getBundleInv(BiFunction<PlayerEntity, ItemStack, Inventory> getBundleInv) {
            qsdata.bundleInvGetter = getBundleInv;
            return this;
        }

        public Builder canBundleInsertItem(CanBundleInsertItemFunction canBundleInsertItem) {
            qsdata.canBundleInsertItem = canBundleInsertItem;
            return this;
        }

        public Builder canOpenInHand(boolean canOpenInHand) {
            qsdata.canOpenInHand = canOpenInHand;
            return this;
        }

        public Builder ignoreSingleStackCheck(Boolean ignoreSingleStackCheck) {
            qsdata.ignoreSingleStackCheck = ignoreSingleStackCheck;
            return this;
        }
    }
}
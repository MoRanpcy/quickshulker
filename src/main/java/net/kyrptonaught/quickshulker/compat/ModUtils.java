package net.kyrptonaught.quickshulker.compat;

import net.fabricmc.loader.api.FabricLoader;

public class ModUtils {

    public static boolean isModLoad(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

}

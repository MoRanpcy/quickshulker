package net.kyrptonaught.quickshulker.compat;

import net.neoforged.fml.ModList;

public class ModUtils {

    public static boolean isModLoad(String modId) {
        return ModList.get().isLoaded(modId);
    }

}

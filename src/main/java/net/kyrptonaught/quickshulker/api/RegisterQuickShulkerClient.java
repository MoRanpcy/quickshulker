package net.kyrptonaught.quickshulker.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RegisterQuickShulkerClient {
    void registerClient();
}
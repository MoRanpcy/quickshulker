package net.kyrptonaught.quickshulker.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.config.ModConfigMenu;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModConfigMenu::getModConfigMenu;
    }
}
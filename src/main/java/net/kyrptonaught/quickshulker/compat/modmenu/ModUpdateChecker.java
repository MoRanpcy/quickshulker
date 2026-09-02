package net.kyrptonaught.quickshulker.compat.modmenu;

import com.terraformersmc.modmenu.api.UpdateChannel;
import com.terraformersmc.modmenu.api.UpdateInfo;
import net.kyrptonaught.quickshulker.util.update.UpdateChecker;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ModUpdateChecker {
    public static @Nullable UpdateInfo checkForUpdates(){
        UpdateChecker.UpdateInfo updateInfo = UpdateChecker.checkForUpdates(true);
        if(updateInfo == null) return null;
        return new UpdateInfo(){
            @Override
            public boolean isUpdateAvailable() {
                return updateInfo.isUpdateAvailable();
            }

            @Override
            public @Nullable Text getUpdateMessage() {
                return Text.translatable("key.quickshulker.update.check").append(" ").append(Text.translatable("key.quickshulker.update.version", updateInfo.lastVersion));
            }

            @Override
            public String getDownloadLink() {
                return updateInfo.getDownloadLink();
            }

            @Override
            public UpdateChannel getUpdateChannel() {
                return UpdateChannel.RELEASE;
            }
        };
    }
}

package net.kyrptonaught.kyrptconfig.keybinding;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;

public class DisplayOnlyKeyBind extends KeyMapping {
    private CustomKeyBinding customKeyBinding;
    private final Consumer<InputConstants.Key> keySet;

    public DisplayOnlyKeyBind(String translationKey, InputConstants.Type type, int code, KeyMapping.Category category) {
        super(translationKey, type, code, category);
        keySet = (boundKey) -> {
        };
    }

    public DisplayOnlyKeyBind(String translationKey, KeyMapping.Category category, CustomKeyBinding customKeyBinding, Consumer<InputConstants.Key> keySet) {
        super(translationKey, customKeyBinding.getDefaultKey().getType(), customKeyBinding.getDefaultKey().getValue(), category);
        this.customKeyBinding = customKeyBinding;
        this.keySet = keySet;
        updateSetKey();
    }

    public void setKey(InputConstants.Key boundKey) {
        super.setKey(boundKey);
        if (customKeyBinding != null)
            customKeyBinding.setRaw(saveString());
        keySet.accept(boundKey);
    }

    public void updateSetKey() {
        super.setKey(customKeyBinding.getKeybinding().orElse(InputConstants.UNKNOWN));
    }

    @Override
    public KeyMapping.Category getCategory() {
        updateSetKey();
        return super.getCategory();
    }

    @Override
    public String getName() {
        updateSetKey();
        return super.getName();
    }

    @Override
    public InputConstants.Key getDefaultKey() {
        updateSetKey();
        return super.getDefaultKey();
    }
}

package net.kyrptonaught.kyrptconfig.keybinding;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.function.Consumer;

public class DisplayOnlyKeyBind extends KeyBinding {
    private CustomKeyBinding customKeyBinding;
    private final Consumer<InputUtil.Key> keySet;

    public DisplayOnlyKeyBind(String translationKey, InputUtil.Type type, int code, String category) {
        super(translationKey, type, code, category);
        keySet = (boundKey) -> {
        };
    }

    public DisplayOnlyKeyBind(String translationKey, String category, CustomKeyBinding customKeyBinding, Consumer<InputUtil.Key> keySet) {
        super(translationKey, customKeyBinding.getDefaultKey().getCategory(), customKeyBinding.getDefaultKey().getCode(), category);
        this.customKeyBinding = customKeyBinding;
        this.keySet = keySet;
        updateSetKey();
    }

    public void setBoundKey(InputUtil.Key boundKey) {
        super.setBoundKey(boundKey);
        if (customKeyBinding != null)
            customKeyBinding.setRaw(getBoundKeyTranslationKey());
        keySet.accept(boundKey);
    }

    public void updateSetKey() {
        super.setBoundKey(customKeyBinding.getKeybinding().orElse(InputUtil.UNKNOWN_KEY));
    }

    @Override
    public String getCategory() {
        updateSetKey();
        return super.getCategory();
    }

    @Override
    public String getTranslationKey() {
        updateSetKey();
        return super.getTranslationKey();
    }

    @Override
    public InputUtil.Key getDefaultKey() {
        updateSetKey();
        return super.getDefaultKey();
    }
}

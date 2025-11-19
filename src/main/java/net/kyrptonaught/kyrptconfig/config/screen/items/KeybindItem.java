package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.kyrptonaught.kyrptconfig.api.ConflictHandler;
import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.kyrptonaught.quickshulker.event.KeyBindingRegister;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class KeybindItem extends ConfigItem<String> {
    private final NotSuckyButton keyButton;
    private Boolean isListening = false;
    private boolean duplicate = false;

    public KeybindItem(Text name, String key, String defaultKey) {
        super(name, key, defaultKey);
        this.keyButton = new NotSuckyButton(0, 0, 100, 20, getCleanName(key), widget -> {
            this.isListening = !this.isListening;
            updateMessage();
        });
        useDefaultResetBTN();
        ConflictHandler.updateMap(this);
    }

    public void setValue(String value) {
        super.setValue(value);
        isListening = false;
        ConflictHandler.updateCustomConflicts();
    }

    public MutableText getCleanName(String str) {
        if (I18n.hasTranslation(value))
            return Text.translatable(str);
        if (str == null || str.isBlank() || str.isEmpty())
            return Text.translatable("key.keyboard.unknown");
        return Text.literal(str.substring(str.length() - 1).toUpperCase());
    }

    public void updateMessage(){
        if(!isListening){
            duplicate = false;
            MutableText mutableText = Text.empty();
            if(isInvalidKeyValue()) {
                for (KeyBinding keyBinding : MinecraftClient.getInstance().options.allKeys) {
                    if(KeyBindingRegister.MAIN.equals(keyBinding.getCategory())){
                        continue;
                    }
                    Text t1 = Text.translatable(keyBinding.getTranslationKey());
                    Text t2 = this.getTitleText();
                    if (!t1.equals(t2) && keyBinding.getBoundKeyTranslationKey().equals(this.value)) {
                        duplicate = true;
                        mutableText.append("\n  - ").append(Text.translatable(keyBinding.getTranslationKey()));
                    }
                }
                for (KeybindItem item : ConflictHandler.CUSTOM_KEYBIND_ITEMS) {
                    if (item != this && this.value.equals(item.value)) {
                        duplicate = true;
                        mutableText.append("\n  - ").append(item.getTitleText());
                    }
                }
            }
            if(duplicate){
                keyButton.setMessage(Text.literal("[ ").append(getCleanName(this.value).formatted(Formatting.WHITE)).append(Text.literal(" ]")).formatted(Formatting.RED));
                keyButton.setTooltip(Tooltip.of(Text.translatable("key.quickshulker.config.savedValue", Text.literal(this.value)).append(Text.translatable("key.quickshulker.config.keybindinsConflict", mutableText))));
            }else{
                keyButton.setMessage(this.getCleanName(this.value));
                keyButton.setTooltip(Tooltip.of(Text.translatable("key.quickshulker.config.savedValue", Text.literal(this.value))));
            }
        }else{
            keyButton.setMessage(Text.literal("> ").append(getCleanName(this.value)).append(Text.literal(" <")));
        }
    }

    private boolean isInvalidKeyValue(){
        return this.value != null && !this.value.isEmpty() && !this.value.isBlank() && !this.value.equals("key.keyboard.unknown");
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isListening) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                setValue("");;
                return true;
            }
            setValue(InputUtil.fromKeyCode(keyCode, scanCode).getTranslationKey());
            return true;
        }
        return false;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        boolean handled;
        handled = (keyButton.mouseClicked(mouseX, mouseY, button) || resetButton.mouseClicked(mouseX, mouseY, button));
        if (isListening && !handled) {
            setValue(InputUtil.Type.MOUSE.createFromCode(button).getTranslationKey());
        }
    }

    @Override
    public void render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(context, x, y, mouseX, mouseY, delta);
        this.keyButton.setY(y);

        this.keyButton.setX(resetButton.getX() - resetButton.getWidth() - (keyButton.getWidth() / 2) - 20);

        keyButton.render(context, mouseX, mouseY, delta);

        if(duplicate){
            int m = keyButton.getX() - 6;
            context.fill(m, y, m + 3, keyButton.getBottom(), -65536);
        }
    }
}
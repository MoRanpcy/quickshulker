package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeybindItem extends ConfigItem<String> {
    private final NotSuckyButton keyButton;
    private Boolean isListening = false;

    public KeybindItem(Text name, String key, String defaultKey) {
        super(name, key, defaultKey);
        this.keyButton = new NotSuckyButton(0, 0, 100, 20, getCleanName(key), widget -> {
            this.isListening = !this.isListening;
            if (!this.isListening) {
                widget.setMessage(this.getCleanName(this.value));
                widget.setTooltip(Tooltip.of(Text.literal(this.value)));
            } else {
                widget.setMessage(Text.literal("> ").append(this.getCleanName(this.value).append(Text.literal(" <"))));
            }
        });
        keyButton.setTooltip(Tooltip.of(Text.literal(this.value)));
        useDefaultResetBTN();
    }

    public void setValue(String value) {
        super.setValue(value);
        isListening = false;
        keyButton.setMessage(this.getCleanName(this.value));
        keyButton.setTooltip(Tooltip.of(Text.literal(this.value)));
    }

    public MutableText getCleanName(String str) {
        if (I18n.hasTranslation(value))
            return Text.translatable(str);
        if (str == null || str.isBlank() || str.isEmpty())
            return Text.translatable("key.keyboard.unknown");
        return Text.literal(str.substring(str.length() - 1).toUpperCase());
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (isListening) {
            if (input.getKeycode() == GLFW.GLFW_KEY_ESCAPE) {
                setValue(value);
                return true;
            }
            setValue(InputUtil.fromKeyCode(input).getTranslationKey());
            return true;
        }
        return false;
    }

    @Override
    public void mouseClicked(Click click, boolean doubled) {
        super.mouseClicked(click, doubled);
        boolean handled;
        handled = (keyButton.mouseClicked(click, doubled) || resetButton.mouseClicked(click, doubled));
        if (isListening && !handled) {
            setValue(InputUtil.Type.MOUSE.createFromCode(click.button()).getTranslationKey());
        }
    }

    @Override
    public void render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
        super.render(context, x, y, mouseX, mouseY, delta);
        this.keyButton.setY(y);

        this.keyButton.setX(resetButton.getX() - resetButton.getWidth() - (keyButton.getWidth() / 2) - 20);

        keyButton.render(context, mouseX, mouseY, delta);
    }
}
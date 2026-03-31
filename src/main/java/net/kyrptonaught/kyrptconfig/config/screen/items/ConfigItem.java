package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ConfigItem<T> {
    private Component fieldTitle;
    private List<Component> toolTipText;
    protected Consumer<T> saveConsumer, valueUpdatedEvent;
    protected NotSuckyButton resetButton;
    protected T value, defaultValue;
    private boolean requiresRestart = false;
    private boolean isHidden = false;

    public ConfigItem(Component name, T value, T defaultValue) {
        this.fieldTitle = name;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public ConfigItem<?> setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public ConfigItem<?> setValueUpdatedEvent(Consumer<T> valueUpdatedEvent) {
        this.valueUpdatedEvent = valueUpdatedEvent;
        return this;
    }

    public ConfigItem<?> setRequiresRestart() {
        requiresRestart = true;
        ((MutableComponent) fieldTitle).append(" *");
        return this;
    }

    public Component getTitleText(){
        return fieldTitle;
    }

    public ConfigItem<?> setTitleText(Component title) {
        this.fieldTitle = title;
        return this;
    }

    public ConfigItem<?> setToolTipWithNewLine(String translatableKey) {
        String[] translated = Language.getInstance().getOrDefault(translatableKey).split("\n");
        this.toolTipText = new ArrayList<>();
        for (String line : translated) {
            this.toolTipText.add(Component.literal(line));
        }

        return this;
    }

    public ConfigItem<?> setToolTip(Component toolTip) {
        this.toolTipText = List.of(toolTip);
        return this;
    }

    public ConfigItem<?> setToolTip(Component... toolTips) {
        this.toolTipText = List.of(toolTips);
        return this;
    }

    public ConfigItem<?> setHidden(boolean hidden) {
        this.isHidden = hidden;
        return this;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean requiresRestart() {
        return requiresRestart;
    }

    protected void runSaveConsumer(T value) {
        if (saveConsumer != null && value != null)
            saveConsumer.accept(value);
    }

    public void save() {
        runSaveConsumer(value);
    }

    public int getSize() {
        if (isHidden) return 0;
        return getHeaderSize() + getContentSize();
    }

    public int getHeaderSize() {
        return 20;
    }

    public int getContentSize() {
        return 0;
    }

    public void useDefaultResetBTN() {
        this.resetButton = new NotSuckyButton(0, 0, 35, 20, Component.translatable("key.kyrptconfig.config.reset"), widget -> {
            resetToDefault();
        });
    }

    public void resetToDefault() {
        setValue(defaultValue);
    }

    public boolean isValueDefault() {
        return value.equals(defaultValue);
    }

    public void setValue(T value) {
        this.value = value;
        if (valueUpdatedEvent != null)
            valueUpdatedEvent.accept(this.value);
    }

    public void tick() {
    }

    public void mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (isHidden) return;
        if (resetButton != null)
            resetButton.mouseClicked(click, doubled);
    }

    public boolean charTyped(CharacterEvent input) {
        return false;
    }

    public boolean keyPressed(KeyEvent input) {
        return false;
    }

    public void extractRenderState(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY, float delta) {
        if (isHidden) return;

        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = y + getHeaderSize();
        if (mouseY > y && mouseY < height)
            context.fill(0, y - 1, width, height + 1, ARGB.color(50, 255, 255, 255));

        context.text(Minecraft.getInstance().font, this.fieldTitle, x, y + 6, -1, true);

        if (resetButton != null) {
            this.resetButton.setY(y);
            this.resetButton.setX(width - resetButton.getWidth() - 20);
            resetButton.active = !isValueDefault();
            resetButton.extractRenderState(context, mouseX, mouseY, delta);
        }

    }

    public void extractRenderState2(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY, float delta) {
        if (isHidden) return;
        if (mouseX > x && mouseX < x + Minecraft.getInstance().font.width(fieldTitle) &&
                mouseY > y && mouseY < y + 12)
            renderToolTip(context, mouseX, mouseY);
    }

    public void renderToolTip(GuiGraphicsExtractor context, int x, int y) {
        if (toolTipText != null && requiresRestart) {
            List<Component> newList = new ArrayList<>(toolTipText);
            newList.add(Component.translatable("key.kyrptconfig.config.restartRequired"));
            context.setComponentTooltipForNextFrame(Minecraft.getInstance().font, newList, x, y);
        } else if (toolTipText != null)
            context.setComponentTooltipForNextFrame(Minecraft.getInstance().font, toolTipText, x, y);
        else if (requiresRestart) {
            context.setTooltipForNextFrame(Minecraft.getInstance().font, Component.translatable("key.kyrptconfig.config.restartRequired"), x, y);
        }
    }
}

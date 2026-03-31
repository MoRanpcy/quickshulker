package net.kyrptonaught.kyrptconfig.config.screen;

import net.kyrptonaught.kyrptconfig.config.screen.items.ConfigItem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfigSection extends Screen {

    Component title;
    public List<ConfigItem<?>> configs = new CopyOnWriteArrayList<>();
    public NotSuckyButton sectionSelectionBTN;
    int selectionIndex = 0;
    int scrollOffset = 0;

    public ConfigSection(ConfigScreen configScreen, Component title) {
        super(title);
        this.title = title;
        this.sectionSelectionBTN = new NotSuckyButton(0, 32, 10, 20, title, widget -> {
            configScreen.setSelectedSection(selectionIndex);
        });
        configScreen.addConfigSection(this);
    }

    public void save() {
        for (ConfigItem<?> configItem : configs) {
            configItem.save();
        }
    }

    public int getTotalSectionSize() {
        int size = configs.size() * 3 + 5;
        for (ConfigItem<?> configItem : configs) {
            size += configItem.getSize();
        }
        return size;
    }

    public ConfigItem<?> addConfigItem(ConfigItem<?> item) {
        this.configs.add(item);
        return item;
    }

    public ConfigItem<?> insertConfigItem(ConfigItem<?> item, int slot) {
        this.configs.add(slot, item);
        return item;
    }

    public ConfigItem<?> removeConfigItem(int slot) {
        return this.configs.remove(slot);
    }

    @Override
    public void tick() {
        super.tick();
        for (ConfigItem<?> configItem : configs) {
            configItem.tick();
        }
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        for (ConfigItem<?> configItem : configs) {
            if (configItem.keyPressed(input))
                return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        for (ConfigItem<?> configItem : configs) {
            if (configItem.charTyped(input))
                return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        for (ConfigItem<?> configItem : configs) {
            configItem.mouseClicked(click, doubled);
        }
        mouseScrolled(click.x(), click.y(), 0,0); // update scroll if option changes screen size
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = Mth.clamp(scrollOffset + (int) (verticalAmount * 15), -calculateSectionHeight(), 0);
        return true;
    }

    public int calculateSectionHeight() {
        int visibleHeight = this.height;
        int sectionSize = getTotalSectionSize();
        if (sectionSize <= visibleHeight) return 0;
        return sectionSize - visibleHeight;
    }

    public void extractRenderState(GuiGraphicsExtractor context, int startY, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        int runningY = scrollOffset + startY + 5;
        for (ConfigItem<?> configItem : configs) {
            // if (runningY + configItem.getSize() > 55 && runningY < 55 + height)
            configItem.extractRenderState(context, 20, runningY, mouseX, mouseY, delta);
            runningY += configItem.getSize() + 3;
        }

    }

    public void extractRenderState2(GuiGraphicsExtractor context, int startY, int mouseX, int mouseY, float delta) {
        int runningY = scrollOffset + startY + 5;
        for (ConfigItem<?> configItem : configs) {
            configItem.extractRenderState2(context, 20, runningY, mouseX, mouseY, delta);
            runningY += configItem.getSize() + 3;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    }
}
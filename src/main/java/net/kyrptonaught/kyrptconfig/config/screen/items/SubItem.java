package net.kyrptonaught.kyrptconfig.config.screen.items;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class SubItem<E> extends ConfigItem<E> {
    protected boolean expanded = false;
    protected int subStart = 0;
    protected List<ConfigItem<?>> configs = new ArrayList<>();

    public SubItem(Component name, boolean isExpanded) {
        super(name, null, null);
        this.expanded = isExpanded;
    }

    public SubItem(Component name) {
        this(name, false);
    }

    public boolean requiresRestart() {
        for (ConfigItem<?> item : configs) {
            if (item.requiresRestart())
                return true;
        }
        return super.requiresRestart();
    }

    public void save() {
        for (ConfigItem<?> item : configs)
            item.save();
        super.save();
    }

    public boolean isValueDefault() {
        for (ConfigItem<?> item : configs) {
            if (!item.isValueDefault())
                return false;
        }
        return true;
    }

    public void tick() {
        for (ConfigItem<?> item : configs) {
            if (item.isHidden()) continue;
            item.tick();
        }
    }

    @Override
    public void mouseClicked(MouseButtonEvent click, boolean doubled) {
        super.mouseClicked(click, doubled);
        if (!isHidden() && click.y() > subStart && click.y() < subStart + 20)
            expanded = !expanded;

        if (expanded && !isHidden()) {
            for (ConfigItem<?> item : configs) {
                if (item.isHidden()) continue;
                item.mouseClicked(click, doubled);
            }
        }
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if (expanded && !isHidden()) {
            for (ConfigItem<?> item : configs) {
                if (item.isHidden()) continue;
                if (item.charTyped(input))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (expanded && !isHidden()) {
            for (ConfigItem<?> item : configs) {
                if (item.isHidden()) continue;
                if (item.keyPressed(input))
                    return true;
            }
        }
        return false;
    }

    public int getContentSize() {
        if (expanded && !isHidden()) {
            int size = 0;
            for (ConfigItem<?> item : configs) {
                if (item.isHidden()) continue;
                size += item.getSize() + 3;
            }
            return size;
        }
        return 0;
    }

    public void clearConfigItems() {
        configs.clear();
    }

    public ConfigItem<?> addConfigItem(ConfigItem<?> item) {
        this.configs.add(item);
        return item;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, x, y, mouseX, mouseY, delta);
        if (isHidden()) return;
        context.text(Minecraft.getInstance().font, expanded ? "-" : "+", x - 10, y + 5, -1, false);
        subStart = y;
        if (expanded) {
            int runningY = subStart + 23;
            for (ConfigItem<?> item : configs) {
                if (item.isHidden()) continue;
                item.extractRenderState(context, 30, runningY, mouseX, mouseY, delta);
                runningY += item.getSize() + 3;
            }
        }
    }

    @Override
    public void extractRenderState2(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY, float delta) {
        super.extractRenderState2(context, x, y, mouseX, mouseY, delta);
        if (isHidden()) return;

        if (expanded) {
            int runningY = y + 23;
            for (ConfigItem<?> item : configs) {
                if (item.isHidden()) continue;
                item.extractRenderState2(context, 30, runningY, mouseX, mouseY, delta);
                runningY += item.getSize() + 3;
            }
        }
    }
}

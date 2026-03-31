package net.kyrptonaught.kyrptconfig.config.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class NotSuckyButton extends Button {
    int buttonColor = -1;
    public boolean disableHover = false;
    private static final WidgetSprites TEXTURES = new WidgetSprites(Identifier.parse("widget/button"), Identifier.parse("widget/button_disabled"), Identifier.parse("widget/button_highlighted"));

    public NotSuckyButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    public void setButtonColor(int color) {
        this.setMessage(ComponentUtils.mergeStyles(this.getMessage(), Style.EMPTY.withColor(color)));
        this.buttonColor = color;
    }

    public boolean detectHover(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        if (disableHover) isHovered = false;

        this.extractDefaultSprite(context);
        this.extractDefaultLabel(context.textRenderer());
    }
}

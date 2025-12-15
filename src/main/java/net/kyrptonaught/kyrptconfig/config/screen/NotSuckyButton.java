package net.kyrptonaught.kyrptconfig.config.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;

public class NotSuckyButton extends ButtonWidget {
    int buttonColor = -1;
    public boolean disableHover = false;
    private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.of("widget/button"), Identifier.of("widget/button_disabled"), Identifier.of("widget/button_highlighted"));

    public NotSuckyButton(int x, int y, int width, int height, net.minecraft.text.Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    public void setButtonColor(int color) {
        this.setMessage(Texts.withStyle(this.getMessage(), Style.EMPTY.withColor(color)));
        this.buttonColor = color;
    }

    public boolean detectHover(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (disableHover) hovered = false;

        this.drawButton(context);
        this.drawLabel(context.getTextConsumer());
    }
}

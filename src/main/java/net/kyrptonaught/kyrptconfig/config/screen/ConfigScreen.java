package net.kyrptonaught.kyrptconfig.config.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    int selectedSection = 0;
    List<ConfigSection> sections = new ArrayList<>();
    private Runnable saveRunnable;
    Screen previousScreen;
    private NotSuckyButton scrollLeftBTN, scrollRightBTN;
    int horizontalScrollOffset = -1;
    private static final Identifier SCROLLER_TEXTURE = Identifier.of("widget/scroller");
    private static final Identifier OPTIONS_BACKGROUND_TEXTURE = Identifier.of("textures/block/dirt.png");

    public ConfigScreen(Screen previousScreen, Text title) {
        super(title);
        this.previousScreen = previousScreen;
    }

    protected void init() {
        int center = this.width / 2;
        this.addDrawableChild(new NotSuckyButton(center - 153, height - 25, 150, 20, Text.translatable("key.kyrptconfig.config.exit"), widget -> {
            this.client.setScreen(previousScreen);
        }));

        this.addDrawableChild(new NotSuckyButton(center + 3, height - 25, 150, 20, Text.translatable("key.kyrptconfig.config.saveExit"), widget -> {
            save();
            this.client.setScreen(previousScreen);
        }));
        for (ConfigSection section : sections) {
            section.init(client, width, height - 57 - 30);
        }

        adjustForHorizontalScroll(this.width);
    }

    public void setSavingEvent(Runnable save) {
        this.saveRunnable = save;
    }

    public void save() {
        for (ConfigSection section : sections) {
            section.save();
        }
        if (saveRunnable != null)
            saveRunnable.run();
    }

    public void addConfigSection(ConfigSection item) {
        item.selectionIndex = sections.size();
        if (sections.size() == 0)
            item.sectionSelectionBTN.setX(10);
        else
            item.sectionSelectionBTN.setX(sections.get(sections.size() - 1).sectionSelectionBTN.getX() + sections.get(sections.size() - 1).sectionSelectionBTN.getWidth() + 3);
        item.sectionSelectionBTN.setWidth(MinecraftClient.getInstance().textRenderer.getWidth(item.title) + 10);

        this.sections.add(item);
    }

    public boolean adjustForHorizontalScroll(int maxWidth) {
        NotSuckyButton lastBTN = sections.get(sections.size() - 1).sectionSelectionBTN;

        this.scrollLeftBTN = new NotSuckyButton(10, 32, 10, 20, Text.literal("<"), widget -> {
            NotSuckyButton nextBtn = sections.get(0).sectionSelectionBTN;
            for (int i = sections.size() - 1; i >= 0; i--) {
                if (sections.get(i).sectionSelectionBTN.getX() < scrollLeftBTN.getX() + scrollLeftBTN.getWidth() + 3) {
                    nextBtn = sections.get(i).sectionSelectionBTN;
                    if (i == 0)
                        scrollLeftBTN.active = false;
                    break;
                }
            }
            scrollRightBTN.active = true;
            horizontalScrollOffset += (nextBtn.getX()) - (scrollLeftBTN.getX() + scrollLeftBTN.getWidth() + 3);
        });

        this.scrollRightBTN = new NotSuckyButton(this.width - 20, 32, 10, 20, Text.literal(">"), widget -> {
            NotSuckyButton nextBtn = lastBTN;
            for (int i = 0; i < sections.size(); i++) {
                if (sections.get(i).sectionSelectionBTN.getX() + sections.get(i).sectionSelectionBTN.getWidth() + 3 > maxWidth) {
                    nextBtn = sections.get(i).sectionSelectionBTN;
                    if (i == sections.size() - 1)
                        scrollRightBTN.active = false;
                    break;
                }
            }
            scrollLeftBTN.active = true;
            horizontalScrollOffset += (nextBtn.getX() + nextBtn.getWidth() + 3) - (scrollRightBTN.getX());
        });

        if (lastBTN.getX() + lastBTN.getWidth() + 3 > maxWidth) {
            for (ConfigSection section : sections) {
                section.sectionSelectionBTN.setX(section.sectionSelectionBTN.getX() + scrollLeftBTN.getWidth() + 3);
            }

            scrollLeftBTN.visible = true;
            scrollRightBTN.visible = true;
            scrollLeftBTN.active = sections.getFirst().sectionSelectionBTN.getX() < scrollLeftBTN.getRight() + 3;
            scrollRightBTN.active = lastBTN.getRight() + 3 > scrollRightBTN.getX();
            horizontalScrollOffset = 0;
            return true;
        }

        scrollLeftBTN.active = scrollLeftBTN.visible = false;
        scrollRightBTN.active = scrollRightBTN.visible = false;
        horizontalScrollOffset = -1;
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        for (ConfigSection section : sections) {
            section.tick();
        }
    }

    public void setSelectedSection(int selectedSection) {
        this.selectedSection = selectedSection;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (sections.get(selectedSection).keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return sections.get(selectedSection).charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (scrollLeftBTN.mouseClicked(mouseX, mouseY, button) || scrollRightBTN.mouseClicked(mouseX, mouseY, button))
            return true;

        for (ConfigSection section : sections)
            if (section.sectionSelectionBTN.mouseClicked(mouseX, mouseY, button)) return true;

        return sections.get(selectedSection).mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return sections.get(selectedSection).mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);

        ConfigSection section = sections.get(selectedSection);

        context.enableScissor(0, 57, this.width, this.height - 30);
        context.fillGradient(0, 57, this.width, this.height, 1744830464, 1744830464);
        section.render(context, 57, mouseX, mouseY, deltaTicks);
        context.disableScissor();

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 13, 0xffffff);
        drawHeaderAndFooterSeparators(context);

        boolean noHover = scrollLeftBTN.detectHover(mouseX, mouseY) | scrollRightBTN.detectHover(mouseX, mouseY);
        if (horizontalScrollOffset > -1) {
            context.enableScissor(scrollLeftBTN.getRight() + 1, scrollLeftBTN.getY(), scrollRightBTN.getX() - 1, scrollRightBTN.getBottom());
            for (int i = 0; i < sections.size(); i++) {
                NotSuckyButton selectionBTN = sections.get(i).sectionSelectionBTN;
                selectionBTN.active = i != selectedSection;
                if (i == 0) {
                    selectionBTN.setX(scrollLeftBTN.getX() + scrollLeftBTN.getWidth() + 3 - horizontalScrollOffset);
                } else {
                    NotSuckyButton previousBTN = sections.get(i - 1).sectionSelectionBTN;
                    selectionBTN.setX(previousBTN.getX() + previousBTN.getWidth() + 3);
                }
                selectionBTN.disableHover = noHover;
                selectionBTN.render(context, mouseX, mouseY, deltaTicks);
            }
            context.disableScissor();
            scrollLeftBTN.render(context, mouseX, mouseY, deltaTicks);
            scrollRightBTN.render(context, mouseX, mouseY, deltaTicks);
        }else{
            for (int i = 0; i < sections.size(); i++) {
                NotSuckyButton selectionBTN = sections.get(i).sectionSelectionBTN;
                selectionBTN.active = i != selectedSection;
                selectionBTN.render(context, mouseX, mouseY, deltaTicks);
            }
        }

        if (section.calculateSectionHeight() > 0) {
            int x = this.width - 6;

            float overflow = ((float) section.calculateSectionHeight() / this.height);
            int height = section.height - MathHelper.lerp(overflow, 0, section.height);
            height = MathHelper.clamp(height, 20, section.height - 8);

            float percentage = (float) -section.scrollOffset / section.calculateSectionHeight();
            int y = MathHelper.lerp(percentage, 57, this.height - 30 - height);

            context.fill(x, 57, x + 6, this.height - 30, -16777216);
            context.drawGuiTexture(RenderLayer::getGuiTextured, SCROLLER_TEXTURE, x, y, 6, height);
        }

        section.render2(context, 57, mouseX, mouseY, deltaTicks);

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    }

    private void renderBackgroundTexture(DrawContext context) {
        context.drawTexture(RenderLayer::getGuiTextured, OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0, 0, this.width, this.height, 32, 32);
    }

    private void drawHeaderAndFooterSeparators(DrawContext context) {
        context.drawTexture(RenderLayer::getGuiTextured, Screen.HEADER_SEPARATOR_TEXTURE, 0, 55, 0.0f, 0.0f, this.width, 2, 32, 2);
        context.drawTexture(RenderLayer::getGuiTextured, Screen.FOOTER_SEPARATOR_TEXTURE, 0, this.height -30, 0.0f, 0.0f, this.width, 2, 32, 2);
    }

    private void drawDirtTextureBlurred(DrawContext context, int x, int y, int width, int height) {
        int color = ColorHelper.fromFloats(.7f, 0, 0, 0);
        context.drawTexture(RenderLayer::getGuiTextured, OPTIONS_BACKGROUND_TEXTURE, x, y, 0, 0, width, height, 64, 64);
        context.fillGradient(x, y, x + width, y + height, color, color);
    }
}

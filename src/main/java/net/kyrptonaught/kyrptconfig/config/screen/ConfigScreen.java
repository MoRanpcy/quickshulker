package net.kyrptonaught.kyrptconfig.config.screen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class ConfigScreen extends Screen {

    int selectedSection = 0;
    List<ConfigSection> sections = new ArrayList<>();
    private Runnable saveRunnable;
    Screen previousScreen;
    private NotSuckyButton scrollLeftBTN, scrollRightBTN;
    int horizontalScrollOffset = -1;
    private static final Identifier SCROLLER_TEXTURE = Identifier.parse("widget/scroller");
    private static final Identifier OPTIONS_BACKGROUND_TEXTURE = Identifier.parse("textures/block/dirt.png");

    public ConfigScreen(Screen previousScreen, Component title) {
        super(title);
        this.previousScreen = previousScreen;
    }

    protected void init() {
        int center = this.width / 2;
        this.addRenderableWidget(new NotSuckyButton(center - 153, height - 25, 150, 20, Component.translatable("key.kyrptconfig.config.exit"), widget -> {
            this.minecraft.setScreen(previousScreen);
        }));

        this.addRenderableWidget(new NotSuckyButton(center + 3, height - 25, 150, 20, Component.translatable("key.kyrptconfig.config.saveExit"), widget -> {
            save();
            this.minecraft.setScreen(previousScreen);
        }));
        for (ConfigSection section : sections) {
            section.init(width, height - 57 - 30);
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
        item.sectionSelectionBTN.setWidth(Minecraft.getInstance().font.width(item.title) + 10);

        this.sections.add(item);
    }

    public boolean adjustForHorizontalScroll(int maxWidth) {
        NotSuckyButton lastBTN = sections.get(sections.size() - 1).sectionSelectionBTN;

        this.scrollLeftBTN = new NotSuckyButton(10, 32, 10, 20, Component.literal("<"), widget -> {
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

        this.scrollRightBTN = new NotSuckyButton(this.width - 20, 32, 10, 20, Component.literal(">"), widget -> {
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
    public boolean keyPressed(KeyEvent input) {
        if (sections.get(selectedSection).keyPressed(input)) return true;
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        return sections.get(selectedSection).charTyped(input);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        super.mouseClicked(click, doubled);

        if (scrollLeftBTN.mouseClicked(click, doubled) || scrollRightBTN.mouseClicked(click, doubled))
            return true;

        for (ConfigSection section : sections)
            if (section.sectionSelectionBTN.mouseClicked(click, doubled)) return true;

        return sections.get(selectedSection).mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return sections.get(selectedSection).mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        super.extractBackground(context, mouseX, mouseY, deltaTicks);

        ConfigSection section = sections.get(selectedSection);

        context.enableScissor(0, 57, this.width, this.height - 30);
        context.fillGradient(0, 57, this.width, this.height, 1744830464, 1744830464);
        section.extractRenderState(context, 57, mouseX, mouseY, deltaTicks);
        context.disableScissor();

        context.centeredText(this.font, this.title, this.width / 2, 13, -1);
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
                selectionBTN.extractRenderState(context, mouseX, mouseY, deltaTicks);
            }
            context.disableScissor();
            scrollLeftBTN.extractRenderState(context, mouseX, mouseY, deltaTicks);
            scrollRightBTN.extractRenderState(context, mouseX, mouseY, deltaTicks);
        }else{
            for (int i = 0; i < sections.size(); i++) {
                NotSuckyButton selectionBTN = sections.get(i).sectionSelectionBTN;
                selectionBTN.active = i != selectedSection;
                selectionBTN.extractRenderState(context, mouseX, mouseY, deltaTicks);
            }
        }

        if (section.calculateSectionHeight() > 0) {
            int x = this.width - 6;

            float overflow = ((float) section.calculateSectionHeight() / this.height);
            int height = section.height - Mth.lerpInt(overflow, 0, section.height);
            height = Mth.clamp(height, 20, section.height - 8);

            float percentage = (float) -section.scrollOffset / section.calculateSectionHeight();
            int y = Mth.lerpInt(percentage, 57, this.height - 30 - height);

            context.fill(x, 57, x + 6, this.height - 30, -16777216);
            context.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, x, y, 6, height);
        }

        section.extractRenderState2(context, 57, mouseX, mouseY, deltaTicks);

        super.extractRenderState(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
    }

    private void extractBackgroundTexture(GuiGraphicsExtractor context) {
        context.blit(RenderPipelines.GUI_TEXTURED, OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0, 0, this.width, this.height, 32, 32);
    }

    private void drawHeaderAndFooterSeparators(GuiGraphicsExtractor context) {
        context.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, 0, 55, 0.0f, 0.0f, this.width, 2, 32, 2);
        context.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height -30, 0.0f, 0.0f, this.width, 2, 32, 2);
    }

    private void drawDirtTextureBlurred(GuiGraphicsExtractor context, int x, int y, int width, int height) {
        int color = ARGB.colorFromFloat(.7f, 0, 0, 0);
        context.blit(RenderPipelines.GUI_TEXTURED, OPTIONS_BACKGROUND_TEXTURE, x, y, 0, 0, width, height, 64, 64);
        context.fillGradient(x, y, x + width, y + height, color, color);
    }
}

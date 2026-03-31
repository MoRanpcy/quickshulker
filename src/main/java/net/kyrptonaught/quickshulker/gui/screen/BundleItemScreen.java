package net.kyrptonaught.quickshulker.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BundleItemScreen extends HandledScreen<BundleItemScreenHandler> {
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/creative_inventory/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/creative_inventory/scroller_disabled");
    private static final Identifier CONTAINER_TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    private static final Identifier SCROLLBAR_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/container/creative_inventory/tab_items.png");
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private float scrollPosition;
    private boolean scrolling;

    private static final int ROWS_COUNT = 5;
    private static final int COLUMNS_COUNT = 8;

    public BundleItemScreen(BundleItemScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.scrollPosition = 0.0f;
        this.scrolling = false;
        this.backgroundHeight = 202;
        this.backgroundWidth = 176;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init(){
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.x, this.y, 0.0F, 0.0F, this.backgroundWidth, ROWS_COUNT * 18 + 17, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.x, this.y + ROWS_COUNT * 18 + 17, 0.0F, 126.0F, this.backgroundWidth, 96, 256, 256);
        this.drawScrollbarBackground(context);
        int i = this.x + 156;
        int j = this.y + 18;
        int k = j + 88;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, i, j + (int) ((k - j - SCROLLBAR_HEIGHT) * this.scrollPosition), SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
    }

    public void drawScrollbarBackground(DrawContext context){
        int i = this.x + 8 + COLUMNS_COUNT * 18 - 1;
        int j = this.y + 18 - 1;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, SCROLLBAR_BACKGROUND_TEXTURE, i, j, 170, 17, 18, 72, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, SCROLLBAR_BACKGROUND_TEXTURE, i, j + 72, 170, 111, 18, 18, 256, 256);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if(click.button() == 0){
            if(this.isClickInScrollbar(click.x(), click.y())){
                return this.scrolling = true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    protected boolean isClickInScrollbar(double mouseX, double mouseY) {
        int k = this.x + 156;
        int l = this.y + 18;
        int m = k + 12;
        int n = l + 88;
        return mouseX >= k && mouseY >= l && mouseX < m && mouseY < n;
    }

    @Override
    public boolean mouseReleased(Click click) {
        if(click.button() == 0){
            this.scrolling = false;
        }

        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.scrollPosition = this.handler.getScrollPosition(this.scrollPosition, verticalAmount);
        this.handler.scrollItems(this.scrollPosition);
        return true;
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if(this.scrolling){
            int i = this.y + 18;
            int j = i + 88;
            this.scrollPosition = ((float) click.y() - i - 7.5F) / (j - i - 15.0F);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
            this.handler.scrollItems(this.scrollPosition);
            return true;
        }else {
            return super.mouseDragged(click, offsetX, offsetY);
        }
    }
}

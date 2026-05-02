package yc.ycqin.nb.gui;

import com.overlast.config.OverConfig;
import com.overlast.gui.RenderHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yc.ycqin.nb.client.ClientUDData;

public class UDBarDecorator extends Gui {
    public static UDBarDecorator In = new UDBarDecorator();
    private static final int FULL_W = 113;
    private static final int FULL_H = 29;
    private static final int MOVING_TEX_X = 23;
    private static final int MOVING_TEX_Y = 32;
    private static final int DEFAULT_BAR_W = 80;

    private static final ResourceLocation[] UD_TEX = {
            null, // 占位，索引 0 不用
            new ResourceLocation("ycqin", "textures/gui/eye1.png"),
            new ResourceLocation("ycqin", "textures/gui/eye2.png"),
            new ResourceLocation("ycqin", "textures/gui/eye3.png"),
            new ResourceLocation("ycqin", "textures/gui/eye4.png")
    };
    private int getX(int screenWidth, int pos) {
        if (!OverConfig.CLIENT.barPositions.equals("top left") &&
                !OverConfig.CLIENT.barPositions.equals("middle left") &&
                !OverConfig.CLIENT.barPositions.equals("bottom left")) {
            return screenWidth - 2 + OverConfig.CLIENT.Xoffset;
        } else {
            return 150 + OverConfig.CLIENT.Xoffset;
        }
    }

    private int getY(int screenHeight, int pos) {
        if (!OverConfig.CLIENT.barPositions.equals("top left") &&
                !OverConfig.CLIENT.barPositions.equals("top right")) {
            if (!OverConfig.CLIENT.barPositions.equals("middle left") &&
                    !OverConfig.CLIENT.barPositions.equals("middle right")) {
                return !OverConfig.CLIENT.barPositions.equals("bottom left") &&
                        !OverConfig.CLIENT.barPositions.equals("bottom right")
                        ? screenHeight / 2 - 30 + 20 * pos + OverConfig.CLIENT.Yoffset
                        : screenHeight - 80 + 20 * pos + OverConfig.CLIENT.Yoffset;
            } else {
                return screenHeight / 2 - 30 + 20 * pos + OverConfig.CLIENT.Yoffset;
            }
        } else {
            return 10 + 20 * pos + OverConfig.CLIENT.Yoffset;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;
        if (!RenderHUD.switchhud) return;

        int level = ClientUDData.udLevel;
        if (level <= 0 || level > 4) return;

        ScaledResolution res = event.getResolution();
        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();
        int pos = 1;

        int x = getX(screenWidth, pos);
        int y = getY(screenHeight, pos);

        mc.getTextureManager().bindTexture(UD_TEX[level]);

        // 启用透明度混合（使纹理透明部分正确叠加）
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // 绘制移动部分（显示 UD 等级对应的进度段）
        this.drawTexturedModalRect(x - DEFAULT_BAR_W - 10, y + 3,
                MOVING_TEX_X, MOVING_TEX_Y,66, FULL_H);

        GlStateManager.disableBlend();
    }
}
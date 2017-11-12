package net.minecraft.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import nivia.gui.customscreens.CustomScreenUtils;
import nivia.modules.Hooked;
import nivia.utils.Helper;

public class GuiButton extends Gui
{
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    private int hoverstage;
    /** Button width in pixels */
    protected int width;

    /** Button height in pixels */
    protected int height;

    /** The x position of this control. */
    public int xPosition;

    /** The y position of this control. */
    public int yPosition;

    /** The string displayed on this control. */
    public String displayString;
    public int id;

    /** True if this control is enabled, false to disable. */
    public boolean enabled;

    /** Hides the button completely if false. */
    public boolean visible;
    protected boolean hovered;

    public GuiButton(int buttonId, int x, int y, String buttonText)
    {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
    {
    	this.hoverstage = 0;
        this.width = 200;
        this.height = 20;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        byte var2 = 1;

        if (!this.enabled)
        {
            var2 = 0;
        }
        else if (mouseOver)
        {
            var2 = 2;
        }

        return var2;
    }

    
    /*     */   public static void drawRectangleBorder(double left, double top, double right, double bottom, float borderWidth, int borderColor)
    /*     */   {
    /*  24 */     float alpha = 1.0F;
    /*  25 */     float red = (borderColor >> 16 & 0xFF) / 255.0F;
    /*  26 */     float green = (borderColor >> 8 & 0xFF) / 255.0F;
    /*  27 */     float blue = (borderColor & 0xFF) / 255.0F;
    /*  28 */     GlStateManager.pushMatrix();
    /*  29 */     GlStateManager.enableBlend();
    /*  30 */     GlStateManager.func_179090_x();
    /*  31 */     GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    /*  32 */     GlStateManager.color(red, green, blue, alpha);
    /*  33 */     if (borderWidth == 1.0F) {
    /*  34 */       GL11.glEnable(2848);
    /*     */     }
    /*  36 */     GL11.glLineWidth(borderWidth);
    /*  37 */     Tessellator tessellator = Tessellator.getInstance();
    /*  38 */     WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    /*  39 */     worldRenderer.startDrawing(1);
    /*  40 */     worldRenderer.addVertex(left, top, 0.0D);
    /*  41 */     worldRenderer.addVertex(left, bottom, 0.0D);
    /*  42 */     worldRenderer.addVertex(right, bottom, 0.0D);
    /*  43 */     worldRenderer.addVertex(right, top, 0.0D);
    /*  44 */     worldRenderer.addVertex(left, top, 0.0D);
    /*  45 */     worldRenderer.addVertex(right, top, 0.0D);
    /*  46 */     worldRenderer.addVertex(left, bottom, 0.0D);
    /*  47 */     worldRenderer.addVertex(right, bottom, 0.0D);
    /*  48 */     tessellator.draw();
    /*  49 */     GL11.glLineWidth(2.0F);
    /*  50 */     if (borderWidth == 1.0F) {
    /*  51 */       GL11.glDisable(2848);
    /*     */     }
    /*  53 */     GlStateManager.func_179098_w();
    /*  54 */     GlStateManager.disableBlend();
    /*  55 */     GlStateManager.popMatrix();
    /*     */   }
    /*     */   public static Color darker(Color color, double fraction)
    /*     */   {
    /*  81 */     int red = (int)Math.round(color.getRed() * (1.0D - fraction));
    /*  82 */     int green = (int)Math.round(color.getGreen() * (1.0D - fraction));
    /*  83 */     int blue = (int)Math.round(color.getBlue() * (1.0D - fraction));
    /*  84 */     if (red < 0) {
    /*  85 */       red = 0;
    /*  86 */     } else if (red > 255) {
    /*  87 */       red = 255;
    /*     */     }
    /*  89 */     if (green < 0) {
    /*  90 */       green = 0;
    /*  91 */     } else if (green > 255) {
    /*  92 */       green = 255;
    /*     */     }
    /*  94 */     if (blue < 0) {
    /*  95 */       blue = 0;
    /*  96 */     } else if (blue > 255) {
    /*  97 */       blue = 255;
    /*     */     }
    /*  99 */     int alpha = color.getAlpha();
    /*     */     
    /* 101 */     return new Color(red, green, blue, alpha);
    /*     */   }
    /*     */   public static Color rainbow(long offset, float fade)
    /*     */   {
    /*  19 */     float hue = (float)(System.nanoTime() + offset) / 1.0E10F % 1.0F;
    /*  20 */     long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 1.0F, 1.0F)).intValue()), 16);
    /*  21 */     Color c = new Color((int)color);
    /*  22 */     return new Color(c.getRed() / 255.0F * fade, c.getGreen() / 255.0F * fade, c.getBlue() / 255.0F * fade, 
    /*  23 */       c.getAlpha() / 255.0F);
    /*     */   }
    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            FontRenderer var4 = mc.fontRendererObj;
            ResourceLocation buttons = buttonTextures;
            if(Hooked.sallosMM && CustomScreenUtils.drawCustomButtons()) buttons = CustomScreenUtils.buttons;
            mc.getTextureManager().bindTexture(buttons);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + var5 * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + var5 * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int var6 = 14737632;
            this.drawGradientRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, -1207959552, -1207959552);
            this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition, -1202367147, -1202367147);
            this.drawGradientRect(this.xPosition, this.yPosition + this.height, this.xPosition + this.width, this.yPosition + this.height, -1202367147, -1202367147);
            this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition, this.yPosition + this.height, -1202367147, -1202367147);
            this.drawGradientRect(this.xPosition + this.width, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -1202367147, -1202367147);
            this.drawGradientRect(this.xPosition + this.width, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -1202367147, -1202367147);
            boolean hover = (mouseX >= this.xPosition) && (mouseX <= this.xPosition + this.width) && (mouseY >= this.yPosition) && (mouseY <= this.yPosition + this.height);
            drawRectangleBorder(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 2.0F, rainbow(1L, 1.0F).getRGB());
                 
            drawRect(this.xPosition, this.yPosition + this.height, this.xPosition + this.width, this.yPosition + this.height - this.hoverstage, new Color(255, 25, 255).getRGB());
     //       mc.fontRendererObj.drawCenteredStringNoShadow(this.displayString, this.xPosition + this.width / 2, this.yPosition + this.height / 2 - 4, darker(new Color(255, 255, 255), this.hoverstage / 20.0D).getRGB());
            /*  91 */       if (hover)
            /*     */       {
            /*  93 */         if (this.hoverstage < this.height) {
            /*  94 */           this.hoverstage += 1;
            /*     */         }
            /*     */       }
            /*  97 */       else if (this.hoverstage > 0) {
            /*  98 */         this.hoverstage -= 1;
            /*     */       }     
            if (!this.enabled)
            {
                var6 = 10526880;
            }
            else if (this.hovered)
            {
                var6 = Hooked.sallosMM && CustomScreenUtils.drawCustomButtons() ? 0x00CCFF : 16777120;
            }
            if(Hooked.sallosMM && CustomScreenUtils.drawCustomButtons())
                Helper.get2DUtils().drawCentredStringWithShadow(this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var6);
            else
                this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var6);
        }
    }

    /*     */   public static void drawRect(int left, int top, int right, int bottom, int color)
    /*     */   {
    /*  79 */     if (left < right)
    /*     */     {
    /*  81 */       int var5 = left;
    /*  82 */       left = right;
    /*  83 */       right = var5;
    /*     */     }
    /*  85 */     if (top < bottom)
    /*     */     {
    /*  87 */       int var5 = top;
    /*  88 */       top = bottom;
    /*  89 */       bottom = var5;
    /*     */     }
    /*  91 */     float var11 = (color >> 24 & 0xFF) / 255.0F;
    /*  92 */     float var6 = (color >> 16 & 0xFF) / 255.0F;
    /*  93 */     float var7 = (color >> 8 & 0xFF) / 255.0F;
    /*  94 */     float var8 = (color & 0xFF) / 255.0F;
    /*  95 */     Tessellator var9 = Tessellator.getInstance();
    /*  96 */     WorldRenderer var10 = var9.getWorldRenderer();
    /*  97 */     GlStateManager.enableBlend();
    /*  98 */     GlStateManager.func_179090_x();
    /*  99 */     GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    /* 100 */     GlStateManager.color(var6, var7, var8, var11);
    /* 101 */     var10.startDrawingQuads();
    /* 102 */     var10.addVertex(left, bottom, 0.0D);
    /* 103 */     var10.addVertex(right, bottom, 0.0D);
    /* 104 */     var10.addVertex(right, top, 0.0D);
    /* 105 */     var10.addVertex(left, top, 0.0D);
    /* 106 */     var9.draw();
    /* 107 */     GlStateManager.func_179098_w();
    /* 108 */     GlStateManager.disableBlend();
    /*     */   }
    
    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {}

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY) {}

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    /**
     * Whether the mouse cursor is currently over the button.
     */
    public boolean isMouseOver()
    {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {}

    public void playPressSound(SoundHandler soundHandlerIn)
    {
        soundHandlerIn.playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public int getButtonWidth()
    {
        return this.width;
    }

    public void func_175211_a(int p_175211_1_)
    {
        this.width = p_175211_1_;
    }
}

package nivia.modules.miscellanous;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;

import org.lwjgl.opengl.GL11;
import nivia.Pandora;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.Event3D;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.FriendManager;
import nivia.managers.PropertyManager;
import nivia.managers.StaffManager;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.utils.Timer;

import java.awt.*;
import java.util.ArrayList;


public class HackerFinder extends Module {
    public Property<Boolean> autoWDR = new Property<Boolean>(this, "AutoWDR", true);    
	
    private int bufferNoFall;
    private int bufferFlight;
    private int bufferSpeed;
    private int buffernodown;
	
	public HackerFinder() {
		super("HackerFinder", 0, 0, Category.MISCELLANEOUS, "wdr.", new String[] { "wdr", "awdr", "autowd", "autowdr"}, true);
	}

	
    public static float getDistanceToGround(final Entity e) {
        if (Helper.mc().thePlayer.isCollidedVertically) {
            return 0.0f;
        }
        float a = (float)e.posY;
        while (a > 0.0f) {
            final int[] stairs = { 53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180 };
            final int[] exemptIds = { 6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 83, 92, 93, 94, 104, 105, 106, 115, 119, 131, 132, 143, 147, 148, 149, 150, 157, 171, 175, 176, 177 };
            final Block block = Helper.mc().theWorld.getBlockState(new BlockPos(e.posX, a - 1.0f, e.posZ)).getBlock();
            if (!(block instanceof BlockAir)) {
                if (Block.getIdFromBlock(block) == 44 || Block.getIdFromBlock(block) == 126) {
                    return ((float)(e.posY - a - 0.5) < 0.0f) ? 0.0f : ((float)(e.posY - a - 0.5));
                }
                int[] array;
                for (int length = (array = stairs).length, i = 0; i < length; ++i) {
                    final int id = array[i];
                    if (Block.getIdFromBlock(block) == id) {
                        return ((float)(e.posY - a - 1.0) < 0.0f) ? 0.0f : ((float)(e.posY - a - 1.0));
                    }
                }
                int[] array2;
                for (int length2 = (array2 = exemptIds).length, j = 0; j < length2; ++j) {
                    final int id = array2[j];
                    if (Block.getIdFromBlock(block) == id) {
                        return ((float)(e.posY - a) < 0.0f) ? 0.0f : ((float)(e.posY - a));
                    }
                }
                return (float)(e.posY - a + block.getBlockBoundsMaxY() - 1.0);
            }
            else {
                --a;
            }
        }
        return 0.0f;
    }
	
	
	@SuppressWarnings("unchecked")
    @EventTarget(Priority.LOWEST)
    public void onPreMotion(final EventPreMotionUpdates event) {
		mc.theWorld.loadedEntityList.forEach(o -> {
			Entity p = (Entity)o;
            if (p != Helper.player() && !p.isDead && p instanceof EntityPlayer) {
                if (getDistanceToGround(p) > 4.0f && p.onGround && p.posY < p.prevPosY && !p.isSlient()) {
                    ++this.bufferNoFall;
                }
                if (this.bufferNoFall >= 10) {
                	if(autoWDR.value) Helper.player().sendChatMessage("/wdr " + p.getName() + " ka speed reach fly antiknockback autoclicker dolphin");
                    Logger.logChat("[HackerFinder] §c"+p.getName()+" 可能开了NoFall！"); 
                    this.bufferNoFall = 0;
                }
                if (p.lastTickPosY < p.posY - 0.4 && !p.isSlient()) {
                    ++this.bufferFlight;
                }
                if (this.bufferFlight >= 50) {
                	if(autoWDR.value) Helper.player().sendChatMessage("/wdr " + p.getName() + " ka speed reach fly antiknockback autoclicker dolphin");
                    Logger.logChat("[HackerFinder] §c"+p.getName()+" 可能开了Fly！"); 
                     this.bufferFlight = 0;
                }
                if (p.lastTickPosX < p.posX - 0.7) {
                    ++this.bufferSpeed;
                }
                if (p.posX > p.lastTickPosX + 0.7) {
                    ++this.bufferSpeed;
                }
                if (p.lastTickPosZ < p.posZ - 0.7) {
                    ++this.bufferSpeed;
                }
                if (p.posX > p.lastTickPosX + 0.7) {
                    ++this.bufferSpeed;
                }
                if (this.bufferSpeed > 14) {
                   if(autoWDR.value) Helper.player().sendChatMessage("/wdr " + p.getName() + " ka speed reach fly antiknockback autoclicker dolphin");
                   Logger.logChat("[HackerFinder] §c"+p.getName()+" 可能开了Speed！"); 
                   this.bufferSpeed = 0;
                }
            }
        });
    }



	@Override
	public void onDisable() {
		super.onDisable();
        this.bufferNoFall = 0;
        this.bufferFlight = 0;
	}

	
	@Override
	public void onEnable() {
		super.onEnable();
        this.bufferNoFall = 0;
        this.bufferFlight = 0;
	}

}

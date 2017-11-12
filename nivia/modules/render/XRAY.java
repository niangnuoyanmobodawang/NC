package nivia.modules.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.Event3D;
import nivia.events.events.EventBlockRender;
import nivia.events.events.EventBoundingBox;
import nivia.events.events.EventTick;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.modules.Module.Category;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.TimeHelper;
import nivia.utils.utils.Timer;

public class XRAY extends Module {
      private Property<Boolean> Bypass = new Property<>(this, "Bypass", true);
      private Property<Boolean> DIF = new Property<>(this, "DiamondFinder", false);
      public DoubleProperty opacity = new DoubleProperty(this, "opacity", 0, 0, 1360);
      public Timer tm = new Timer();
	  public boolean loaded;
	   private static String KEY_OPACITY = "OPACITY";
	   private static HashSet blockIDs = new HashSet();
	//   private int opacity = 0;
	   List KEY_IDS = Lists.newArrayList(new Integer[]{10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62, 73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154});
	
	   

    public XRAY() {
        super("XRAY", 0, 0, Category.RENDER, "XRAY.", new String[] { "xray", "XRAY" }, true);
    }
    
     @EventTarget(Priority.LOWEST)
	public void onTick(EventTick tick) {
	      Helper.opacity = (int) opacity.getValue();
    }
    
 	/*@EventTarget
 	public void onRender3D(Event3D e) {
 		//if(!loaded)
 		//	return;
 	    final double viewerPosX = Helper.mc().getRenderManager().viewerPosX;
        final double viewerPosY = Helper.mc().getRenderManager().viewerPosY;
        final double viewerPosZ = Helper.mc().getRenderManager().viewerPosZ;
        final double line_lenght = 4.35;
        for(int i=0;i<Helper.dimblock.size();i++){
        	synchronized(this){
        final ArrayList<BlockPos> dontRender = new ArrayList<BlockPos>();
            final Block pos = (Block) Helper.dimblock.get(i);
            if (dontRender.contains(pos)) {
                continue;
            }
            final double x = (viewerPosX - pos.getBlockBoundsMinX()) + 0.5;
            final double y = (viewerPosY - pos.getBlockBoundsMinY()) + 0.5;
            final double z = (viewerPosZ - pos.getBlockBoundsMinZ()) + 0.5;
        //   
            GL11.glPushMatrix();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(1.0f);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(true);
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            GL11.glBegin(1);
            GL11.glVertex3d(x - line_lenght, y, z);
            GL11.glVertex3d(x + line_lenght, y, z);
            GL11.glVertex3d(x, y + line_lenght, z);
            GL11.glVertex3d(x, y - line_lenght, z);
            GL11.glVertex3d(x, y, z + line_lenght);
            GL11.glVertex3d(x, y, z - line_lenght);
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        	}
        }
 	}
    */
	@Override
	public void onEnable() {
		super.onEnable();
		  Helper.dimblock.clear();
	      blockIDs.clear();
	      this.opacity = opacity;

	      try {
	         Iterator var1 = this.KEY_IDS.iterator();

	         while(var1.hasNext()) {
	            Integer o = (Integer)var1.next();
	            blockIDs.add(o);
	         }
	      } catch (Exception var3) {
	         var3.printStackTrace();
	      }
	      System.out.println(blockIDs);
	    if(Bypass.value)  this.setSuffix("Bypass");
	      Helper.bypass = Bypass.value;
	      Helper.opacity = (int) opacity.getValue();
	      Helper.blockIDs = blockIDs;
	      Helper.DIF = DIF.value;
          Helper.xray = true;
	      mc.renderGlobal.loadRenderers();
	//	oldGamma = mc.gameSettings.gammaSetting;
	//	this.mc.gameSettings.gammaSetting = 1000.0F;
	      final int var0 = (int)mc.thePlayer.posX;
	      final int var = (int)mc.thePlayer.posY;
	      final int var2 = (int)mc.thePlayer.posZ;
	      mc.renderGlobal.markBlockRangeForRenderUpdate(var0 - 900, var - 900, var2 - 900, var0 + 900, var + 900, var2 + 900);

	      tm.reset();
	      loaded = true;
	  //    Logger.logChat(Helper.dimblock.size());
	 }
	

	
	@Override
	public void onDisable() {
		super.onDisable();
		      Helper.xray = false;
		      mc.renderGlobal.loadRenderers();
		      Helper.dimblock.clear();
              loaded = false; 
		   }
	
    
}

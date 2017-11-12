package nivia.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import nivia.events.Event;
import nivia.events.EventTarget;
import nivia.events.events.EventMove;
import nivia.events.events.EventPreMotionUpdates;
import nivia.events.events.EventTick;
import nivia.modules.Module;
import nivia.modules.Module.Category;
import nivia.utils.Helper;

public class Strafe extends Module {

	public boolean air = true;
	
    public Strafe() {
        super("QQStrafe", Keyboard.KEY_NONE, 0xFFFFFFFF, Category.MOVEMENT, "RemoStrafe.", new String[] { "Strafe" }, true);
      }

	@EventTarget
    public void onPreMotion(EventPreMotionUpdates event) {
        if(event instanceof EventPreMotionUpdates) {
        	Minecraft Minecraft = mc;
        	if ( 
        		 (Minecraft.thePlayer.hurtTime <= 0) && (
        	 (Minecraft.thePlayer.onGround) || ((this.air) && (!Minecraft.thePlayer.isInWater()))))
        	 {
        	  float dir = Minecraft.thePlayer.rotationYaw;
        			/* 34 */       if (Minecraft.thePlayer.moveForward < 0.0F) {
        			/* 35 */         dir += 180.0F;
        			/*    */       }
        			/* 37 */       if (Minecraft.thePlayer.moveStrafing > 0.0F) {
        			/* 38 */         dir -= 90.0F * (Minecraft.thePlayer.moveForward > 0.0F ? 0.68F : Minecraft.thePlayer.moveForward < 0.0F ? -0.5F : 1.0F);
        			/*    */       }
        			/* 40 */       if (Minecraft.thePlayer.moveStrafing < 0.0F) {
        			/* 41 */         dir += 90.0F * (Minecraft.thePlayer.moveForward > 0.0F ? 0.68F : Minecraft.thePlayer.moveForward < 0.0F ? -0.5F : 1.0F);
        			/*    */       }
        			/* 43 */       double hOff = 0.221D;
        			/* 44 */       if (Minecraft.thePlayer.isSprinting()) {
        			/*    */ 
        			/* 48 */           hOff *= 1.3190000119209289D;
        			/*    */                  
        			/*    */       }
        			/* 54 */       if (Minecraft.thePlayer.isSneaking()) {
        			/* 55 */         hOff *= 0.3D;
        			/*    */       }
        			/*    */       
        			/* 58 */       float var9 = (float)((float)Math.cos((dir + 90.0F) * 3.141592653589793D / 180.0D) * hOff);
        			/* 59 */       float zD = (float)((float)Math.sin((dir + 90.0F) * 3.141592653589793D / 180.0D) * hOff);
        			/* 60 */       if ((mc.gameSettings.keyBindForward.pressed) || (mc.gameSettings.keyBindLeft.pressed) || (mc.gameSettings.keyBindRight.pressed) || (mc.gameSettings.keyBindBack.pressed))
        			/*    */       {
        			/* 62 */         event.setX(var9);
        			/* 63 */         event.setZ(zD);
        			/*    */       }
        			/*    */     }
        }

     }
}

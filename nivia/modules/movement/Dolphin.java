package nivia.modules.movement;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import nivia.Pandora;
import nivia.commands.Command;
import nivia.events.EventTarget;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.modules.movement.Speed.speedMode;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.Wrapper;
import nivia.utils.utils.BlockUtils;
import nivia.utils.utils.Timer;

public class Dolphin extends Module {
	boolean getDown;
	private boolean wasWater = false;
	private int ticks = 0;
	public Property<Boolean> SpoofGround = new Property<Boolean>(this, "SpoofGround", true);
	public Property<Boolean> lava = new Property<Boolean>(this, "OnLava", true);
	public Property<Boolean> aac = new Property<Boolean>(this, "Improved", true);
	public DoubleProperty Jmo = new DoubleProperty(this, "JumpMotion", 0.096, 0.001, 0.901 , 0.001);
	public DoubleProperty JmoT = new DoubleProperty(this, "JumpTick", 20, 16, 50);
	
	private Timer timer = new Timer();
	public Dolphin() {
		super("Jesus2", 0, 0x75FF47, Category.MOVEMENT, "Leap 4.3 blocks whilst on water.",
				new String[] { "dolphin", "dolph" }, true);
	}


	@EventTarget
	public void onEvent(EventPreMotionUpdates pre) {
		
	    if (Helper.player().onGround || Helper.player().isOnLadder())
		wasWater = false;

	if (Helper.player().motionY > 0 && wasWater) {
		if (Helper.player().motionY <= 0.1127)
		    Helper.player().motionY *= 1.267;
		Helper.player().motionY += 0.05172;
	}

	if (isInLiquid() && !Helper.player().isSneaking()) {
		if(SpoofGround.value) pre.setGround(true);
		mc.timer.timerSpeed = 1.34F;
		if(!aac.value){
		if (ticks < 3) {
			Helper.player().motionY = 0.13;
			ticks++;
			wasWater = false;
		} else {
		    Helper.player().motionY = 0.078;
			ticks = 0;
			wasWater = true;
			}
		}else{
			if (ticks < 1.5) {
				Helper.player().motionY = 0.132;
				ticks++;
				wasWater = false;
			}
			else if (ticks < 3.5) {
				Helper.player().motionY = 0.074;
				ticks++;
				wasWater = false;
			} 
			else if (ticks < 9.5) {
				Helper.player().motionY = 0.075;
				ticks++;
				wasWater = false;
			} else if (ticks < 15) {
				Helper.player().motionY = 0.077;
				//Helper.player().motionY = 0.07;
				ticks++;
				wasWater = true;
			} 
			else if (ticks < JmoT.getValue()) {
				Helper.player().motionY = Jmo.getValue();
				//Helper.player().motionY = 0.07;
				ticks++;
				wasWater = true;
			} 
			else {
			    Helper.player().motionY = 0.099;
				ticks = 0;
				wasWater = true;
				}
			
		}
		mc.timer.timerSpeed = 1.0F;
		}
	}
	

	
	public boolean isInLiquid() {
	      AxisAlignedBB par1AxisAlignedBB = Wrapper.getPlayer().boundingBox.contract(0.001D, 0.001D, 0.001D);
	      int var4 = MathHelper.floor_double(par1AxisAlignedBB.minX);
	      int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
	      int var6 = MathHelper.floor_double(par1AxisAlignedBB.minY);
	      int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
	      int var8 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
	      int var9 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);
	      new Vec3(0.0D, 0.0D, 0.0D);

	      for(int var12 = var4; var12 < var5; ++var12) {
	         for(int var13 = var6; var13 < var7; ++var13) {
	            for(int var14 = var8; var14 < var9; ++var14) {
	               Minecraft.getMinecraft();
	               Block var15 = BlockUtils.getBlock(var12, var13, var14);
	               if(var15 instanceof BlockLiquid) {
	                  return true;
	               }
	            }
	         }
	      }

	      return false;
	   }
	
	@Override
	public void onEnable() {
		super.onEnable();
		Logger.logChat("使用-jesus2 jumpmotion来详细修改");
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		mc.timer.timerSpeed = 1.0F;
		Logger.logChat("使用-jesus2 jumpmotion来详细修改");
	}
	
    protected void addCommand(){
        Pandora.getCommandManager().cmds.add(new Command
                ("jesus2", "Manages speed values", Logger.LogExecutionFail("Option, Options:", new String[]{"jumpmotion","jumptick"}) , "sp") {
            @Override
            public void execute(String commandName,String[] arguments){
                String message = arguments[1];
                switch(message.toLowerCase()){                 
                    case "jumpmotion":
                    	try{
                    	Jmo.setValue(Double.parseDouble(arguments[2]));
                    	Logger.logChat(Double.parseDouble(arguments[2]));
                    	}catch(Exception x){x.printStackTrace();}
                        break;
                    case "jumptick":
                    	try{
                    	JmoT.setValue(Double.parseDouble(arguments[2]));
                    	Logger.logChat(Double.parseDouble(arguments[2]));
                    	}catch(Exception x){x.printStackTrace();}
                        break;
                    default:
                        Logger.logChat(this.getError());
                        break;
                }
            }
        });
    }
}

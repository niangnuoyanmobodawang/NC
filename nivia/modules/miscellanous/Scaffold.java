package nivia.modules.miscellanous;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import nivia.Pandora;
import nivia.commands.Command;
import nivia.commands.commands.Damage;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.Event2D;
import nivia.events.events.Event3D;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.events.events.EventTick;
import nivia.managers.PropertyManager;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.modules.render.GUI;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.utils.Timer;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
 
public class Scaffold extends Module {
    private List<Block> invalid = Arrays.asList(new Block[] { Blocks.air, Blocks.water, Blocks.fire,
            Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.chest, Blocks.anvil, Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.gravel });
    private Timer timer = new Timer();
    private boolean speedon = false;
    private Timer timerMotion = new Timer();
    private Timer timerMotion2 = new Timer();
    private double moveSpeed;
    private BlockData blockData;
    boolean placing;
	private Random random2 = new Random();
	private Timer time = new Timer();
    //public Property<Boolean> fast = new Property<>(this, "Fast", true);
    public Property<Boolean> Hypixel = new Property<>(this, "Hypixel", true);
    public Property<Boolean> tower = new Property<>(this, "Tower", true);
    public Property<Boolean> Switch = new Property<>(this, "Switch", false);
    public PropertyManager.DoubleProperty timersp = new PropertyManager.DoubleProperty(this, "MotionSpeed", 0.9, 0.3, 2.0, 0.1);
	private BlockPos blockBelow;
	public List blacklist = Arrays.asList(Blocks.air, Blocks.water, Blocks.torch, Blocks.redstone_torch, Blocks.ladder, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.web, Blocks.redstone_torch, Blocks.brewing_stand, Blocks.waterlily, Blocks.farmland, Blocks.sand, Blocks.beacon);
	    
    
    public Scaffold() {
    super("Scaffold", 0, 0x005C00, Module.Category.MISCELLANEOUS,"Places block below you in the specified direction.", new String[] { "scw", "swalk", "tower", "sc" },true);
    }
 
    public static double getBaseMoveSpeed() {
		double baseSpeed = 0.2873D;
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
		}
		return baseSpeed;
	}
 
    
	@Override
	public void onEnable() {
		super.onEnable();
	//	this.setSuffix(this.getBlockAmount()+"");
	//   if(fast.value) {
	//	   this.setSuffix("Fast");
//		   mc.timer.timerSpeed = (int)timersp.getValue();
//		   speedon=true;
	//   }
	   Helper.scaf = true;

	}
    
	@Override
	public void onDisable() {
		super.onDisable();
//	   if(Speeds.value&&speedon) {
//		   mc.timer.timerSpeed = 1.0F;
//		   speedon=false;
//	   }
		 Helper.scaf = false;
	}
	


	
	
    
    @EventTarget
    public void onPre(EventPreMotionUpdates event) {
        blockData = null;
      
            
               EventPreMotionUpdates em1 = event;
		//	if (em1.isPre()) {
            //    if(!Helper.player().movementInput.jump)
				mc.timer.timerSpeed = 1.085F;
				double forward = mc.thePlayer.movementInput.moveForward;
				double strafe = mc.thePlayer.movementInput.moveStrafe;
				if (((forward != 0.0D) || (strafe != 0.0D)) && (!mc.thePlayer.isJumping)
						&& (!mc.thePlayer.isInWater()) && (!mc.thePlayer.isOnLadder())
						&& (!mc.thePlayer.isCollidedHorizontally)) {
					em1.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 != 0 ? 0.4D : 0.0D));
				}
				this.moveSpeed = Math.max(mc.thePlayer.ticksExisted % 2 == 0 ? 2.1D : 1.3D, getBaseMoveSpeed());
				float yaw = mc.thePlayer.rotationYaw;
				if ((forward == 0.0D) && (strafe == 0.0D)) {
					mc.thePlayer.motionX = 0.0D;
					mc.thePlayer.motionZ = 0.0D;
				} else {
					if (forward != 0.0D) {
						if (strafe > 0.0D) {
							yaw += (forward > 0.0D ? -45 : 45);
						} else if (strafe < 0.0D) {
							yaw += (forward > 0.0D ? 45 : -45);
						}
						strafe = 0.0D;
						if (forward > 0.0D) {
							forward = 0.15D;
						} else if (forward < 0.0D) {
							forward = -0.15D;
						}
					}
					if (strafe > 0.0D) {
						strafe = 0.15D;
					} else if (strafe < 0.0D) {
						strafe = -0.15D;
					}
					if(mc.thePlayer.isMoving()){
						mc.gameSettings.keyBindSneak.pressed = false;
					}
					mc.thePlayer.motionX = (forward * this.moveSpeed * Math.cos(Math.toRadians(yaw + 90.0F))
							+ strafe * this.moveSpeed * Math.sin(Math.toRadians(yaw + 90.0F)));
					mc.thePlayer.motionZ = (forward * this.moveSpeed * Math.sin(Math.toRadians(yaw + 90.0F))
							- strafe * this.moveSpeed * Math.cos(Math.toRadians(yaw + 90.0F)));
					this.blockData = null;
	                double x = mc.thePlayer.posX;
	                double y = mc.thePlayer.posY - 1.0;
	                double z = mc.thePlayer.posZ;
	                double forward1 = mc.thePlayer.movementInput.moveForward;
	                double strafe1 = mc.thePlayer.movementInput.moveStrafe;
	                float yaw1 = mc.thePlayer.rotationYaw;
	                if ( mc.theWorld.getBlockState(blockBelow = new BlockPos(x, y, z)).getBlock() != Blocks.air && mc.theWorld.getBlockState(blockBelow = new BlockPos(x, y, z)).getBlock() != Blocks.snow_layer && mc.theWorld.getBlockState(blockBelow = new BlockPos(x += forward1 * 0.4 * Math.cos(Math.toRadians(yaw1 + 90.0f)) + strafe1 * 0.45 * Math.sin(Math.toRadians(yaw1 + 90.0f)), y, z += forward1 * 0.4 * Math.sin(Math.toRadians(yaw1 + 90.0f)) - strafe1 * 0.45 * Math.cos(Math.toRadians(yaw1 + 90.0f)))).getBlock() != Blocks.tallgrass || !this.timer.hasTicksElapsed((int) 100.0f)) return;
	                this.timer.reset();
	                this.blockData = this.getBlockData(blockBelow,blacklist);
	                if (this.blockData == null) return;
	                float[] rotations = getFacingRotations(this.blockData.position.getX(), this.blockData.position.getY(), this.blockData.position.getZ(), this.blockData.face);
	                event.setYaw(rotations[0]);
	                event.setPitch(rotations[1]);
	                return;
	            }
            
            
            
    }
   
    private float[] getFacingRotations(int paramInt1, int paramInt2, int paramInt3, EnumFacing paramEnumFacing) {
        EntityPig localEntityPig = new EntityPig(mc.theWorld);
        localEntityPig.posX = (double)paramInt1 + 0.5;
        localEntityPig.posY = (double)paramInt2 + 0.5;
        localEntityPig.posZ = (double)paramInt3 + 0.5;
        localEntityPig.posX += (double)paramEnumFacing.getDirectionVec().getX() * 0.25;
        localEntityPig.posY += (double)paramEnumFacing.getDirectionVec().getY() * 0.25;
        localEntityPig.posZ += (double)paramEnumFacing.getDirectionVec().getZ() * 0.25;
        return jdMethod_double(localEntityPig);
    }

    protected void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
    }
    
    private float[] jdMethod_double(EntityLivingBase paramEntityLivingBase) {
        double d1 = paramEntityLivingBase.posX - mc.thePlayer.posX;
        double d2 = paramEntityLivingBase.posY + (double)paramEntityLivingBase.getEyeHeight() - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
        double d3 = paramEntityLivingBase.posZ - mc.thePlayer.posZ;
        double d4 = MathHelper.sqrt_double(d1 * d1 + d3 * d3);
        float f1 = (float)(Math.atan2(d3, d1) * 180.0 / 3.141592653589793) - 90.0f;
        float f2 = (float)(- Math.atan2(d2, d4) * 180.0 / 3.141592653589793);
        return new float[]{f1, f2};
    }
    
    @EventTarget
    public void onPost(EventPostMotionUpdates post) {
        if (this.getBlockCount()>0 && Switch.value) {
        //	Logger.logChat("test");        	
     //   	if(!timerMotion.hasTimeElapsed(42))
     //   			return;
        //	timerMotion.reset();
        	  int i;
              if (!mc.gameSettings.keyBindJump.getIsKeyPressed()) {
                  this.timerMotion.reset();
              }
              if (tower.value && this.getBlockCount() > 0) {
            	  
                  mc.rightClickDelayTimer = 0;
                  if (Helper.player().movementInput.jump) {
                  	mc.timer.timerSpeed = 1.0f;
                      Helper.player().motionY = 0.42;
                      mc.thePlayer.motionX *= 0.3;
                      mc.thePlayer.motionZ *= 0.3;
                      if(timerMotion.hasTimeElapsed(1000)) {
                      	mc.timer.timerSpeed = 1.0f;
                          Helper.player().motionY = -0.28;
                          timerMotion.reset();
                          if(timerMotion.hasTimeElapsed(2)) {
                          	mc.timer.timerSpeed = 1.0f;
                              Helper.player().motionY = 0.42;
                          }
                      }
                  }
              }
              if (this.isHotbarEmpty()) {
                  for (i = 9; i < 36; ++i) {
                      Item item;
                      if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !((item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()) instanceof ItemBlock) || this.blacklist.contains(((ItemBlock)item).getBlock())) continue;
                      this.swap(i, 7);
                      break;
                  }
              }
              i = 36;
              while (i < 45) {
                  Item item;
                  ItemStack is;
                  if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && (item = (is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem()) instanceof ItemBlock && !this.blacklist.contains(((ItemBlock)item).getBlock()) && this.blockData != null) {
                      mc.rightClickDelayTimer = 2;
                      int last = mc.thePlayer.inventory.currentItem;
                      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i - 36));
                      mc.thePlayer.inventory.currentItem = i - 36;
                      mc.playerController.updateController();
                      if (mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), this.blockData.position, this.blockData.face, new Vec3(this.blockData.position.getX(), this.blockData.position.getY(), this.blockData.position.getZ()))) {
                          mc.thePlayer.swingItem();
                      }
                      mc.thePlayer.inventory.currentItem = last;
                      mc.playerController.updateController();
                      return;
                  }
                  ++i;
              }
              return;
         
        }
        }
    
    private boolean isHotbarEmpty() {
        for (int i = 36; i < 45; ++i) {
            Item item;
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !((item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()) instanceof ItemBlock) || this.blacklist.contains(((ItemBlock)item).getBlock())) continue;
            return false;
        }
        return true;
    }
    public static float rando05(long seed){
        seed = System.currentTimeMillis() + seed;
        return 0.30000000000f + (new Random(seed).nextInt(70000000) / 100000000.000000000000f) + 0.00000001458745f;
    }
   /* @EventTarget
    public void onPacket(EventPacketSend e){
    	if (e.getPacket() instanceof C03PacketPlayer) {
    		   float yaw = aimAtLocation((double) blockData.position.getX(),
                       (double) blockData.position.getY(), (double) blockData.position.getZ(),
                       blockData.face)[0];
               float pitch = aimAtLocation((double) blockData.position.getX(),
                       (double) blockData.position.getY(), (double) blockData.position.getZ(),
                       blockData.face)[1];
               ((C03PacketPlayer) e.getPacket()).yaw = yaw;
   				((C03PacketPlayer) e.getPacket()).pitch = pitch;
				if (e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
					C03PacketPlayer.C04PacketPlayerPosition playerPos = (C03PacketPlayer.C04PacketPlayerPosition) e.getPacket();
					playerPos.yaw = yaw;
					playerPos.pitch = pitch;
				} else if (e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
					C03PacketPlayer.C05PacketPlayerLook look = (C03PacketPlayer.C05PacketPlayerLook) e.getPacket();
					look.yaw = yaw;
					look.pitch = pitch;
				} else if (e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
					C03PacketPlayer.C06PacketPlayerPosLook posLook = (C03PacketPlayer.C06PacketPlayerPosLook) e.getPacket();
					posLook.yaw = yaw;
					posLook.pitch = pitch;
				}			
		}
    
    */
    
    public int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if (!(is.getItem() instanceof ItemBlock) || this.blacklist.contains(((ItemBlock)item).getBlock())) continue;
            blockCount += is.stackSize;
        }
        return blockCount;
    }

    private class BlockData {
        public BlockPos position;
        public EnumFacing face;

        private BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }
    
    private BlockData getBlockData(BlockPos pos, List list) {
        return !list.contains(Helper.world().getBlockState(pos.add(0, -1, 0)).getBlock())
                ? new BlockData(pos.add(0, -1, 0), EnumFacing.UP)
                : (!list.contains(Helper.world().getBlockState(pos.add(-1, 0, 0)).getBlock())
                        ? new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST)
                        : (!list.contains(Helper.world().getBlockState(pos.add(1, 0, 0)).getBlock())
                                ? new BlockData(pos.add(1, 0, 0), EnumFacing.WEST)
                                : (!list.contains(Helper.world().getBlockState(pos.add(0, 0, -1)).getBlock())
                                		
                                		? new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH)
                                        : (!list.contains(
                                        		Helper.world().getBlockState(pos.add(0, 0, 1)).getBlock())
                                                        ? new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH) : null))));
    }
    //This is cancer, but it's awesome.
    public BlockData getBlockData1(BlockPos pos) {
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, -1, 0)).getBlock())) {
            return new BlockData( pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
            return new BlockData( pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(-1, 0, -1)).getBlock())) {
            return new BlockData( pos.add(-1, 0, -1), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
            return new BlockData( pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(1, 0, 1)).getBlock())) {
            return new BlockData( pos.add(1, 0, 1), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
            return new BlockData( pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(-1, 0, -1)).getBlock())) {
            return new BlockData( pos.add(-1, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
            return new BlockData( pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(1, 0, 1)).getBlock())) {
            return new BlockData( pos.add(1, 0, 1), EnumFacing.NORTH);
        }
        final BlockPos add = pos.add(-1, 0, 0);
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add.add(-1, 0, 0)).getBlock())) {
            return new BlockData( add.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add.add(1, 0, 0)).getBlock())) {
            return new BlockData( add.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add.add(0, 0, -1)).getBlock())) {
            return new BlockData( add.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add.add(0, 0, 1)).getBlock())) {
            return new BlockData( add.add(0, 0, 1), EnumFacing.NORTH);
        }
        final BlockPos add2 = pos.add(1, 0, 0);
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add2.add(-1, 0, 0)).getBlock())) {
            return new BlockData( add2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add2.add(1, 0, 0)).getBlock())) {
            return new BlockData( add2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add2.add(0, 0, -1)).getBlock())) {
            return new BlockData( add2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add2.add(0, 0, 1)).getBlock())) {
            return new BlockData( add2.add(0, 0, 1), EnumFacing.NORTH);
        }
        final BlockPos add3 = pos.add(0, 0, -1);
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add3.add(-1, 0, 0)).getBlock())) {
            return new BlockData( add3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add3.add(1, 0, 0)).getBlock())) {
            return new BlockData(add3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add3.add(0, 0, -1)).getBlock())) {
            return new BlockData(add3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add3.add(0, 0, 1)).getBlock())) {
            return new BlockData(add3.add(0, 0, 1), EnumFacing.NORTH);
        }
        final BlockPos add4 = pos.add(0, 0, 1);
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add4.add(-1, 0, 0)).getBlock())) {
            return new BlockData(add4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add4.add(1, 0, 0)).getBlock())) {
            return new BlockData(add4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add4.add(0, 0, -1)).getBlock())) {
            return new BlockData(add4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add4.add(0, 0, 1)).getBlock())) {
            return new BlockData(add4.add(0, 0, 1), EnumFacing.NORTH);
        }
        BlockData blockData = null;
 
        return blockData;
    }
 

   
    private int getBlockAmount() {
        int n = 0;
        for (int i = 9; i < 45; ++i) {
            final ItemStack stack = Helper.player().inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock().isCollidable()) {
                n += stack.stackSize;
            }
        }
        return n;
    }
    
    
   
    private float[] aimAtLocation(double x, double y, double z, EnumFacing facing) {
        EntitySnowball temp = new EntitySnowball(Helper.world());
        temp.posX = x + 0.5D;
        temp.posY = y - 2.70352523530000001D;
        temp.posZ = z + 0.5D;
        temp.posX += (double) facing.getDirectionVec().getX() * 0.25D;
        temp.posY += (double) facing.getDirectionVec().getY() * 0.25D;
        temp.posZ += (double) facing.getDirectionVec().getZ() * 0.25D;
        return aimAtLocation(temp.posX, temp.posY, temp.posZ);
    }
 
    private float[] aimAtLocation(double positionX, double positionY, double positionZ) {
        double x = positionX - Helper.player().posX;
        double y = positionY - Helper.player().posY;
        double z = positionZ - Helper.player().posZ;
        double distance = (double) MathHelper.sqrt_double(x * x + z * z);
        return new float[] { (float) (Math.atan2(z, x) * 180.0D / 3.141592653589793D) - 90.0F,
                (float) (-(Math.atan2(y, distance) * 180.0D / 3.141592653589793D)) };
    }
    
    //timersp
    protected void addCommand() {
		Pandora.getCommandManager().cmds.add(new Command("Scaffold", "Manages scaffold stuff",
				Logger.LogExecutionFail("Option, Options:", new String[] { "Switch, Hypixel" }), "sc", "scaff",
				"autop") {
			@Override
			public void execute(String commandName, String[] arguments) {
				String message = arguments[1];
				switch (message) {
				case "switch":
				case "s":
				case "as":
                    break;
				case "speed":
				case "sp":
				case "spee":
				case "spe":
                    String message2 = arguments[1];
                    Integer sD = Integer.parseInt(message2);
					timersp.setValue(sD);
					Logger.logSetMessage("MotionSpeed","Switch Delay", timersp);
                    break;
				case "hypixel":
				case "h":
				case "watchdog":
				case "wd":
					Hypixel.value = !Hypixel.value;
                    Logger.logToggleMessage("Hypixel", Hypixel.value);
                    break;
				case "values":
				case "actual":
					logValues();
					break;
				default:
					Logger.LogExecutionFail("Option, Options:", new String[] { "Health", "Delay", "Values" });
					break;

				}
			}
		});
	}
}
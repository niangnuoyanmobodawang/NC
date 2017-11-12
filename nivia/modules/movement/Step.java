package nivia.modules.movement;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import nivia.Pandora;
import nivia.commands.Command;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.events.events.EventStep;
import nivia.events.events.EventTick;
import nivia.managers.PropertyManager;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.Wrapper;

import java.util.HashMap;
import java.util.Map;

public class Step extends Module {
	public Property<StepMode> stepMode = new Property<StepMode>(this, "StepMode", StepMode.NEW2);
	public Property<Boolean> reverse = new Property<Boolean>(this, "Reverse", false);
    private Map<EntityPlayerSP, PlayerData> data = new HashMap<>();
	public PropertyManager.DoubleProperty height = new PropertyManager.DoubleProperty(this, "Height", 1.0f, 1.0F, 10, 1);
	public static int Delay = 0, jumpDelay;
	public static boolean stepJesus;

	public Step() {
		super("Step", Keyboard.KEY_N, 0x0A84C4, Category.MOVEMENT, "Step up full blocks", new String[] { "stp" },
				false);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		Entity.stepModule = 0;
		mc.thePlayer.stepHeight = 0.5F;
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	public static enum StepMode {
		OLD, NEW , NEW2;
		public String getName(StepMode s) {
			String name = "";
			switch (s) {
			case OLD:
				name = "Old Mode";
				break;
			case NEW:
				name = "New Mode";
				break;
			case NEW2:
				name = "New Mode2";
				break;
			}
			return name;
		}
	}

	@EventTarget(Priority.LOWEST)
	public void onTick(EventTick event) {
		mc.thePlayer.stepHeight = (float)height.getValue();
		if (jumpDelay < 11)
			jumpDelay++;
		if (mc.gameSettings.keyBindJump.getIsKeyPressed())
			jumpDelay = 0;
		if (Helper.mc().thePlayer != null)
			Delay++;
		// TODO: Reverse Step

		if(stepMode.value == StepMode.NEW2){
			
            return;
			
		}
		
		
		if (reverse.value && !Pandora.getModManager().getModState("Glide")
				&& (Helper.playerUtils().getDistanceToFall() < 1.3 && Helper.playerUtils().getDistanceToFall() > 0.9) && Delay >= 8
				&& (Helper.blockUtils().isBlockUnderPlayer(Material.air, 0.9F)
						&& !Helper.blockUtils().isBlockUnderPlayer(Material.air, 1.1F))
				&& jumpDelay > 10 && mc.thePlayer.isCollidedVertically) {
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.42, mc.thePlayer.posZ);
		}
		
		
		
		if (stepMode.value != StepMode.OLD)
			return;
		boolean canStep = false;
		stepJesus = false;
		if (Helper.mc().thePlayer.onGround)
			canStep = true;
		if (Helper.mc().gameSettings.keyBindJump.isPressed())
			Delay = 1;
		if (canStep && Helper.blockUtils().getBlockUnderPlayer(mc.thePlayer, 0.01).getMaterial() == Material.water
				|| Helper.blockUtils().getBlockUnderPlayer(mc.thePlayer, 0.01).getMaterial() == Material.lava) {
			stepJesus = true;
			Entity.stepModule = 0.075F;
			Helper.mc().thePlayer.stepHeight = (float)height.getValue();
		} else if (canStep && Delay > 1 && !Helper.blockUtils().isOnLiquid()) {
			Entity.stepModule = 0.075F;
			Delay = 0;
			Helper.mc().thePlayer.stepHeight = (float) height.getValue();
		} else {
			Helper.mc().thePlayer.stepHeight = 0.5F;
			Entity.stepModule = 0;
		}
	}
	
	@EventTarget
	public void onPre(EventPreMotionUpdates e) {
		PlayerData pData = data.get(Wrapper.getPlayer());
		EventPreMotionUpdates es = (EventPreMotionUpdates)e;
		if(stepMode.value == StepMode.NEW2){
    //        es.setStepHeight(1.0D);
    //        es.setActive(true);
		}
		if (pData == null)
			data.put(Wrapper.getPlayer(), pData = new PlayerData(0, false, new double[0], Wrapper.getPlayer().posX, Wrapper.getPlayer().posY, Wrapper.getPlayer().posZ));
		if (pData.doStep)
			e.setCancelled(true);
		return;
	}
	
	/*     */   public Block getBlock(AxisAlignedBB bb)
	/*     */   {
	/* 116 */     int y = (int)bb.minY;
	/* 117 */     for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; x++) {
	/* 118 */       for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; z++)
	/*     */       {
	/* 120 */         Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
	/* 121 */         if (block != null) {
	/* 122 */           return block;
	/*     */         }
	/*     */       }
	/*     */     }
	/* 126 */     return null;
	/*     */   }
	/*     */   
	/*     */   public Block getBlock(double offsetX, double offsetY, double offsetZ)
	/*     */   {
	/* 131 */     return getBlock(mc.thePlayer.getEntityBoundingBox().offset(offsetX, offsetY, offsetZ));
	/*     */   }
	
	@EventTarget
	public void onPost(EventPostMotionUpdates e) {
        PlayerData pData = data.get(Wrapper.getPlayer());
        if (pData == null) {
            data.put(Wrapper.getPlayer(), pData = new PlayerData(0, false, new double[0], Wrapper.getPlayer().posX, Wrapper.getPlayer().posY, Wrapper.getPlayer().posZ));
        }
        if ((mc.thePlayer.movementInput != null) && (!mc.thePlayer.movementInput.jump) && 
        		/*  51 */       (mc.thePlayer.isCollidedHorizontally))
        		/*     */     {
        		/*  53 */       double x = 0.0D;
        		/*  54 */       double z = 0.0D;
        		/*  55 */       EnumFacing yawF = EnumFacing.getHorizontal(MathHelper.floor_double(mc.thePlayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3);
        		/*  56 */       if ((yawF == EnumFacing.EAST) && (mc.gameSettings.keyBindForward.pressed)) {
        		/*  57 */         x += 1.0D;
        		/*  58 */       } else if ((yawF == EnumFacing.EAST) && (mc.gameSettings.keyBindRight.pressed)) {
        		/*  59 */         z += 1.0D;
        		/*  60 */       } else if ((yawF == EnumFacing.EAST) && (mc.gameSettings.keyBindLeft.pressed)) {
        		/*  61 */         z -= 1.0D;
        		/*  62 */       } else if ((yawF == EnumFacing.EAST) && (mc.gameSettings.keyBindBack.pressed)) {
        		/*  63 */         x -= 1.0D;
        		/*  64 */       } else if ((yawF == EnumFacing.WEST) && (mc.gameSettings.keyBindForward.pressed)) {
        		/*  65 */         x -= 1.0D;
        		/*  66 */       } else if ((yawF == EnumFacing.WEST) && (mc.gameSettings.keyBindRight.pressed)) {
        		/*  67 */         z -= 1.0D;
        		/*  68 */       } else if ((yawF == EnumFacing.WEST) && (mc.gameSettings.keyBindLeft.pressed)) {
        		/*  69 */         z += 1.0D;
        		/*  70 */       } else if ((yawF == EnumFacing.WEST) && (mc.gameSettings.keyBindBack.pressed)) {
        		/*  71 */         x += 1.0D;
        		/*  72 */       } else if ((yawF == EnumFacing.NORTH) && (mc.gameSettings.keyBindForward.pressed)) {
        		/*  73 */         z -= 1.0D;
        		/*  74 */       } else if ((yawF == EnumFacing.NORTH) && (mc.gameSettings.keyBindRight.pressed)) {
        		/*  75 */         x += 1.0D;
        		/*  76 */       } else if ((yawF == EnumFacing.NORTH) && (mc.gameSettings.keyBindLeft.pressed)) {
        		/*  77 */         x -= 1.0D;
        		/*  78 */       } else if ((yawF == EnumFacing.NORTH) && (mc.gameSettings.keyBindBack.pressed)) {
        		/*  79 */         z += 1.0D;
        		/*  80 */       } else if ((yawF == EnumFacing.SOUTH) && (mc.gameSettings.keyBindForward.pressed)) {
        		/*  81 */         z += 1.0D;
        		/*  82 */       } else if ((yawF == EnumFacing.SOUTH) && (mc.gameSettings.keyBindRight.pressed)) {
        		/*  83 */         x -= 1.0D;
        		/*  84 */       } else if ((yawF == EnumFacing.SOUTH) && (mc.gameSettings.keyBindLeft.pressed)) {
        		/*  85 */         x += 1.0D;
        		/*  86 */       } else if ((yawF == EnumFacing.SOUTH) && (mc.gameSettings.keyBindBack.pressed)) {
        		/*  87 */         z -= 1.0D;
        		/*     */       }
        		/*  89 */       Block step = getBlock(x, 0.0D, z);
        		/*  90 */       Block check = getBlock(x, 1.0D, z);
        		/*  91 */       Block over = getBlock(0.0D, 2.0D, 0.0D);
        		/*     */       
        		/*  93 */       if ((check.getMaterial() == Material.air) && (step.getMaterial() != Material.air) && (over.getMaterial() == Material.air) && (!mc.thePlayer.isOnLadder()))
        		/*     */       {
        		/*  95 */         mc.thePlayer.stepHeight = 100000.0F;
        		/*  96 */         mc.thePlayer.sendQueue
        		/*  97 */           .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
        		/*  98 */           mc.thePlayer.posY + 0.42D, mc.thePlayer.posZ, mc.thePlayer.onGround));
        		/*  99 */         mc.thePlayer.sendQueue
        		/* 100 */           .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
        		/* 101 */           mc.thePlayer.posY + 0.753D, mc.thePlayer.posZ, mc.thePlayer.onGround));
        		/*     */       }
        		/*     */       else
        		/*     */       {
        		/* 105 */         mc.thePlayer.stepHeight = 0.5F;
        		/*     */       }
        		/*     */     }
        		/*     */     else
        		/*     */     {
        		/* 110 */       mc.thePlayer.stepHeight = 0.5F;
        		/*     */     }
  //      EventPostMotionUpdates es = (EventPostMotionUpdates)e;

        pData.x = Wrapper.getPlayer().posX;
        pData.y = Wrapper.getPlayer().posY;
        pData.z = Wrapper.getPlayer().posZ;
        if (!pData.doStep)
        	pData.tick = 0;
        
        if (pData.doStep) {
        	if (pData.a15.length > 0) {
        		Wrapper.getPlayer().setPosition(pData.x, pData.y + pData.a15[pData.tick], pData.z);
        		pData.tick++;
        		if (pData.tick == pData.a15.length) {
        			pData.doStep = false;
        			pData.tick = 0;
        			Wrapper.getPlayer().setPosition(pData.x, pData.y + pData.a15[pData.a15.length - 1], pData.z);
        		}
        	}
        }
        if (pData.doStep) {
        	if (pData.doStep)
        		Wrapper.getPlayer().setPosition(pData.x, pData.y, pData.z);
        }
        return;
	}
	
	@EventTarget
	public void onStep(EventStep e) {
		Wrapper.getPlayer().stepHeight = Wrapper.getPlayer().isInWater() || !Wrapper.getPlayer().onGround ? 0.6f : 2f;
		PlayerData pData = data.get(Wrapper.getPlayer());
        EventStep es = (EventStep)e;
        if (!mc.thePlayer.movementInput.jump && mc.thePlayer.isCollidedVertically) {
           es.setStepHeight((float) 1.0D);
        } else if (es.getStepHeight() >= 0.9D && es.getStepHeight() > 0.0D) {
           double realHeight = es.getStepHeight();
           double height1 = realHeight * 0.42D;
           double height2 = realHeight * 0.75D;
           mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + height1, mc.thePlayer.posZ, mc.thePlayer.onGround));
           mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + height2, mc.thePlayer.posZ, mc.thePlayer.onGround));
           mc.timer.timerSpeed = 0.37F;
           (new Thread(() -> {
              try {
                 Thread.sleep(125L);
              } catch (InterruptedException var1) {
                 ;
              }

              mc.timer.timerSpeed = 1.0F;
           })).start();
        }
		if (pData == null)
			data.put(Wrapper.getPlayer(), pData = new PlayerData(0, false, new double[0], Wrapper.getPlayer().posX, Wrapper.getPlayer().posY, Wrapper.getPlayer().posZ));
		
	}
	
    private class PlayerData {
        private int tick;
        private boolean doStep;
        private double[] a15;
        private double x, y, z;

        private PlayerData(int tick, boolean doStep, double[] a15, double x, double y, double z) {
            this.tick = tick;
            this.doStep = doStep;
            this.a15 = a15;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

	protected void addCommand() {
		Pandora.getCommandManager().cmds.add(new Command("Step", "Manages step",
				Logger.LogExecutionFail("Option, Options:", new String[] { "old", "new", "reverse", "height" }), "st") {
			@Override
			public void execute(String commandName, String[] arguments) {
				String message = arguments[1], message2 = "";
				try {
					message2 = arguments[2];
				} catch (Exception e) {
				}
				switch (message) {
				case "height":
				case "Height":
				case "h":
					switch (message2) {
					case "actual":
						logValue(height);
						break;
					case "reset":
						height.reset();
						break;
					default:
						height.setValue(Float.parseFloat(message2));
						Logger.logSetMessage("Step", "Height", height);
						if (stepMode.value == StepMode.NEW)
							mc.thePlayer.stepHeight = (float)height.getValue();
						break;
					}
					break;
				case "reverse":
				case "Reverse":
				case "r":
					reverse.value = !reverse.value;
					Logger.logSetMessage("Step", "Reverse", reverse);
					break;
				case "Old":
				case "old":
				case "o":
					stepMode.value = StepMode.OLD;
					Logger.logChat(
							"Set Step mode to " + EnumChatFormatting.AQUA + stepMode.value.getName(stepMode.value));
					break;
				case "New":
				case "new":
				case "n":
					stepMode.value = StepMode.NEW;
					Logger.logChat(
							"Set Step mode to " + EnumChatFormatting.AQUA + stepMode.value.getName(stepMode.value));
					break;
				case "values":
					logValues();
					break;
				default:
					Logger.logChat(this.getError());
				}
			}
		});
	}
}
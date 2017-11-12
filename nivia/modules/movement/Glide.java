package nivia.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import nivia.Pandora;
import nivia.commands.Command;
import nivia.commands.commands.Damage;
import nivia.events.EventTarget;
import nivia.events.events.EventMove;
import nivia.events.events.EventPacketSend;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.modules.exploits.NoFall.Mode;
import nivia.utils.Helper;
import nivia.utils.Logger;


public class Glide extends Module {
	public Glide() {
		super("Glide", 0, 0x75FF47, Category.MOVEMENT, "Glide down the specified speed.",
				new String[] { "glide", "gl" }, true);
	}

    private double moveSpeed;
	public DoubleProperty glideSpeed = new DoubleProperty(this, "Speed", 0.03145, 0, 10, 0.1);
	public DoubleProperty verticalSpeed = new DoubleProperty(this, "Vertical Speed", 0.4, 0, 10, 0.1);
	public DoubleProperty horizontalSpeed = new DoubleProperty(this, "Horizontal Speed", 0.8D, 0, 10, 0.1);
	public Property<Boolean> lock = new Property<Boolean>(this, "Lock", true);
	public Property<Boolean> damage = new Property<Boolean>(this, "Damage", true);
	public Property<Boolean> bypass = new Property<Boolean>(this, "Bypass", true);
	public Property<Boolean> Tspeed = new Property<Boolean>(this, "TimerSpeed", true);
	public DoubleProperty MoSpeed = new DoubleProperty(this, "XZ Speed", 0.16D, 0, 10.0D, 0.1D);
	public DoubleProperty Forward = new DoubleProperty(this, "Forward Speed", 0.168D, 0, 0.99D, 0.1D);
	public DoubleProperty BForward = new DoubleProperty(this, "BackForward Speed", 0.2D, 0, 0.99D, 0.1D);
	public DoubleProperty strafe2 = new DoubleProperty(this, "Strafe Speed", 0.2D, 0, 0.99D, 0.1D);
	public DoubleProperty Bstrafe2 = new DoubleProperty(this, "BStrafe Speed", 0.2D, 0, 0.99D, 0.1D);
	
	
	public Property<Boolean> lemon = new Property<Boolean>(this, "Lemon", true);
	private double maxPosY = 0;

	
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
		maxPosY = mc.thePlayer.posY;
		if (damage.value)
			Pandora.getCommandManager().getCommand(Damage.class).execute("Damage", new String[] { "", "0" });
	Logger.logChat("使用-glide strafe/bstrafe/forward/bforward 来设置");
	    if(mc.thePlayer.onGround && bypass.value){
	    	mc.thePlayer.motionY = 0.000000001f;
	        mc.thePlayer.moveForward = -0.000000001f;
	        mc.thePlayer.swingItem();
	    }
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		mc.timer.timerSpeed = 1.0f; 
	}
	
    @EventTarget
    public void packetSend(EventPacketSend event) {        
        if (bypass.value) {
        	this.setSuffix("HypixelNew");
            if (event.getPacket() instanceof C03PacketPlayer) {
                if (!mc.thePlayer.onGround && mc.thePlayer.moveForward >= 0.0F && mc.thePlayer.getHealth() == mc.thePlayer.getMaxHealth()) {
                    C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();
                    packet.field_149474_g = true;
        //            ((nivia.modules.player.Timer) Pandora.getModManager().getModule(nivia.modules.player.Timer.class)).keepAlives.add(event.getPacket());
                }
            }  
        }
    }
	
	@EventTarget
	public void onEvent(EventPreMotionUpdates pre) {
		if (!mc.thePlayer.isEntityAlive())
			return;
		//if(Tspeed.value){
		//	mc.timer.timerSpeed = (float) MoSpeed.getValue();
		//    mc.thePlayer.motionX *= MxSpeed.getValue();
		//    mc.thePlayer.motionZ *= MzSpeed.getValue();
			 
		boolean shouldBlock = (!((mc.thePlayer.posY + 0.1) < maxPosY) && mc.gameSettings.keyBindJump.getIsKeyPressed())
				&& lock.value;
		boolean isGliding = !mc.thePlayer.onGround && !mc.thePlayer.isCollidedVertically;
		if (mc.thePlayer.isSneaking())
			mc.thePlayer.motionY = -verticalSpeed.getValue();
		else if (mc.gameSettings.keyBindJump.getIsKeyPressed() && !shouldBlock)
			mc.thePlayer.motionY = verticalSpeed.getValue();
		else {
			double speed = glideSpeed.getValue();
			double x = 0;
			x++;
			if (Helper.blockUtils().isInsideBlock())
				speed = 0;
	
			mc.thePlayer.motionY = -speed;
			if(lemon.value){
				if(mc.thePlayer.ticksExisted % 3 == 0) {
					pre.setY(mc.thePlayer.posY - 0.000000001);
					pre.setPitch(40f);
					pre.setGround(true);
				} else {
					pre.setY(mc.thePlayer.posY);
					pre.setGround(false);
				}
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.000000001, mc.thePlayer.posZ);
			}
		}
	}

	@EventTarget
	public void onMove(EventMove event) {
		if (Pandora.getModManager().getModState("Speed") && mc.thePlayer.onGround)
			return;

			//	mc.timer.timerSpeed = (float) MoSpeed.getValue();
			//    mc.thePlayer.motionX *= MxSpeed.getValue();
			//    mc.thePlayer.motionZ *= MzSpeed.getValue();
		 EventMove em1 = event;
			//	if (em1.isPre()) {
	            //    if(!Helper.player().movementInput.jump)
					mc.timer.timerSpeed = 1.085F;
					double forward = mc.thePlayer.movementInput.moveForward;
					double strafe = mc.thePlayer.movementInput.moveStrafe;
					if (((forward != 0.0D) || (strafe != 0.0D)) && (!mc.thePlayer.isJumping)
							&& (!mc.thePlayer.isInWater()) && (!mc.thePlayer.isOnLadder())
							&& (!mc.thePlayer.isCollidedHorizontally)) {
			//			em1.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 != 0 ? 0.4D : 0.0D));
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
								forward = Forward.getValue();
							} else if (forward < 0.0D) {
								forward = -BForward.getValue();
							}
						}
						if (strafe > 0.0D) {
							strafe = strafe2.getValue();
						} else if (strafe < 0.0D) {
							strafe = -Bstrafe2.getValue();
						}
						if(mc.thePlayer.isMoving()){
							mc.gameSettings.keyBindSneak.pressed = false;
						}
						mc.thePlayer.motionX = (forward * this.moveSpeed * Math.cos(Math.toRadians(yaw + 90.0F))
								+ strafe * (this.moveSpeed+MoSpeed.getValue()) * Math.sin(Math.toRadians(yaw + 90.0F)));
						mc.thePlayer.motionZ = (forward * this.moveSpeed * Math.sin(Math.toRadians(yaw + 90.0F))
								- strafe * (this.moveSpeed+MoSpeed.getValue()) * Math.cos(Math.toRadians(yaw + 90.0F)));
					}
	/*
						
		double forward = mc.thePlayer.movementInput.moveForward;
		double strafe = mc.thePlayer.movementInput.moveStrafe;
		float yaw = mc.thePlayer.rotationYaw;
		if (forward == 0.0 && strafe == 0.0) {
			event.x = (0.0);
			event.z = (0.0);
	
			if (forward != 0.0) {
				if (strafe > 0.0)
					yaw += ((forward > 0.0) ? -45 : 45);
				else if (strafe < 0.0)
					yaw += ((forward > 0.0) ? 45 : -45);

				strafe = 0.0;

				if (forward > 0.0)
					forward = 1.0;
				else if (forward < 0.0)
					forward = -1.0;
			}
			event.x = (forward * horizontalSpeed.getValue() * Math.cos(Math.toRadians(yaw + 90.0f))
					+ strafe * horizontalSpeed.getValue() * Math.sin(Math.toRadians(yaw + 90.0f)));
			event.z = (forward * horizontalSpeed.getValue() * Math.sin(Math.toRadians(yaw + 90.0f))
					- strafe * horizontalSpeed.getValue() * Math.cos(Math.toRadians(yaw + 90.0f)));
			
		}
		
			*/
	}

	protected void addCommand() {
		Pandora.getCommandManager().cmds
				.add(new Command("Glide", "Manages glide", Logger.LogExecutionFail("Option, Options:",
						new String[] { "Vertical Speed", "Glide Speed", "Lock", "Values" }), "gl") {
					@Override
					public void execute(String commandName, String[] arguments) {
						String message = arguments[1], message2 = "";
						try {
							message2 = arguments[2];
						} catch (Exception e) {
						}
						switch (message.toLowerCase()) {
						case "lock":
						case "l":
							lock.value = !lock.value;
							if (lock.value)
								maxPosY = mc.thePlayer.posY;
							Logger.logSetMessage("Glide", "Lock", lock);
							break;
						case "damage":
						case "dmg":
							damage.value = !damage.value;
							Logger.logSetMessage("Glide", "Damage", damage);
							break;
						case "forward":
						case "forwar":
						case "forwa":
						case "for":
							Forward.setValue(Double.parseDouble(message2));
							break;
						case "bforward":
						case "bforwar":
						case "backforwar":
						case "bfor":
							BForward.setValue(Double.parseDouble(message2));
							break;	
						case "strafe":
						case "stra":
							strafe2.setValue(Double.parseDouble(message2));
							break;
						case "bstrafe":
						case "backstra":
						case "bstra":
							Bstrafe2.setValue(Double.parseDouble(message2));
							break;
						case "vspeed":
						case "verticalspeed":
						case "vs":
							switch (message2) {
							case "actual":
								logValue(verticalSpeed);
								break;
							case "reset":
								verticalSpeed.reset();
								break;
							default:
								verticalSpeed.setValue(Double.parseDouble(message2));
								Logger.logSetMessage("Glide", "Vertical Speed", verticalSpeed);
								break;
							}
							break;
						case "hspeed":
						case "horizontalspeed":
						case "hs":
							switch (message2) {
							case "actual":
								logValue(horizontalSpeed);
								break;
							case "reset":
								horizontalSpeed.reset();
								break;
							default:
								horizontalSpeed.setValue(Double.parseDouble(message2));
								;
								Logger.logSetMessage("Glide", "Horizontal Speed", horizontalSpeed);
								break;
							}
							break;
						case "gspeed":
						case "gs":
						case "glidespeed":
							switch (message2) {
							case "actual":
								logValue(glideSpeed);
								break;
							case "reset":
								glideSpeed.reset();
								break;
							default:
								glideSpeed.setValue(Double.parseDouble(message2));
								Logger.logSetMessage("Glide", "Glide Speed", glideSpeed);
								break;
							}
							break;
						case "values":
						case "actual":
							logValues();
							break;
						default:
							Logger.logChat(this.getError());
							break;
						}
					}
				});
	}
}

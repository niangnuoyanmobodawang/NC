package nivia.modules.movement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
/*     */ import net.minecraft.block.BlockBeacon;
/*     */ import net.minecraft.block.BlockBed;
/*     */ import net.minecraft.block.state.IBlockState;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.client.entity.EntityPlayerSP;
/*     */ import net.minecraft.client.multiplayer.PlayerControllerMP;
/*     */ import net.minecraft.client.multiplayer.WorldClient;
/*     */ import net.minecraft.client.network.NetHandlerPlayClient;
/*     */ import net.minecraft.network.NetworkManager;
/*     */ import net.minecraft.network.play.client.C03PacketPlayer;
/*     */ import net.minecraft.network.play.client.C07PacketPlayerDigging;
/*     */ import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
/*     */ import net.minecraft.network.play.client.C0APacketAnimation;
/*     */ import net.minecraft.util.BlockPos;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.MathHelper;
import nivia.Pandora;
import nivia.commands.Command;
import nivia.events.Event;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventMove;
import nivia.events.events.EventPacketReceive;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.events.events.EventTick;
import nivia.managers.PropertyManager;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.modules.movement.Speed.speedMode;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.TimeHelper;
import nivia.utils.utils.BlockUtils;
import nivia.utils.utils.MathUtils;
import nivia.utils.utils.Timer;

public class TestSpeed extends Module {
	   private static Block currentBlock;
	   private float currentDamage;
	   private EnumFacing side = EnumFacing.UP;
	   private byte blockHitDelay = 0;
	   private BlockPos pos;
	   /*     */   private int posX;
	   /*     */   private int posY;
	   /*     */   private int posZ;
	   /*     */   float pitch;
	   /*     */   float yaw;
	   Timer stimer = new Timer();
		public Property<Boolean> aaca = new Property<Boolean>(this, "aac", true);
		public Property<Boolean> bypass = new Property<Boolean>(this, "bypass", true);
		private DoubleProperty HorizontalV = new DoubleProperty(this, "Hvel", 0.0D, -3.00, 3.00, 1);
		private DoubleProperty VerticalV = new DoubleProperty(this, "Vvel", 0.0D, -3.00, 3.00, 1);

	  public TestSpeed() {
		super("NoVelocity", 0, 0, Category.MISCELLANEOUS, "Fucker, run.", new String[] { "run", "spr" }, true);
	}
	
		@EventTarget
		public void call(EventPacketReceive e) {
			if(e.isCancelled())
				return;
			if(aaca.value){
				if (mc.thePlayer.hurtTime == 1 || mc.thePlayer.hurtTime == 2 || mc.thePlayer.hurtTime == 3 || mc.thePlayer.hurtTime == 4 || mc.thePlayer.hurtTime == 5 || mc.thePlayer.hurtTime == 6 || mc.thePlayer.hurtTime == 7 || mc.thePlayer.hurtTime == 8) {
					double yaw = mc.thePlayer.rotationYawHead;
					yaw = Math.toRadians(yaw);
					final double dX = -Math.sin(yaw) * 0.05;
					final double dZ = Math.cos(yaw) * 0.05;
					mc.thePlayer.motionX = dX;
					mc.thePlayer.motionZ = dZ;
	    	}
		}else{
			if(bypass.value){
			        S12PacketEntityVelocity packet;
			        if (e.getPacket() instanceof S12PacketEntityVelocity && (packet = (S12PacketEntityVelocity)e.getPacket()).func_149412_c() == mc.thePlayer.getEntityId()) {
			            int vertical = (int) VerticalV.getValue();
			            int horizontal = (int) HorizontalV.getValue();
			            if (vertical != 0 || horizontal != 0) {
			            	packet.setMotX(horizontal * packet.func_149412_c() / 100);
			                packet.setMotY(vertical * packet.func_149411_d() / 100);
			                packet.setMotZ(horizontal * packet.func_149410_e() / 100);
			            } else {
			                e.setCancelled(true);
			            }
			        }
			        if (e.getPacket() instanceof S27PacketExplosion) {
			            e.setCancelled(true);
			        }
			}else{
			if (e.getPacket() instanceof S12PacketEntityVelocity
					&& ((S12PacketEntityVelocity) e.getPacket()).func_149412_c() == this.mc.thePlayer.getEntityId()) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				packet.field_149415_b = (int) (packet.field_149415_b * HorizontalV.getValue());
				packet.field_149416_c = (int) (packet.field_149416_c * VerticalV.getValue());
				packet.field_149414_d = (int) (packet.field_149414_d * HorizontalV.getValue());
				if (packet.field_149415_b == 0 && packet.field_149416_c == 0 && packet.field_149414_d == 0)
					e.setCancelled(true);
			}
			if (e.getPacket() instanceof S27PacketExplosion) {
				S27PacketExplosion packetExplosion = (S27PacketExplosion) e.getPacket();
				packetExplosion.field_149152_f = packetExplosion.field_149153_g = packetExplosion.field_149159_h = 0;
			}
			}
		 }
		}

		protected void addCommand() {
			Pandora.getCommandManager().cmds.add(new Command("NoVelocity", "Manages velocity",
					Logger.LogExecutionFail("Option, Options:", new String[] { "Vertical Speed", "Horizontal Speed" }),
					"antivel", "novel", "avel", "velocity", "vel", "nov") {
				@Override
				public void execute(String commandName, String[] arguments) {
					String message = arguments[1], message2 = "";
					try {
						message2 = arguments[2];
					} catch (Exception e) {
					}
					switch (message.toLowerCase()) {
					case "vspeed":
					case "verticalspeed":
					case "vs":
					case "vv":
					case "verticalvelocity":
					case "verticalvel":
						switch (message2) {
						case "actual":
							logValue(VerticalV);
							break;
						case "reset":
							VerticalV.reset();
							break;
						default:
							double vS = (Double.parseDouble(message2));
							VerticalV.setValue(vS);
							Logger.logSetMessage("Velocity", "Vertical velocity", VerticalV);
							VerticalV.setValue(vS * 0.01);
							break;
						}
						break;
					case "hspeed":
					case "horizontalspeed":
					case "hs":
					case "hv":
					case "horizontalvelocity":
					case "horizontalvel":
						switch (message2) {
						case "actual":
							logValue(HorizontalV);
							break;
						case "reset":
							HorizontalV.reset();
							break;
						default:
							double hS = (Double.parseDouble(message2));
							HorizontalV.setValue(hS);
							Logger.logSetMessage("Velocity", "Horizontal velocity", HorizontalV);
							HorizontalV.setValue(hS * 0.01);
							break;
						}
						break;
					case "values":
						logValues();
						break;
					default:
						Logger.logChat(this.getError());
						break;
					}
				}
			});
		}


	public void jump() {
		mc.thePlayer.motionY = 0.42f;
	}
}

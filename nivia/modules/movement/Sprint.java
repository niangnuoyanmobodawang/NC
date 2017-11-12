package nivia.modules.movement;

import java.util.Random;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import nivia.Pandora;
import nivia.events.EventTarget;
import nivia.events.events.EventPacketReceive;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.PropertyManager;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Helper;

public class Sprint extends Module {
    private Property<Boolean> keepSpirit = new Property<>(this, "Keep Sprint", false);
    
	
	public Sprint() {
		super("Sprint", 0, 0x75FF47, Category.MOVEMENT, "Run nigger, run.", new String[] { "run", "spr" }, true);
	}

//	public PropertyManager.Property<Boolean> omni = new PropertyManager.Property<Boolean>(this, "Omni Directional", true);

	@EventTarget
	public void onEvent(EventPreMotionUpdates pre) {

		if ((mc.thePlayer.moveForward > 0.0F) && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally && canSprint() ) {
				if(mc.thePlayer.moveForward <= 0.0F && mc.thePlayer.isCollidedVertically && !Pandora.getModManager().getModState("Speed")){
				mc.thePlayer.motionX *= 1.185;
				mc.thePlayer.motionZ *= 1.185;
			}
			mc.thePlayer.setSprinting(true);
		} else if(new Random().nextBoolean()){
			mc.thePlayer.setSprinting(false);
		}
		
	}
	
	private boolean canSprint() {
		return mc.thePlayer.isMoving() && mc.thePlayer.getFoodStats().getFoodLevel() > 6;
	}
	
	@EventTarget
	 public void onEvent(EventPacketReceive event) {
		if(!keepSpirit.value)
			return;
		EventPacketReceive e = (EventPacketReceive)event;
		      if (!e.isCancelled() && e.getPacket() instanceof C0BPacketEntityAction) {
		         C0BPacketEntityAction packet = (C0BPacketEntityAction)e.getPacket();
		         if (packet.func_180764_b() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
		            e.setCancelled(true);
		         }
		      }

		   }
	

	@EventTarget
	public void onPost(EventPostMotionUpdates post) {

	}
}

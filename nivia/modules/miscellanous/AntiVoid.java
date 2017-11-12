package nivia.modules.miscellanous;

import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import nivia.Pandora;
import nivia.events.EventTarget;
import nivia.events.events.EventPacketReceive;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.FriendManager.Friend;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.utils.Timer;

public class AntiVoid extends Module {
	public AntiVoid() {
		super("AntiVoid", 0, 1, Category.MISCELLANEOUS, "Accepts friend's tpas automatically",
				new String[] { "aacce2pt", "acc2ept" }, true);
	}
	public Property<Boolean> lag = new Property<Boolean>(this, "Lagback", true);
	public DoubleProperty fallDistance = new DoubleProperty(this, "FallDistance", 5, 1, 13);
	public DoubleProperty wtime = new DoubleProperty(this, "WaitTime", 105, 15, 5900);
	public static Timer timer = new Timer();
	public static boolean saveMe;
	
	@EventTarget
	  public void onReceivePacket(EventPreMotionUpdates e) {	   
		if (this.timer.hasTicksElapsed((int) wtime.getValue()) && mc.thePlayer.fallDistance > fallDistance.getValue()
				&& !mc.thePlayer.isJumping && !Pandora.getModManager().getModState("Glide") && !Pandora.getModManager().getModState("Fly")) {
			if (!this.isBlockUnder()) {
				this.saveMe = true;
			}
		} else if (this.saveMe) {
			mc.thePlayer.motionY = 0.0;
			this.timer.reset();
			this.saveMe = false;
		}
		this.setSuffix((mc.thePlayer.fallDistance > fallDistance.getValue() ? "\u00a7c" : "\u00a78")
				+ Integer.toString((int) mc.thePlayer.fallDistance));   
	  }
	  
	private boolean isBlockUnder() {
		for (int i = (int) (mc.thePlayer.posY - 1.0); i > 0; --i) {
			BlockPos pos = new BlockPos(mc.thePlayer.posX, (double) i, mc.thePlayer.posZ);
			if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)
				continue;
			return true;
		}
		return false;
	}
}
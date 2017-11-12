package nivia.modules.ghost;

import net.minecraft.block.BlockAir;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import nivia.Pandora;
import nivia.commands.Command;
import nivia.events.EventTarget;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Logger;
import nivia.utils.utils.InventoryUtils;
import nivia.utils.utils.Timer;

public class Refill extends Module {

	public DoubleProperty delay = new DoubleProperty(this, "Delay", 100, 0, 1000);
	private Timer timer = new Timer();

	public Refill() {
		super("AntiVoid", 0, 0, Category.EXPLOITS, "Refills your potions.", new String[] { "rfill", "rf", "fill", "refil" },
				false);
	}
	public Property<Boolean> lag = new Property<Boolean>(this, "Lagback", true);
	public Property<Boolean> CheckBlocksUnder = new Property<Boolean>(this, "CheckBlocksUnder", true);
	
	public DoubleProperty fallDistance = new DoubleProperty(this, "FallDistance", 5, 1, 13);
	public DoubleProperty wtime = new DoubleProperty(this, "WaitTime", 105, 15, 5900);
//	public static Timer timer = new Timer();
//	public static boolean saveMe;
	
	@EventTarget
	  public void onReceivePacket(EventPreMotionUpdates e) {	   
		if (this.timer.hasTicksElapsed((int) wtime.getValue()) && mc.thePlayer.fallDistance > fallDistance.getValue()
				&& !mc.thePlayer.isJumping && !Pandora.getModManager().getModState("Glide") && !Pandora.getModManager().getModState("Fly")) {
			if (CheckBlocksUnder.value ? !this.isBlockUnder() : true && lag.value) {
				mc.thePlayer.motionY = 0.0;
				Logger.logChat("lagging back.....");
				this.timer.reset();
				//this.saveMe = false;
			}
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
	
	/*
	@EventTarget
	public void onPre(EventPreMotionUpdates pre) {
		for (int i = 0; i < 9; ++i) {
			if (!InventoryUtils.hotbarIsFull() && this.timer.hasTimeElapsed((long) this.delay.getValue(), false)
					&& InventoryUtils.inventoryHasPotion(Potion.heal, true)) {
				InventoryUtils.shiftClickPotion(Potion.heal, true);
				timer.reset();
				if (!InventoryUtils.inventoryHasPotion(Potion.heal, true))
					this.Toggle();
			}

			if (InventoryUtils.hotbarIsFull())
				this.Toggle();

			if (!InventoryUtils.inventoryHasPotion(Potion.heal, true))
				this.Toggle();
		}
	}

	protected void addCommand() {
		Pandora.getCommandManager().cmds.add(new Command("Refill", "Manages Refill stuff.",
				Logger.LogExecutionFail("Option, Options:", new String[] { "Delay", "Values" }), "rfill", "rf", "fill",
				"refil") {
			@Override
			public void execute(String commandName, String[] arguments) {
				String message = arguments[1];
				switch (message.toLowerCase()) {
				case "delay":
				case "d":
				case "D":
				case "Delay":
					String message2 = arguments[2];
					switch (message2) {
					case "actual":
					case "value":
						logValue(delay);
						break;
					case "reset":
						delay.reset();
						Logger.logSetMessage("Refill", "Delay", delay);
						break;
					default:
						Long nD = Long.parseLong(message2);
						delay.setValue(nD);
						Logger.logSetMessage("Refill", "Delay", delay);
						break;
					}
					break;
				case "values":
				case "actual":
					logValues();
					break;
				default:
					Logger.logChat(getError());
					break;

				}
			}
		});
	}*/
}
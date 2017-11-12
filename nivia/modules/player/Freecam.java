package nivia.modules.player;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;

import static net.minecraft.init.Items.bed;
import static net.minecraft.init.Items.comparator;
import static net.minecraft.init.Items.compass;
import static net.minecraft.init.Items.paper;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import nivia.events.EventTarget;
import nivia.events.events.EventBoundingBox;
import nivia.events.events.EventPacketSend;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.Logger;

public class Freecam extends Module {
    public Property<Boolean> death = new Property<>(this, "AutoDisable", false);
	
	public Freecam() {
		super("Freecam", Keyboard.KEY_Y, 0xFFFF19, Category.PLAYER, "Temp Freecam while the new one is done.",
				new String[] { "fcam" }, true);
	}

	double x, y, z;

	public void onDisable() {
		super.onDisable();
		mc.thePlayer.setPosition(x, y, z);
		Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Helper.player().posX, Helper.player().posY + 0.01,
				Helper.player().posZ, Helper.player().onGround));
		mc.thePlayer.capabilities.isFlying = false;
		mc.thePlayer.noClip = false;
		mc.theWorld.removeEntityFromWorld(-1);
	}

	public void onEnable() {
		super.onEnable();
		x = mc.thePlayer.posX;
		y = mc.thePlayer.posY;
		z = mc.thePlayer.posZ;
		EntityOtherPlayerMP ent = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
		ent.inventory = mc.thePlayer.inventory;
		ent.inventoryContainer = mc.thePlayer.inventoryContainer;
		ent.setHealth(mc.thePlayer.getHealth());
		ent.setPositionAndRotation(this.x, mc.thePlayer.boundingBox.minY, this.z, mc.thePlayer.rotationYaw,
				mc.thePlayer.rotationPitch);
		ent.rotationYawHead = mc.thePlayer.rotationYawHead;
		mc.theWorld.addEntityToWorld(-1, ent);
	}

	@EventTarget
	public void onPreMotion(EventPreMotionUpdates e) {
        if(mc.thePlayer.isDead && death.value && this.getState()){
            this.setState(false);
            Logger.logChat("Freecam 死亡自动关闭!");
        }else{
            if (Helper.mc().getCurrentServerData().serverIP.toLowerCase().contains("hypixel") && mc.thePlayer.inventory.hasItem(bed) && mc.thePlayer.inventory.hasItem(comparator) && mc.thePlayer.inventory.hasItem(compass) && mc.thePlayer.inventory.hasItem(paper)) {
                this.setState(false);
                Logger.logChat("Freecam 死亡自动关闭!");
            }
        }
		mc.thePlayer.capabilities.isFlying = true;
		mc.thePlayer.noClip = true;
		mc.thePlayer.capabilities.setFlySpeed(0.1F);
		e.setCancelled(true);
	}

	@EventTarget
	public void onPacketSend(EventPacketSend e) {
		if (e.getPacket() instanceof C03PacketPlayer && new Random().nextBoolean())
			e.setCancelled(true);
	}

	@EventTarget
	public void onBB(EventBoundingBox e) {
		e.setBoundingBox(null);
	}
}

package nivia.modules.miscellanous;

import nivia.Pandora;
import nivia.commands.Command;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.Event2D;
import nivia.events.events.Event3D;
import nivia.events.events.EventPacketSend;
import nivia.events.events.EventPreMotionUpdates;
import nivia.events.events.EventTick;
import nivia.managers.FriendManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.Vec3;
import nivia.managers.FriendManager.Friend;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.modules.render.ESP.ESPMode;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.TimeHelper;
import nivia.utils.Wrapper;
import nivia.utils.utils.RenderUtils;
import nivia.utils.utils.RenderUtils.Camera;
import nivia.utils.utils.RenderUtils.Stencil;
import shadersmod.client.Shaders;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Random;

import org.lwjgl.opengl.GL11;

public class AntiAim extends Module {
	Property<mode> Mode = new Property<>(this, "Mode", mode.Headshot);
	TimeHelper spinTimer = new TimeHelper();
	TimeHelper emotionTimer = new TimeHelper();
	Random random = new Random();
	boolean emotion;
	public static EntityLivingBase en = null;
	public static boolean firstShot = true;
	float yaw;
	float pitch;
	public AntiAim() {
		super("CSGame", 0, 0x005C00, Category.MISCELLANEOUS, "look like a retard", new String[] { "aa", "derp", "retard" }, true);
	}
	
	public static boolean isNotItem(Object o) {
		if (!(o instanceof EntityLivingBase)) {
			return false;
		}
		return true;
	}
	
	public static ArrayList<EntityLivingBase> getClosestEntitiesToEntity(float range, Entity ent) {
		ArrayList<EntityLivingBase> entities = new ArrayList<EntityLivingBase>();
		for (Object o : Minecraft.getMinecraft().theWorld.loadedEntityList) {
			if (isNotItem(o) && !ent.isEntityEqual((EntityLivingBase) o)) {
				EntityLivingBase en = (EntityLivingBase) o;
				if (ent.getDistanceToEntity(en) < range) {
					entities.add(en);
				}
			}
		}
		return entities;
	}
	
	
	public boolean isValidTarget(Entity e){
		if(!(e instanceof EntityAnimal) && !(e instanceof EntityMob) && !(e instanceof EntityPlayer) && !(e instanceof EntityItem)) return false;
		if (((e instanceof EntityAnimal))) return false;
		if ((e instanceof EntityPlayerSP) && ((e instanceof EntityPlayer))) return true;
		if (((e instanceof EntityMob))) return false;
		if (((e instanceof EntityItem))) return false;
		if(e.isInvisible()) return false;
		if(isOnSameTeam(e)) return false;
		return true;
	}

	public boolean validEntity(EntityLivingBase en) {
		if (en.isEntityEqual(Minecraft.getMinecraft().thePlayer)) {
			return false;
		}
		if (en.isDead) {
			return false;
		}
		if (en.getHealth() <= 0) {
			return false;
		}
		if (!(en instanceof EntityLivingBase)) {
			return false;
		}
		if (en instanceof EntityPlayer) {
			if(FriendManager.isFriend(en.getName()) || isOnSameTeam(en)) {
				return false;
			}
		}
		if (en.isInvisible()) {
		//	if (!Jigsaw.getModuleByName("Invisible").isToggled()) {
				return false;
		//	}
		}
		if (en instanceof EntityPlayer) {
			if (en.height < 0.21f) {
				return false;
			}
		}

		if (!(en instanceof EntityPlayer)) {
	//		if (!Jigsaw.getModuleByName("NonPlayers").isToggled()) {
				return false;
	//		}
		}
		if (!(en instanceof EntityPlayer) && en instanceof EntityLiving ) {
			EntityLiving living = (EntityLiving)en;
			boolean armor = false;
			if(!armor && living.getCurrentArmor(0) != null && living.getCurrentArmor(0).getItem() != null) {
				armor = true;
			}
			if(!armor && living.getCurrentArmor(1) != null && living.getCurrentArmor(1).getItem() != null) {
				armor = true;
			}
			if(!armor && living.getCurrentArmor(2) != null && living.getCurrentArmor(2).getItem() != null) {
				armor = true;
			}
			if(!armor && living.getCurrentArmor(3) != null && living.getCurrentArmor(3).getItem() != null) {
				armor = true;
			}
			if(armor == false) {
				return false;
			}
		}
		if ((en instanceof EntityPlayer) ) {
			EntityPlayer living = (EntityPlayer)en;
			boolean armor = false;
			if(!armor && living.inventory.armorInventory[0] != null && living.inventory.armorInventory[0].getItem() != null) {
				armor = true;
			}
			if(!armor && living.inventory.armorInventory[1] != null && living.inventory.armorInventory[1].getItem() != null) {
				armor = true;
			}
			if(!armor && living.inventory.armorInventory[2] != null && living.inventory.armorInventory[2].getItem() != null) {
				armor = true;
			}
			if(!armor && living.inventory.armorInventory[3] != null && living.inventory.armorInventory[3].getItem() != null) {
				armor = true;
			}
			if(armor == false) {
				return false;
			}
		}
		if ((en instanceof EntityPlayer)) {

//			if(Jigsaw.getBypassManager().getEnabledBypass() != null && Jigsaw.getBypassManager().getEnabledBypass().getName().equals("AntiWatchdog") || Jigsaw.getModuleByName("AntiBot(Watchdog)").isToggled()) {
				if(en.ticksExisted < 10) {
					return false;
				}
//			}
		}

		// if(en.hurtTime > 12 &&
		// !Jigsaw.getModuleByName("HurtResistant").isToggled()) {
		// return false;
		// }
		return true;
	}
	
    private boolean isOnSameTeam(Entity entity) {
        boolean team = false;

        if( entity instanceof EntityPlayer) {
            String n = entity.getDisplayName().getFormattedText();
            if(n.startsWith('\u00a7' + "f") && !n.equalsIgnoreCase(entity.getName()))
                team = (n.substring(0, 6).equalsIgnoreCase(mc.thePlayer.getDisplayName().getFormattedText().substring(0, 6)));
            else team = (n.substring(0,2).equalsIgnoreCase(mc.thePlayer.getDisplayName().getFormattedText().substring(0,2)));
        }

        return team;
    }
	
	public ArrayList<EntityLivingBase> getClosestEntities(float range) {
		ArrayList<EntityLivingBase> entities = new ArrayList<EntityLivingBase>();
		for (Object o : Minecraft.getMinecraft().theWorld.loadedEntityList) {
			if (isNotItem(o) && !(o instanceof EntityPlayerSP)) {
				EntityLivingBase en = (EntityLivingBase) o;
				if (!validEntity(en)) {
					continue;
				}
				if (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(en) < range) {
					entities.add(en);
				}
			}
		}
		return entities;
	}
	
    @EventTarget
    public void onPre(EventPreMotionUpdates event) {
        if(en!=null && en.getHealth()>0.0){
        	event.setYaw(getRot(en)[0]);
        	event.setPitch(getRot(en)[1]);        	
        	final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(mc.thePlayer.inventory.currentItem).getStack();
            Helper.sendPacket(new C08PacketPlayerBlockPlacement(stack));
        }
    }
	
	@EventTarget
	private void onPre(EventTick event) {
		ArrayList<EntityLivingBase> ens = getClosestEntities(100);
		double minDistance = 999;
		for (EntityLivingBase en : ens) {
			if (mc.theWorld.rayTraceBlocks(
					new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
					new Vec3(en.posX, en.posY + en.getEyeHeight(), en.posZ), false, true, false) != null) {
				continue;
			}
			if (mc.thePlayer.getDistanceToEntity(en) < minDistance) {
				minDistance = mc.thePlayer.getDistanceToEntity(en);
				this.en = en;
			}
		}
		if (en == null) {
			return;
		}
		if (mc.theWorld.rayTraceBlocks(
				new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
				new Vec3(en.posX, en.posY + en.getEyeHeight(), en.posZ), false, true, false) != null) {
			en = null;
		}
	}
	
	@EventTarget(Priority.HIGH)
	public void onRender3D(Event3D e){
		for(Object entity : mc.theWorld.loadedEntityList){
			
			Entity ent = (Entity) entity;
			if (ent.isEntityEqual(Minecraft.getMinecraft().thePlayer)) {
				return;
			}
			final double posX = ent.lastTickPosX
					+ (ent.posX - ent.lastTickPosX)
					* e.getPartialTicks() - RenderManager.renderPosX;
			final double posY = ent.lastTickPosY
					+ (ent.posY - ent.lastTickPosY)
					* e.getPartialTicks() - RenderManager.renderPosY;
			final double posZ = ent.lastTickPosZ
					+ (ent.posZ - ent.lastTickPosZ)
					* e.getPartialTicks() - RenderManager.renderPosZ;
			if(isValidTarget(ent)){
				GL11.glPushMatrix();
				GL11.glTranslated(posX, posY, posZ);
				GL11.glRotatef(-ent.rotationYaw, 0.0F, ent.height, 0.0F);
				GL11.glTranslated(-posX, -posY, -posZ);
				mc.entityRenderer.setupCameraTransform(e.getPartialTicks(), 0  );
				RenderUtils.R3DUtils.RenderLivingEntityBox((Entity) entity, e.getPartialTicks(), true);
				GL11.glPopMatrix();

			}
		}
	//	if(!mode.value.equals(ESPMode.OBOX)) return;
		RenderUtils.Stencil.getInstance().checkSetupFBO();
	//	if(mode.value == ESPMode.OBOX) drawCombinedBoxes(e.getPartialTicks(), false);
		mc.getFramebuffer().bindFramebuffer(true);
		mc.getFramebuffer().bindFramebufferTexture();
	}
	
	public void drawCombinedBoxes(final float partialTicks, boolean shaders) {
		final int entityDispList = GL11.glGenLists(1);
		Stencil.getInstance().startLayer();
		GL11.glPushMatrix();
		mc.entityRenderer.setupCameraTransform(partialTicks, 0);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		RenderHelper.enableStandardItemLighting();
		GL11.glEnable(GL11.GL_CULL_FACE);
		final Camera playerCam = new Camera(Minecraft.getMinecraft().thePlayer);
		final Frustrum frustrum = new Frustrum();
		frustrum.setPosition(playerCam.getPosX(), playerCam.getPosY(), playerCam.getPosZ());
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		Stencil.getInstance().setBuffer(true);
		GL11.glNewList(entityDispList, 4864);
		for (final Object obj : Minecraft.getMinecraft().theWorld.loadedEntityList) {
			final Entity entity = (Entity) obj;
			if (entity == Minecraft.getMinecraft().thePlayer || !isValidTarget(entity))
				continue;
			GL11.glLineWidth(3);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			final Camera entityCam = new Camera(entity);
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glTranslated(entityCam.getPosX() - playerCam.getPosX(), entityCam.getPosY() - playerCam.getPosY(), entityCam.getPosZ() - playerCam.getPosZ());
			final Render entityRender = mc.getRenderManager().getEntityRenderObject(entity);
			if (entityRender != null) {
				if (entity instanceof EntityLivingBase || entity instanceof EntityItem) {
					if (FriendManager.isFriend(entity.getName())) GL11.glColor4f(0, 1, 1, 1);
					else {
						if (entity instanceof EntityPlayer || entity instanceof EntityItem) Helper.colorUtils().glColor(Helper.colorUtils().getRainbow(1, 1));
						else if (entity instanceof EntityMob) Helper.colorUtils().glColor(0x99992EE8);
						else if (entity instanceof EntityAnimal) Helper.colorUtils().glColor(0x990EFFA2);
					}
					final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - RenderManager.renderPosX;
					final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - RenderManager.renderPosY;
					final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - RenderManager.renderPosZ;
					if (!shaders) GlStateManager.disableLighting();
					Helper.get3DUtils().drawBoundingBox(new AxisAlignedBB(
									entity.boundingBox.minX - entity.posX + (posX),
									entity.boundingBox.minY - (entity.isSneaking() ? entity.posY + 0.2 : entity.posY) + (posY),
									entity.boundingBox.minZ - entity.posZ + (posZ),
									entity.boundingBox.maxX - entity.posX + (posX),
									entity.boundingBox.maxY - (entity.isSneaking() ? entity.posY + 0.2 : entity.posY - 0.1) + (posY),
									entity.boundingBox.maxZ - entity.posZ + (posZ)));
					if (!shaders)
						GlStateManager.enableLighting();
				}
			}
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
		GL11.glColor3d(1, 1, 1);
		GL11.glEndList();
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glCallList(entityDispList);
		Stencil.getInstance().setBuffer(false);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glCallList(entityDispList);
		Stencil.getInstance().cropInside();
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glCallList(entityDispList);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		Minecraft.getMinecraft().entityRenderer.func_175072_h();
		RenderHelper.disableStandardItemLighting();
		Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
		Stencil.getInstance().stopLayer();
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glPopMatrix();
		Minecraft.getMinecraft().entityRenderer.func_175072_h();
		RenderHelper.disableStandardItemLighting();
		Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
		GL11.glDeleteLists(entityDispList, 1);
	}
	
	public static float[] getFacePos(Vec3 vec) {
		double diffX = vec.xCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
		double diffY = vec.yCoord + 0.5
				- (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
		double diffZ = vec.zCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		return new float[] {
				Minecraft.getMinecraft().thePlayer.rotationYaw
						+ MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw),
				Minecraft.getMinecraft().thePlayer.rotationPitch
						+ MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch) };
	}
	
	private float[] getRot(EntityLivingBase en) {
		double xAim = (en.posX - 0.5) + (en.posX - en.lastTickPosX) * 5.5;
		double yAim = en.posY + (en.getEyeHeight());
		double zAim = (en.posZ - 0.5) + (en.posZ - en.lastTickPosZ) * 5.5;
		if (firstShot || mode.Dynamic.equals("Headshot")) {
			return getFacePos(new Vec3(xAim, yAim - 0.35, zAim));
		}
		float[] rots = getFacePos(new Vec3(xAim, yAim - 0.35, zAim));
		Item heldItem = mc.thePlayer.getCurrentEquippedItem().getItem();
		if (heldItem != null) {
			if (heldItem instanceof ItemSpade) {
				rots[1] += 4.2;
			}
			if (heldItem instanceof ItemHoe) {
				rots[1] += 6.5;
			}
		}
		return rots;
	}

	public C06PacketPlayerPosLook createPosLookPacket(C03PacketPlayer.C06PacketPlayerPosLook posLookIn) {
		float[] rots = getRot(en);
		return new C03PacketPlayer.C06PacketPlayerPosLook(posLookIn.getPositionX(), posLookIn.getPositionY(),
				posLookIn.getPositionZ(), rots[0], rots[1], mc.thePlayer.onGround);
	}

	public C05PacketPlayerLook createPacket() {
		float[] rots = getRot(en);
		return new C03PacketPlayer.C05PacketPlayerLook(rots[0], rots[1], mc.thePlayer.onGround);
	}

	@EventTarget(Priority.HIGH)
	public void onPacketSent(EventPacketSend packet) {
		if (en != null) {
			if (packet.getPacket() instanceof C08PacketPlayerBlockPlacement) {
				firstShot = false;
			}
			if (packet.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
			//	packet.cancel();
				packet.setPacket(this.createPacket());
			}
		}	
	}
	
	
	public static void facePos(Vec3 vec) {
		double diffX = vec.xCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
		double diffY = vec.yCoord + 0.5
				- (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
		double diffZ = vec.zCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		Minecraft.getMinecraft().thePlayer.rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw
				+ MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw);
		Minecraft.getMinecraft().thePlayer.rotationPitch = Minecraft.getMinecraft().thePlayer.rotationPitch
				+ MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch);
	}
	
	public enum mode {
		Dynamic, Headshot;
	}
	
	

}

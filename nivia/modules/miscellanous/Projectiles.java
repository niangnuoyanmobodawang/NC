package nivia.modules.miscellanous;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import nivia.Pandora;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.Event3D;
import nivia.events.events.EventTick;
import nivia.modules.Module;
import nivia.modules.exploits.FastUse;
import nivia.utils.Helper;
import nivia.utils.TimeHelper;
import nivia.utils.utils.Timer;

import java.util.ArrayList;
import java.util.List;

public class Projectiles extends Module {
	private Timer timer = new Timer();
	public Projectiles() {
		super("Projectiles", 0, 0, Category.MISCELLANEOUS, "Displays the landing position of throwables.",
				new String[] {}, true);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		positions.clear();
		timer.reset();
	}
	

	
	@EventTarget
	public void call(Event3D event) {
		double renderPosX = mc.thePlayer.lastTickPosX
				+ (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.getPartialTicks();
		double renderPosY = mc.thePlayer.lastTickPosY
				+ (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.getPartialTicks();
		double renderPosZ = mc.thePlayer.lastTickPosZ
				+ (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.getPartialTicks();
		Helper.mc().entityRenderer.setupCameraTransform(Helper.mc().timer.renderPartialTicks, 2);
		ItemStack stack;
		Item item;

		if (mc.thePlayer.getHeldItem() != null && mc.gameSettings.thirdPersonView == 0) {
			if (!isValidPotion(mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getItem())
					&& mc.thePlayer.getHeldItem().getItem() != Items.experience_bottle
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod)
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSnowball)
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl)
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEgg))
				return;
			stack = mc.thePlayer.getHeldItem();
			item = mc.thePlayer.getHeldItem().getItem();
		} else
			return;
		FastUse fuse = (FastUse) Pandora.getModManager().getModule(FastUse.class);
		boolean is2Fast = fuse.delay.getValue() <= 3;
		if ((mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) && (!mc.thePlayer.isUsingItem() && !is2Fast))
			return;
		double posX = renderPosX - MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		double posY = renderPosY + mc.thePlayer.getEyeHeight() - 0.1000000014901161D;
		double posZ = renderPosZ - MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;

		double motionX = -MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
				* (item instanceof ItemBow ? 1.0D : 0.4D);
		double motionY = -MathHelper.sin(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
				* (item instanceof ItemBow ? 1.0D : 0.4D);
		double motionZ = MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
				* (item instanceof ItemBow ? 1.0D : 0.4D);

		int var6 = 72000 - mc.thePlayer.getItemInUseCount();
		float power = (float) (var6 / ((fuse.bows.value && fuse.delay.getValue() < 22)
				? (is2Fast ? 0F : (fuse.delay.getValue() - 1)) : 20.0F));
		power = (power * power + power * 2.0F) / 3.0F;

		if (power < 0.1D)
			return;

		if (power > 1.0F)
			power = 1.0F;

		float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);

		motionX /= distance;
		motionY /= distance;
		motionZ /= distance;

		float pow = (item instanceof ItemBow ? power * 2.0F
				: isValidPotion(stack, item) ? 0.325F
						: item instanceof ItemFishingRod ? 1.25F
								: mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.9F : 1.0F);

		motionX *= pow * (item instanceof ItemFishingRod ? 0.75F
				: mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F);
		motionY *= pow * (item instanceof ItemFishingRod ? 0.75F
				: mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F);
		motionZ *= pow * (item instanceof ItemFishingRod ? 0.75F
				: mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F);

		Helper.get3DUtils().startDrawing();
		if (power > 0.6F) {
			GlStateManager.color(0.0F, 1.0F, 0.0F, 0.15F);
		} else if (power > 0.3F) {
			GlStateManager.color(0.8F, 0.5F, 0.0F, 0.15F);
		} else {
			GlStateManager.color(1.0F, 0.0F, 0.0F, 0.15F);
		}
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderer.startDrawing(3);
		if (!(item instanceof ItemBow))
			GL11.glColor4d(0, 0.9, 1, 0.75);
		renderer.addVertex(posX - renderPosX, posY + 0.01 - renderPosY, posZ - renderPosZ);

		float size = (float) (item instanceof ItemBow ? 0.3D : 0.25D);
		boolean hasLanded = false;
		Entity landingOnEntity = null;
		MovingObjectPosition landingPosition = null;
		while (!hasLanded && posY > 0.0D) {
			Vec3 present = new Vec3(posX, posY, posZ);
			Vec3 future = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);

			MovingObjectPosition possibleLandingStrip = mc.theWorld.rayTraceBlocks(present, future, false, true, false);
			if (possibleLandingStrip != null
					&& possibleLandingStrip.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
				landingPosition = possibleLandingStrip;
				hasLanded = true;
			}

			AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size,
					posZ + size);
			List entities = getEntitiesWithinAABB(
					arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			for (Object entity : entities) {
				Entity boundingBox = (Entity) entity;
				if ((boundingBox.canBeCollidedWith()) && (boundingBox != mc.thePlayer)) {
					float var11 = 0.3F;
					AxisAlignedBB var12 = boundingBox.getEntityBoundingBox().expand(var11, var11, var11);
					MovingObjectPosition possibleEntityLanding = var12.calculateIntercept(present, future);
					if (possibleEntityLanding != null) {
						hasLanded = true;
						landingOnEntity = boundingBox;
						landingPosition = possibleEntityLanding;
					}
				}
			}

			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			float motionAdjustment = 0.99F;
			motionX *= motionAdjustment;
			motionY *= motionAdjustment;
			motionZ *= motionAdjustment;
			motionY -= (item instanceof ItemBow ? 0.05D : 0.03D);
			renderer.addVertex(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
		}
		tessellator.draw();

		if (landingPosition != null && landingPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			GlStateManager.translate(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);

			int side = landingPosition.field_178784_b.getIndex();

			if (side == 2) {
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			} else if (side == 3) {
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			} else if (side == 4) {
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			} else if (side == 5) {
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			}

			Cylinder c = new Cylinder();

			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			c.setDrawStyle(GLU.GLU_LINE);

			if (landingOnEntity != null) {
				GL11.glColor4d(0.9, 1, 0, 0.15);
			}
			if (!(item instanceof ItemBow))
				GL11.glColor4d(0, 0.9, 1, 1);
			c.draw(0.5F, 0.5F, 0.0F, 4, 1);
			if (!(item instanceof ItemBow)) {
				GL11.glColor4d(0, 0.9, 1, 0.15);
				c.draw(0.0f, 0.5F, 0.0F, 4, 100);
			} else {
				c.draw(0.0f, 0.5F, 0.0F, 4, 27);
			}
		}
		Helper.get3DUtils().stopDrawing();
	}

	private boolean isValidPotion(ItemStack stack, Item item) {
		if (item != null && item instanceof ItemPotion) {
			ItemPotion potion = (ItemPotion) item;
			if (!ItemPotion.isSplash(stack.getItemDamage()))
				return false;

			if (potion.getEffects(stack) != null) {
				return true;
			}
		}
		return false;
	}

	   private boolean isOnSameTeam(Entity entity) {
	        boolean team = false;

	        if(entity instanceof EntityPlayer) {
	            String n = entity.getDisplayName().getFormattedText();
	            if(n.startsWith('\u00a7' + "f") && !n.equalsIgnoreCase(entity.getName()))
	                team = (n.substring(0, 6).equalsIgnoreCase(mc.thePlayer.getDisplayName().getFormattedText().substring(0, 6)));
	            else team = (n.substring(0,2).equalsIgnoreCase(mc.thePlayer.getDisplayName().getFormattedText().substring(0,2)));
	        }

	        return team;
	    }
	   private final List<double[]> positions = new ArrayList<double[]>();
		@EventTarget(Priority.LOWEST)
		public void onRender3D(Event3D render) {
		for (Object e2 : mc.theWorld.loadedEntityList) {
			Entity e = (Entity)e2;
			if (!(e instanceof EntityArrow)) {
				continue;
			}
			EntityArrow arrow = (EntityArrow) e;

			if (arrow.shootingEntity != null && arrow.shootingEntity.isEntityEqual(mc.thePlayer)) {
	//			continue;
			}
			if (arrow.shootingEntity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
				if (isOnSameTeam(player)) {
			//		continue;
				}
				
			}
			// MovingObjectPosition rayTrace = arrow.rayTrace(100,
			// mc.timer.elapsedPartialTicks);

			// if(rayTrace == null) {
			// continue;
			// }
			// Atlas.chatMessage(rayTrace.toString());
			// if(rayTrace.getBlockPos() != null) {
			//
			// }
			if (arrow.inGround) {
				continue;
			}
			
			positions.add(new double[] { arrow.getPosition().getX(), arrow.getPosition().getY(), arrow.getPosition().getZ() });

			final double viewerPosX = Helper.mc().getRenderManager().viewerPosX;
	        final double viewerPosY = Helper.mc().getRenderManager().viewerPosY;
	        final double viewerPosZ = Helper.mc().getRenderManager().viewerPosZ;
	        final double line_lenght = 0.25;
	        final ArrayList<BlockPos> dontRender = new ArrayList<BlockPos>();
	            final BlockPos pos = arrow.getPosition();
	            if (dontRender.contains(pos)) {
	                continue;
	            }
	            final double x = -(viewerPosX - pos.getX()) + 0.5;
	            final double y = -(viewerPosY - pos.getY()) + 0.5;
	            final double z = -(viewerPosZ - pos.getZ()) + 0.5;
	            GL11.glPushMatrix();
	            GL11.glBlendFunc(770, 771);
	            GL11.glEnable(3042);
	            GL11.glLineWidth(1.0f);
	            GL11.glDisable(3553);
	            GL11.glDisable(2929);
	            GL11.glDepthMask(true);
	            GL11.glColor3f(255.0f, 255.0f, 1.0f);
	            GL11.glBegin(1);
	        //    for (double[] position : positions) {
	         
	       
	            GL11.glVertex3d(x - line_lenght, y, z);
	            GL11.glVertex3d(x + line_lenght, y, z);
	            GL11.glVertex3d(x, y + line_lenght, z);
	            GL11.glVertex3d(x, y - line_lenght, z);
	            GL11.glVertex3d(x, y, z + line_lenght);
	            GL11.glVertex3d(x, y, z - line_lenght);
	     //       }
	            GL11.glEnd();
	            GL11.glEnable(3553);
	            GL11.glEnable(2929);
	            GL11.glDepthMask(true);
	            GL11.glDisable(3042);
	            GL11.glPopMatrix();
	        
			
		}
		
	}
	
	private List getEntitiesWithinAABB(AxisAlignedBB bb) {
		ArrayList list = new ArrayList();
		int chunkMinX = MathHelper.floor_double((bb.minX - 2.0D) / 16.0D);
		int chunkMaxX = MathHelper.floor_double((bb.maxX + 2.0D) / 16.0D);
		int chunkMinZ = MathHelper.floor_double((bb.minZ - 2.0D) / 16.0D);
		int chunkMaxZ = MathHelper.floor_double((bb.maxZ + 2.0D) / 16.0D);
		for (int x = chunkMinX; x <= chunkMaxX; x++) {
			for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
				if (mc.theWorld.getChunkProvider().chunkExists(x, z)) {
					mc.theWorld.getChunkFromChunkCoords(x, z).func_177414_a(mc.thePlayer, bb, list, null);
				}
			}
		}
		return list;
	}
}

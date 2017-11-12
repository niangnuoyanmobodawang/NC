package nivia.modules.combat;


import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.pathfinder.WalkNodeProcessor;
import nivia.Pandora;
import nivia.events.EventTarget;
import nivia.events.events.EventPacketReceive;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.FriendManager;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.utils.NewValues;

public class FightGod extends nivia.modules.Module{

	public FightGod() {
		super("FightBot", 0, 0, Category.COMBAT, "", new String[]{"fightbot", "fb"});
	}
	public NewValues attackrange = new NewValues("fightbot_Attack Range", 4F, 1F, 6F, 0.1F);
	public NewValues aps = new NewValues("fightbot_APS", 13F, 1F, 20F, 0.1F);
	public NewValues mastername = new NewValues("fightbot_Master", "Hypno", 16);
	public NewValues alias = new NewValues("fightbot_Alias", "Hypno", 16);
	public NewValues block = new NewValues("fightbot_Block", true);
	public NewValues invis = new NewValues("fightbot_Invisible", true);
	public NewValues attack = new NewValues("fightbot_Follow only", false);
	
	public int ticks = 0;
	public static EntityPlayer master;
	public static EntityPlayer niggertodie;
	
	public PathFinder pathFinder = new PathFinder(new WalkNodeProcessor());
	
	@EventTarget
	public void onPre(EventPreMotionUpdates event){
		if(Helper.mc().currentScreen == null){
			if(alias.editvalue.equalsIgnoreCase("")){
				if(!mastername.editvalue.equalsIgnoreCase("")){
					alias.editvalue = mastername.editvalue;
				}
			}
		}
		if(Helper.player().isDead){
			Helper.player().respawnPlayer();
		}
		master = getPlayerByName(mastername.editvalue);
//		if(master == null){
//			Helper.player().sendChatMessage("Please specify a master.");
//			setToggled(false);
//		}
		if(niggertodie == null || !isValid(niggertodie)){
			chooseTarget();
		}
		if(master != null && !(niggertodie != null && isValid(niggertodie) && !attack.boolvalue)){
			PathEntity pe = this.pathFinder.func_176188_a(Helper.mc().theWorld, Helper.player(), master, 50.0F);
			if ((pe != null) && 
	          (pe.getCurrentPathLength() > 1) && Helper.player().getDistanceToEntity(master) > 1.5)
	        {
	          PathPoint point = pe.getPathPointFromIndex(1);
	          float[] meme = getRotationTo(new Vec3(point.xCoord + 0.5D, point.yCoord + 0.5D, point.zCoord + 0.5D));
	          if(Pandora.getModManager().getModState("Speed")){
	        	  float yaw;
	        	  Helper.player().rotationYaw =  meme[0];
		          Helper.player().motionX = (Helper.player().motionZ = 0.0D);
		          double newx = Math.sin(Helper.player().rotationYaw * 3.1415927F / 180.0F) * 0.28F;
		          double newz = Math.cos(Helper.player().rotationYaw * 3.1415927F / 180.0F) * 0.28F;
		          Helper.player().movementInput.moveForward = 1.0F;
		            if ((Helper.player().isCollidedHorizontally) && (Helper.player().onGround)) {
		              Helper.player().jump();
		            }
		            if (((Helper.player().isInWater()) || (Helper.player().isInsideOfMaterial(Material.lava))) && (!Helper.player().movementInput.sneak) && (!Helper.player().movementInput.jump) ) {
		              Helper.player().motionY += 0.039D;
		            }
	          }else{
	        	  for(int i = 0; i < 15; i++){
	      			float[] nigs = getRots(master, Float.MAX_VALUE, Float.MAX_VALUE);
	      	          Helper.player().rotationYaw = nigs[0];
	      	          Helper.player().rotationPitch = nigs[1];
	      	        float yaw;
	  	          yaw =  meme[0];
	  	          Helper.player().motionX = (Helper.player().motionZ = 0.0D);
	  	          double newx = Math.sin(yaw * 3.1415927F / 180.0F) * (Helper.player().getAIMoveSpeed() * 3);
	  	          double newz = Math.cos(yaw * 3.1415927F / 180.0F) * (Helper.player().getAIMoveSpeed() * 3);
	  	          Helper.player().motionX -= newx;
	  	          Helper.player().motionZ += newz;
	  	            if ((Helper.player().isCollidedHorizontally) && (Helper.player().onGround)) {
	  	              Helper.player().jump();
	  	            }
	  	            if (((Helper.player().isInWater()) || (Helper.player().isInsideOfMaterial(Material.lava))) && (!Helper.player().movementInput.sneak) && (!Helper.player().movementInput.jump)) {
	  	              Helper.player().motionY += 0.039D;
	  	            }
	      			}
	          }
	         
	}else{
	}
		}
		if(niggertodie != null && isValid(niggertodie) && !attack.boolvalue){
			for(int i = 0; i < 15; i++){
			float[] nigs = getRots(niggertodie, Float.MAX_VALUE, Float.MAX_VALUE);
	          Helper.player().rotationYaw = nigs[0];
	          Helper.player().rotationPitch = nigs[1];
			}
			PathEntity pe = this.pathFinder.func_176188_a(Helper.mc().theWorld, Helper.player(), niggertodie, 50.0F);
			if ((pe != null) && 
	          (pe.getCurrentPathLength() > 1) && Helper.player().getDistanceToEntity(niggertodie) > 1.5)
	        {
	          PathPoint point = pe.getPathPointFromIndex(1);
	          float[] meme = getRotationTo(new Vec3(point.xCoord + 0.5D, point.yCoord + 0.5D, point.zCoord + 0.5D));
	          if(Pandora.getModManager().getModState("Speed")){
	        	  float yaw;
	        	  Helper.player().rotationYaw =  meme[0];
		          Helper.player().motionX = (Helper.player().motionZ = 0.0D);
		          double newx = Math.sin(Helper.player().rotationYaw * 3.1415927F / 180.0F) * 0.28F;
		          double newz = Math.cos(Helper.player().rotationYaw * 3.1415927F / 180.0F) * 0.28F;
		          Helper.player().movementInput.moveForward = 1.0F;
		            if ((Helper.player().isCollidedHorizontally) && (Helper.player().onGround)) {
		              Helper.player().jump();
		            }
		            if (((Helper.player().isInWater()) || (Helper.player().isInsideOfMaterial(Material.lava))) && (!Helper.player().movementInput.sneak) && (!Helper.player().movementInput.jump)) {
		              Helper.player().motionY += 0.039D;
		            }
	          }else{
	        	  for(int i = 0; i < 15; i++){
	      			float[] nigs = getRots(niggertodie, Float.MAX_VALUE, Float.MAX_VALUE);
	      	          Helper.player().rotationYaw = nigs[0];
	      	          Helper.player().rotationPitch = nigs[1];
	      	        float yaw;
	  	          yaw =  meme[0];
	  	          Helper.player().motionX = (Helper.player().motionZ = 0.0D);
	  	          double newx = Math.sin(yaw * 3.1415927F / 180.0F) * 0.28F;
	  	          double newz = Math.cos(yaw * 3.1415927F / 180.0F) * 0.28F;
	  	          Helper.player().motionX -= newx;
	  	          Helper.player().motionZ += newz;
	  	            if ((Helper.player().isCollidedHorizontally) && (Helper.player().onGround)) {
	  	              Helper.player().jump();
	  	            }
	  	            if (((Helper.player().isInWater()) || (Helper.player().isInsideOfMaterial(Material.lava))) && (!Helper.player().movementInput.sneak) && (!Helper.player().movementInput.jump)) {
	  	              Helper.player().motionY += 0.039D;
	  	            }
	      			}
	          }
	}else{
	}
			if(Helper.player().getDistanceToEntity(niggertodie) < attackrange.value){
				if(block.boolvalue){
					if(Helper.player().getHeldItem() != null){
						if(Helper.player().getHeldItem().getItem() instanceof ItemSword){
							Helper.mc().playerController.sendUseItem(Helper.player(), Helper.world(), Helper.player().getHeldItem());
						}
					}
				}
				if(!Pandora.getModManager().getModState("KillAura")){
				ticks++;
				if(ticks >= 20 / aps.value){
				Helper.player().swingItem();
				if (KillAura.getAura().autoblock.value && Helper.player().getHeldItem() != null && Helper.player().getHeldItem().getItem() instanceof ItemSword) {
					Helper.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
				}
			//	if(!KillAura.getAura().is){
				Helper.sendPacket(new C02PacketUseEntity(niggertodie, C02PacketUseEntity.Action.ATTACK));
		//		}
				if(block.boolvalue){
					if(Helper.player().getHeldItem() != null){
						if(Helper.player().getHeldItem().getItem() instanceof ItemSword){
							Helper.mc().playerController.sendUseItem(Helper.player(), Helper.world(), Helper.player().getHeldItem());
						}
					}
				}
				ticks = 0;
				}
			}
			}
		}
	}
	public boolean shouldMovetoOthers(){
		return !attack.boolvalue && niggertodie != null && isValid(niggertodie);
	}
	@EventTarget
	public void onPacket(EventPacketReceive packet){
		if(packet.getPacket() instanceof S02PacketChat){
			S02PacketChat chat = (S02PacketChat)packet.getPacket();
			String chats = chat.func_148915_c().getUnformattedText();
			if(chats.contains(alias.editvalue)){
				if(chats.toLowerCase().contains("-bots say ")){
					String[] args = chats.toLowerCase().split("-bots say ");
					Helper.player().sendChatMessage(args[1]);
				}
				if(chats.toLowerCase().contains("-bots connect ")){
					String[] args = chats.toLowerCase().split("-bots connect ");
					String[] ips = args[1].split(":");
					String ip = "";
					int port = 25565;
					if(ips.length > 0){
						port = Integer.parseInt(ips[1]);
						ip = args[1];
					}else{
						ip = ips[0];
					}
					Helper.mc().displayGuiScreen(new GuiConnecting(null, Helper.mc(), ip, port));
				}
				if(chats.toLowerCase().contains("-bots newkit")){
					Helper.player().sendChatMessage("/newkit");
				}
				if(chats.toLowerCase().contains("-bots friend add ")){
					String[] args = chats.toLowerCase().split("-bots friend add ");
					Helper.player().sendChatMessage(".friend add " + args[1]);
					Helper.player().sendChatMessage("Friended " + args[1]);
				}
				if(chats.toLowerCase().contains("-bots friend del ")){
					String[] args = chats.toLowerCase().split("-bots friend del ");
					Helper.player().sendChatMessage(".friend del " + args[1]);
					Helper.player().sendChatMessage("Unfriended " + args[1]);
				}
				if(chats.toLowerCase().contains("-bots killself")){
					String[] args = chats.toLowerCase().split("-bots toggle ");
                    System.exit(0);
					Helper.player().sendChatMessage("bye");
				}
			}
		}
	}
	@EventTarget
	public void onPost(EventPostMotionUpdates event){
		if(Helper.player().isBlocking()){
			Helper.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
		}
	}
	public void onDisable(){
		Helper.player().movementInput.moveForward = 1.0F;
		niggertodie = null;
	}
	 public static float[] getRotationTo(Vec3 pos)
	    {
	      double xD = Helper.player().posX - pos.xCoord;
	      double yD = Helper.player().posY + Helper.player().getEyeHeight() - pos.yCoord;
	      double zD = Helper.player().posZ - pos.zCoord;
	      double yaw = Math.atan2(zD, xD);
	      double pitch = Math.atan2(yD, Math.sqrt(Math.pow(xD, 2.0D) + Math.pow(zD, 2.0D)));
	      
	      return new float[] { (float)Math.toDegrees(yaw) + 90.0F, (float)Math.toDegrees(pitch) };
	    }
	public float[] getRots(Entity p_70625_1_, float p_70625_2_, float p_70625_3_)
    {
    	double var4 = p_70625_1_.posX - Helper.player().posX;
        double var8 = p_70625_1_.posZ - Helper.player().posZ;
        double var6;

        if (p_70625_1_ instanceof EntityLivingBase)
        {
            EntityLivingBase var14 = (EntityLivingBase)p_70625_1_;
            var6 = var14.posY + (double)var14.getEyeHeight() - (Helper.player().posY + (double)Helper.player().getEyeHeight());
        }
        else
        {
            var6 = (p_70625_1_.getEntityBoundingBox().minY + p_70625_1_.getEntityBoundingBox().maxY) / 2.0D - (Helper.player().posY + (double)Helper.player().getEyeHeight());
        }

        double var141 = (double)MathHelper.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float)(Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
        float var13 = (float)(-(Math.atan2(var6, var141) * 180.0D / Math.PI));
        float pitch = this.updateRotation(Helper.player().rotationPitch, var13, p_70625_3_);
        float yaw  = this.updateRotation(Helper.player().rotationYaw, var12, p_70625_2_);
        return new float[]{yaw, pitch};
    }

    /**
     * Arguments: current rotation, intended rotation, max increment.
     */
    private float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_)
    {
        float var4 = MathHelper.wrapAngleTo180_float(p_70663_2_ - p_70663_1_);

        if (var4 > p_70663_3_)
        {
            var4 = p_70663_3_;
        }

        if (var4 < -p_70663_3_)
        {
            var4 = -p_70663_3_;
        }

        return p_70663_1_ + var4;
    }
	public boolean isValid(EntityPlayer entity){
		if(entity.getName().startsWith("Body #")){
			return false;
		}
		if(entity == master){
			return false;
		}
		if(entity.getDistanceToEntity(master) > 10){
			return false;
		}
		if(entity.getHealth() == 0){
			return false;
		}
		if(FriendManager.isFriend(entity.getName())){
			return false;
		}
		if(entity.isDead){
			return false;
		}
		if(entity.isPlayerSleeping()){
			return false;
		}
		if(entity instanceof EntityPlayerSP){
			return false;
		}
		if(entity.isInvisible() && !invis.boolvalue){
			return false;
		}
		return true;
	}
	public void chooseTarget(){
		EntityPlayer meme = null;
		for(Object o : Helper.world().loadedEntityList){
			if(o instanceof EntityPlayer){
				EntityPlayer p = (EntityPlayer)o;
				if(meme == null || (meme.getDistanceToEntity(Helper.player()) > p.getDistanceToEntity(Helper.player()))){
					if(isValid(p)){
					meme = p;
					}
				}
			}
		}
		niggertodie = meme;
	}
	public EntityPlayer getPlayerByName(String name){
		for(Object o : Helper.world().loadedEntityList){
			if(o instanceof EntityPlayer){
				EntityPlayer p = (EntityPlayer)o;
				if(p.getName().equalsIgnoreCase(name)){
					return p;
				}
			}
		}
		return null;
	}
	public void runCmd(String s) {
		try{
		String[] args = s.split(" ");
		if(args[0].equalsIgnoreCase("alias")){
			alias.stringvalue = args[1];
		}
		}catch(Exception e){
			Helper.player().sendChatMessage("Invalid args. .fb master <Name> | .fb alias <Alias>");
		}
	}
}


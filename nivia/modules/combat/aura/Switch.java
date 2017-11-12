package nivia.modules.combat.aura;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import nivia.Pandora;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventPacketReceive;
import nivia.events.events.EventPacketSend;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.events.events.EventTick;
import nivia.modules.Module;
import nivia.modules.combat.AutoPot;
import nivia.modules.combat.Criticals;
import nivia.modules.combat.Criticals.CritMode;
import nivia.modules.combat.KillAura;
import nivia.modules.combat.KillAura.AuraMode;
import nivia.modules.movement.NoSlow;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.Wrapper;
import nivia.utils.utils.RaycastUtils;
import nivia.utils.utils.Timer;

import java.util.Objects;
import java.util.Random;

public class Switch extends AuraMode {

    public Switch(KillAura killAura) {
        super("Switch", killAura);
    }
    public static boolean attack;
    public static float yaw;
	public static float pitch;
    public int ticks;
    public int delay;
    public boolean override;
    public int currentTarget;
    private static float oldYaw;
    private static float oldPitch;
    private static boolean lookChanged;
    private static int notSet;
    public static Timer potTimer = new Timer();
    public static Timer duraTimer = new Timer();
    public static Timer burstTimer = new Timer();
    public static Timer critTimer = new Timer();
	private static Module setBy;

    @EventTarget(Priority.HIGH)
    public void onPost(EventPostMotionUpdates e) {
    }
    
  
    
    @Override
    public void onPreMotion(EventPreMotionUpdates e) {
        if (currentTarget >= ka.attackList.size())
            currentTarget = 0;
        ticks++;
        if (ticks > 20) ticks = 20;
        if (!canAttack()) {
            attack = false;
            ka.nextTick = false;
            override = true;
            return;
        }
     //   e.se
        ka.nextTick = false;
        ka.sortTargets();
        attack = true;

        if (ka.lockview.value) {
            EntityPlayerSP player = mc.thePlayer;
            EntityLivingBase target = getTarget();
			final double diffX = target.posX - player.posX;
            final double diffY = target.posY + target.getEyeHeight() * 0.9 - (player.posY + player.getEyeHeight());
            final double diffZ = target.posZ - player.posZ;
            final double dist = (double)MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
            final float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
            final float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
            final float[] neededRotations = { (false ? mc.thePlayer.getRotationYawHead() : player.rotationYaw) + MathHelper.wrapAngleTo180_float(yaw - (false ? e.getYaw() : player.rotationYaw)), (false ? e.getPitch() : player.rotationPitch) + MathHelper.wrapAngleTo180_float(pitch - (false ? e.getPitch() : player.rotationPitch)) };
            final float[] rlyneed = (float[])neededRotations.clone();
            final float d0 = 0.0f - (false ? e.getYaw() : player.rotationYaw);
            final float d0y = neededRotations[0] + d0;
            final boolean rotateRight = d0y > 0.0f;
            if (rotateRight) {
                neededRotations[0] = (false ? e.getYaw() : player.rotationYaw) + Math.min(Math.abs(0.0f - d0y), 40.2f);
            }
            else {
                neededRotations[0] = (false ? e.getYaw() : player.rotationYaw) - Math.min(Math.abs(0.0f - d0y), 40.2f);
            }
            e.setYaw(neededRotations[0]);
            e.setPitch(neededRotations[1]);
       //     mc.thePlayer.rotationYaw = neededRotations[0];
       //     mc.thePlayer.rotationPitch = neededRotations[1];
        }else{
        e.setYaw(Helper.combatUtils().faceTarget(getTarget(), 1000, 1000, false)[0]);
        e.setPitch(Helper.combatUtils().faceTarget(getTarget(), 1000, 1000, false)[1]);
        }
        yaw = e.getYaw();
        pitch = e.getPitch();
        
   
       
        
    }
    

    
    
    
/*	public void onRecievePacket(EventPacketSend e){
        if (ka.lockview.value && canAttack()) {
		if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            packet.field_148936_d = (float) yaw;
            packet.field_148937_e = (float) pitch;
        }
       }
	}
    */
    /*
     * Auto clicker 
     * (non-Javadoc)
     * @see nivia.modules.combat.KillAura.AuraMode#onRunTick(nivia.events.events.EventTick)
     */
    
    @Override
    public void onRunTick(EventTick e) {
        if(attack) {
        if(ka.Slowtime.value)	mc.timer.timerSpeed = 0.90001F;
            boolean useRandom = ka.rnd.getValue() > 0;
            int cd = (int) (Criticals.getCrits().cMode.value.equals(CritMode.PACKET) ? ka.Cdel.getValue() : 0);
          	double attackDelay;
       //   	 if(ka.hitmiss.getValue()>=floatBounded)
       //   		attackDelay = 66.314;
        //  		else
          		attackDelay = (useRandom ? (ka.APS.getValue() + Helper.mathUtils().getRandom((int) ka.rnd.getValue())) : ka.APS.getValue()) ;
          	    attackDelay = attackDelay - (getTarget().getHealth()<ka.Sburst.getValue() ? (ka.SHIT.value ? Helper.mathUtils().getRandom((int)ka.SCPS.getValue()) : 0) : 0);
          	 //      if(attackDelay<ka.MCL.getValue())
      //      	attackDelay=attackDelay+Helper.mathUtils().getRandom((int) ka.rnd.getValue());
          	 if(attackDelay > ka.LAPS.getValue())
          		attackDelay = attackDelay - Helper.mathUtils().getRandom((int) ka.rnd.getValue());
          	 if (ticks >= (20 / attackDelay) && (getTarget().getHealth()<getTarget().getMaxHealth() ? getTarget().hurtTime<ka.hurttime.getValue() : true)) {
                delay++;
                if (critTimer.hasTimeElapsed((long) cd, true)) {
          	        if (Criticals.getCrits().cMode.value.equals(Criticals.CritMode.PACKET)){
                        Criticals.doCrit2(false);
                        Criticals.doCrit2(true);
          	        }
                    else ka.nextTick = true;
                }
                if (potTimer.hasTimeElapsed((long) ka.CDR.getValue(), false))
                    doAttack(getTarget());
                ticks = 0;
                changeTargets();
            }
        }
        if (ka.canBlock() && (ka.hasPlayerNear() || !ka.attackList.isEmpty())){
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
            //     Helper.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }      
    }
    
    public void burst(final Entity entity) {
        final ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
        final boolean wasSneaking = mc.thePlayer.isSneaking();
        if(new Random().nextBoolean()&&new Random().nextBoolean()&&ka.Heal.value&& mc.thePlayer.getFoodStats().getFoodLevel() > 10){
        	if (mc.thePlayer.onGround)
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
			else
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
        }
      //  mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
        //TODO:CombatHeal
        if(ka.Slowtime.value)   mc.timer.timerSpeed = 0.910001F;
        boolean b = false;
        Label_0061: {
            if (currentItem != null && currentItem.getItem().getItemUseAction(currentItem) == EnumAction.BLOCK) {
                if (mc.thePlayer.isBlocking()) {
                    b = true;
                    break Label_0061;
                }
            }
            b = false;
        }
        final boolean wasBlocking = b;
        if (wasSneaking) {
            final NetHandlerPlayClient netHandler = mc.getNetHandler();
            netHandler.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }
        if (wasBlocking) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
        for (int i = 0; i < ka.Btime.getValue(); ++i) {
        	
            mc.thePlayer.attackTargetEntityWithCurrentItem(entity);
            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
            mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            mc.thePlayer.attackTargetEntityWithCurrentItem(entity);
            if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().isItemEnchanted()) {
                mc.thePlayer.onEnchantmentCritical(entity);
             }
        }
        if (wasSneaking) {
            final NetHandlerPlayClient netHandler2 = mc.getNetHandler();
            netHandler2.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        }
        if (wasBlocking) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(currentItem));
        }
        mc.gameSettings.keyBindSneak.pressed = true;     
        if (wasBlocking) {
            mc.thePlayer.setItemInUse(currentItem, 101);
        }
        mc.gameSettings.keyBindSneak.pressed = false;
    }

    

    public void doRote(){
        if (ka.lockview.value) {
            EntityPlayerSP player = mc.thePlayer;
            EntityLivingBase target = getTarget();
			final double diffX = target.posX - player.posX;
            final double diffY = target.posY + target.getEyeHeight() * 0.9 - (player.posY + player.getEyeHeight());
            final double diffZ = target.posZ - player.posZ;
            final double dist = (double)MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
            final float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
            final float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
            final float[] neededRotations = { (false ? mc.thePlayer.getRotationYawHead() : player.rotationYaw) + MathHelper.wrapAngleTo180_float(yaw - (false ? mc.thePlayer.getRotationYawHead() : player.rotationYaw)), (false ? player.cameraPitch : player.rotationPitch) + MathHelper.wrapAngleTo180_float(pitch - (false ? player.cameraPitch : player.rotationPitch)) };
            final float[] rlyneed = (float[])neededRotations.clone();
            final float d0 = 0.0f - (false ? mc.thePlayer.getRotationYawHead() : player.rotationYaw);
            final float d0y = neededRotations[0] + d0;
            final boolean rotateRight = d0y > 0.0f;
            if (rotateRight) {
                neededRotations[0] = (false ? player.cameraYaw : player.rotationYaw) + Math.min(Math.abs(0.0f - d0y), 40.2f);
            }
            else {
                neededRotations[0] = (false ? player.cameraYaw : player.rotationYaw) - Math.min(Math.abs(0.0f - d0y), 40.2f);
            }
            Helper.sendPacket(new C03PacketPlayer.C05PacketPlayerLook(neededRotations[0], neededRotations[1], mc.thePlayer.onGround));
       //     mc.thePlayer.rotationYaw = neededRotations[0];
       //     mc.thePlayer.rotationPitch = neededRotations[1];
        }
    }
    
    
    public void doAttack(Entity target) {
    	//Helper.player().swingItem();
    	if(ka.Kwaa.value) doRote();
    	if(ka.Slowtime.value)mc.timer.timerSpeed = 0.910001F;
        if(mc.thePlayer.isBlocking() && !NoSlow.noslowing && ka.unblockq.value)
            Helper.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//TODO:timerspeed
    //    mc.timer.timerSpeed = 1.000001F;
        if (burstTimer.hasTicksElapsed((int) ka.Bcool.getValue()-(int)(getTarget().getHealth()<ka.Sburst.getValue() ? ka.Bbursts.getValue() : 0)) && ka.burst.value && new Random().nextBoolean()) {       	
        //   if(ka.debug.value) Logger.logChat("buring");
         	burst(target);
      //   	mc.thePlayer.setSneaking(false);
            burstTimer.reset();
         }
		if(ka.LNCPRayCast.value)
		{
	        MovingObjectPosition ray = null;
            ray = RaycastUtils.rayCast(mc.thePlayer, target.posX, target.posY + target.getEyeHeight() * 0.7, target.posZ);
        
        if (!mc.thePlayer.canEntityBeSeen(target)) {
            return;
        }
		}
        if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.isBlocking() && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getMaxItemUseDuration());
         }
        if (duraTimer.hasTimeElapsed(370L, true) && target instanceof EntityPlayer && ka.armorbreaker.value) {
            ka.nextTick = false;
            ka.doCritAttack(target, false);
            ka.doCritAttack(target, true);
            return;
        }
       
        Helper.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        
        if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().isItemEnchanted()) {
            mc.thePlayer.onEnchantmentCritical(target);
         if(new Random().nextBoolean()) mc.thePlayer.onEnchantmentCritical(target);
         }
    }
    
    private static int randomNumber(int max, int min) {
        return -min + (int)(Math.random() * (double)(max - -min + 1));
     }
    
    private void changeTargets(){
        if(ka.attackList.size() == 1)
            return;
        if(ka.sdelay.getValue() == 0)
            currentTarget += 1;
        if(delay >= ka.sdelay.getValue()){
            currentTarget += 1;
            delay = 0;
        }
        attack = false;
    }
    public  EntityLivingBase getTarget() {
        return (EntityLivingBase) ka.attackList.get(currentTarget);
    }
    @Override
    public void onPostMotion(EventPostMotionUpdates en) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }


    @Override
    public void onDisabled() {
        currentTarget = ticks = 0;
        attack = false;
        override = true;
        mc.timer.timerSpeed = 0.92F;
        if(!Helper.playerUtils().MovementInput())
            Helper.sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
   
        mc.timer.timerSpeed = 1.0F;
    }

    @Override
    public void onEnabled() {

    }
    public boolean canAttack() {
        if (!ka.attackList.isEmpty() && !Pandora.getModManager().getModState("Freecam") && getTarget() != null && !AutoPot.doPot)
            return true;
        return false;
    }

    public void updateProperties() {
        if(ka.mode.value.equals(this)) {
            Pandora.getPropertyManager().addProperty(ka.APS);
            Pandora.getPropertyManager().addProperty(ka.rnd);
            Pandora.getPropertyManager().addProperty(ka.sdelay);
        } else {
            Pandora.getPropertyManager().removeProperty(ka.APS);
            Pandora.getPropertyManager().removeProperty(ka.rnd);
            Pandora.getPropertyManager().removeProperty(ka.sdelay);
        }
        if (ka.mode.value.equals(ka.multiMode)) {
        	Pandora.getPropertyManager().addProperty(ka.multiDelay);
        	Pandora.getPropertyManager().addProperty(ka.targets);
        } else {
        	Pandora.getPropertyManager().removeProperty(ka.multiDelay);
        	Pandora.getPropertyManager().removeProperty(ka.targets);
        }
    }
}

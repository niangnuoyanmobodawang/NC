package nivia.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import nivia.events.Event;
import nivia.events.EventTarget;
import nivia.events.Priority;

import static net.minecraft.init.Items.bed;
import static net.minecraft.init.Items.comparator;
import static net.minecraft.init.Items.compass;
import static net.minecraft.init.Items.paper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import nivia.events.events.EventPacketSend;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.events.events.EventTick;
import nivia.managers.FriendManager;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.Wrapper;
import nivia.utils.utils.Timer;

public class TestingAura extends Module {
    

    private int lookDelay;
    private int blockDelay;
    private boolean isBlocking;
    private float oldYaw;
    private float oldPitch;
    private Timer pseudoTimer;
    private Timer angleTimer;
    private List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
    private int index;
    public boolean angle;
    private boolean setupTick;
    private boolean switchingTargets;

    public static EntityLivingBase pseudoTarget;
    private boolean crit;
    private int auraTicks;
    private boolean lastTickCrit;
    private static Timer timer = new Timer();
    public Property<Boolean> autoBlock = new Property<>(this, "Autoblock", true);
    public Property<Boolean> players = new Property<>(this, "Players", true);
    public Property<Boolean> animals = new Property<>(this, "Animals", false);
    public Property<Boolean> mobs = new Property<>(this, "Monsters", false);
    public Property<Boolean> invisibles = new Property<>(this, "Invisibles", true);
    public Property<Boolean> autoAttack = new Property<>(this, "autoAttack", false);
    public Property<Boolean> UseRandom = new Property<>(this, "RandomDelay", true);
    public Property<Boolean> silent = new Property<>(this, "silent", false);
    public Property<Boolean> rayTrace = new Property<>(this, "rayTrace", false);
    public DoubleProperty ticksExisted = new DoubleProperty(this, "Exist Ticks", 27, 0, 100);
    public DoubleProperty range = new DoubleProperty(this, "Reach", 4.4, 1, 10, 1);
    public DoubleProperty fov = new DoubleProperty(this, "FOV", 360, 15, 360);

    public DoubleProperty rand = new DoubleProperty(this, "Random", 5.0f, 0.0f, 6.0f);
    public DoubleProperty attackDelay = new DoubleProperty(this, "AttackDelay", 5.0f, 0.0f, 20.0f);

    public DoubleProperty aimSpeed = new DoubleProperty(this, "aimSpeed", 10.0f, 1.0f, 10.0f);

    public static String target = "Switch";
    public static String aimType = "Instant";
    public static String targetPriority = "Closest";

    

    public TestingAura() {
	super("AACAura", 0, 0xFFFFFF, Category.COMBAT, "Test killaura.", new String[] { "testaura" }, true);
    }
    

	@Override
	public void onDisable() {
		super.onDisable();
        this.lookDelay = 0;
    }
    
	@Override
	public void onEnable() {
		super.onEnable();
    }
    
    public void attackTarget(final EntityLivingBase entity) {
    	if (!entity.isDead) {
        final float sharpLevel = EnchantmentHelper.func_152377_a(this.mc.thePlayer.getHeldItem(), entity.getCreatureAttribute());
        this.mc.thePlayer.swingItem();
        this.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        if (sharpLevel > 0.0f) {
            this.mc.thePlayer.onEnchantmentCritical(entity);
        }
    	}
    }
    
    public float getPitchChange(final Entity entity) {
        final double deltaX = entity.posX - mc.getMinecraft().thePlayer.posX;
        final double deltaZ = entity.posZ - mc.getMinecraft().thePlayer.posZ;
        final double deltaY = entity.posY - 2.2 + entity.getEyeHeight() - mc.getMinecraft().thePlayer.posY;
        final double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        final double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(mc.getMinecraft().thePlayer.rotationPitch - (float)pitchToEntity) - 2.5f;
    }
    
    public float getYawChange(final Entity entity) {
        final double deltaX = entity.posX - mc.getMinecraft().thePlayer.posX;
        final double deltaZ = entity.posZ - mc.getMinecraft().thePlayer.posZ;
        double yawToEntity = 0.0;
        if (deltaZ < 0.0 && deltaX < 0.0) {
            yawToEntity = 90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        else if (deltaZ < 0.0 && deltaX > 0.0) {
            yawToEntity = -90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        else {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapAngleTo180_float(-(mc.getMinecraft().thePlayer.rotationYaw - (float)yawToEntity));
    }
    
    public float getDistanceToEntity(final TileEntity tileEntity) {
        float var2 = (float)(this.mc.thePlayer.posX - tileEntity.getPos().getX());
        var2 = (float)(this.mc.thePlayer.posY - tileEntity.getPos().getY());
        final float var3 = (float)(this.mc.thePlayer.posZ - tileEntity.getPos().getZ());
        return MathHelper.sqrt_float(var2 * var2 + var2 * var2 + var3 * var3);
    }
    
    private Float[] getRotations(final Entity entity) {
        final double posX = entity.posX - this.mc.thePlayer.posX;
        final double posZ = entity.posZ - this.mc.thePlayer.posZ;
        final double posY = entity.posY + entity.getEyeHeight() - this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight();
        final double helper = MathHelper.sqrt_double(posX * posX + posZ * posZ);
        float newYaw = (float)Math.toDegrees(-Math.atan(posX / posZ));
        final float newPitch = (float)(-Math.toDegrees(Math.atan(posY / helper)));
        if (posZ < 0.0 && posX < 0.0) {
            newYaw = (float)(90.0 + Math.toDegrees(Math.atan(posZ / posX)));
        }
        else if (posZ < 0.0 && posX > 0.0) {
            newYaw = (float)(-90.0 + Math.toDegrees(Math.atan(posZ / posX)));
        }
        return new Float[] { newYaw, newPitch };
    }
    
    public boolean isVisibleFOV(final Entity e, final Entity e2, final float fov) {
        return ((Math.abs(getRotations(e)[0] - e2.rotationYaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(this.getRotations(e)[0] - e2.rotationYaw) % 360.0f) : (Math.abs(this.getRotations(e)[0] - e2.rotationYaw) % 360.0f)) <= fov;
    }
    
    public boolean shouldHitEntity(final Entity e, final double range, final float fov, final boolean rayTrace, final int ticksExisted, final int invisibles, final int players, final int mobs, final int animals) {
        final boolean isAlive = e instanceof EntityLivingBase;
        final boolean isNotMe = e != this.mc.thePlayer;
        final boolean isNotNull = e != null;
        final boolean isInRange = this.mc.thePlayer.getDistanceToEntity(e) <= range;
        final boolean isInFov = this.isVisibleFOV(e, this.mc.thePlayer, fov);
        final boolean isNotDead = !e.isDead;
        final boolean ticks = e.ticksExisted >= ticksExisted;
        final boolean isNotFakeDummie = e.getName() != this.mc.thePlayer.getName();
        if (rayTrace) {
            return isAlive && isNotFakeDummie && isNotDead && ticks && isInFov && isNotMe && isNotNull && isInRange;
        }
        return isAlive && isNotFakeDummie && isNotDead && ticks && isInFov && isNotMe && isNotNull && isInRange;
    }
    
    public Entity getBestEntity(final double range, final float fov, final boolean rayTrace, final int ticksExisted, final int invisibles, final int players, final int mobs, final int animals) {
        Entity tempEntity = null;
        double dist = range;
        for (final Object i : this.mc.theWorld.loadedEntityList) {
            final boolean isValidEntity = (mobs == 1 && i instanceof EntityMob && !((Entity)i).isInvisible() && !(i instanceof EntityAnimal) && !(i instanceof EntityPlayer)) || (animals == 1 && i instanceof EntityAnimal && !((Entity)i).isInvisible() && !(i instanceof EntityMob) && !(i instanceof EntityPlayer)) || (players == 1 && i instanceof EntityPlayer && !((Entity)i).isInvisible() && !(i instanceof EntityAnimal) && !(i instanceof EntityMob) && !FriendManager.isFriend(((Entity)i).getName())) || (invisibles == 1 && ((Entity)i).isInvisible() && !FriendManager.isFriend(((Entity)i).getName()));
            if (isValidEntity) {
                final Entity entity = (Entity)i;
                if (!shouldHitEntity(entity, range, fov, rayTrace, ticksExisted, invisibles, players, mobs, animals)) {
                    continue;
                }
                final double curDist = this.mc.thePlayer.getDistanceToEntity(entity);
                if (curDist > dist) {
                    continue;
                }
                dist = curDist;
                tempEntity = entity;
            }
        }
        return tempEntity;
    }
    
	@EventTarget
    public void preTick(final EventTick e) {
    //    if (this.isToggled()) {
            if (silent.value) {
                this.oldYaw = mc.thePlayer.rotationYaw;
                this.oldPitch = mc.thePlayer.rotationPitch;
            }
   //         if (target.getSelectedOption().equalsIgnoreCase("Switch")) {
            	this.setSuffix("Switch");
                Entity entity = null;
                if (target != null) {
                    entity = getBestEntity(range.getValue(), (float)fov.getValue(), rayTrace.value, (int) ticksExisted.getValue(), invisibles.value ? 1 : 0, players.value ? 1 : 0, mobs.value ? 1 : 0, animals.value ? 1 : 0);
                }
                if (entity != null) {                 
                    final EntityPlayerSP thePlayer = mc.getMinecraft().thePlayer;
                    thePlayer.rotationPitch += getPitchChange(entity) / aimSpeed.getValue();
                    final EntityPlayerSP thePlayer2 = mc.thePlayer;
                    thePlayer2.rotationYaw += getYawChange(entity) / aimSpeed.getValue();
                }
                ++this.lookDelay;
                double attackDela2y = UseRandom.value ? (attackDelay.getValue() - Helper.mathUtils().getRandom((int) rand.getValue())) : attackDelay.getValue();               
                if (this.lookDelay >= attackDela2y && autoAttack.value) {
                    attackTarget((EntityLivingBase)entity);
                    if (autoBlock.value && mc.thePlayer.isSwingInProgress && this.mc.thePlayer.inventory.getCurrentItem() != null && !this.mc.thePlayer.isBlocking() && this.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                        this.mc.thePlayer.setItemInUse(this.mc.thePlayer.inventory.getCurrentItem(), this.mc.thePlayer.inventory.getCurrentItem().getMaxItemUseDuration());
                    	//mc.thePlayer.swingProgressInt = 3;
                    }
                    else if (autoBlock.value && mc.thePlayer.isSwingInProgress && this.mc.thePlayer.inventory.getCurrentItem() != null && !this.mc.thePlayer.isBlocking() && this.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                        this.mc.thePlayer.setItemInUse(this.mc.thePlayer.inventory.getCurrentItem(), this.mc.thePlayer.inventory.getCurrentItem().getMaxItemUseDuration());
                    	//mc.thePlayer.swingProgressInt = 3;
                }
                    this.lookDelay = 1;
                }
        //    }
                /*
            if (false) {
            	this.setModInfo(" ?Good Aura");
                for (final Object i : Minecraft.theWorld.loadedEntityList) {
                    final Entity entity2 = (Entity)i;
                    if (Client.getClientHelper().shouldHitEntity(entity2, range.getFloatValue(), fov.getFloatValue(), rayTrace.value, ticksExisted.getIntValue(), invisibles.value ? 1 : 0, players.value ? 1 : 0, mobs.value ? 1 : 0, animals.value ? 1 : 0) && entity2 != null && autoAttack.value) {
                        Client.getClientHelper().attackTarget((EntityLivingBase)entity2);
                        if (autoBlock.value && this.mc.thePlayer.inventory.getCurrentItem() != null && !this.mc.thePlayer.isBlocking() && this.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                            this.mc.thePlayer.setItemInUse(this.mc.thePlayer.inventory.getCurrentItem(), this.mc.thePlayer.inventory.getCurrentItem().getMaxItemUseDuration());
                        }
                        else if (autoBlock.value && this.mc.thePlayer.inventory.getCurrentItem() != null && !this.mc.thePlayer.isBlocking() && this.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                            this.mc.thePlayer.setItemInUse(this.mc.thePlayer.inventory.getCurrentItem(), this.mc.thePlayer.inventory.getCurrentItem().getMaxItemUseDuration());
                        
                    }
                    }
                }
            }
            */

            if (autoBlock.value && this.isBlocking && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, getBlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), getEnumFacing((int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ)));
                this.isBlocking = false;
            }
            if (!autoBlock.value && mc.thePlayer.isUsingItem() && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, getBlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), getEnumFacing((int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ)));
            }
   //     }
    }
    
    public EnumFacing getEnumFacing(final float posX, final float posY, final float posZ) {
        return EnumFacing.func_176737_a(posX, posY, posZ);
    }
	
    public BlockPos getBlockPos(final double x, final double y, final double z) {
        final BlockPos pos = new BlockPos(x, y, z);
        return pos;
    }
    
    @EventTarget(Priority.HIGH)
    public void onPre(EventPreMotionUpdates e) {
        if(mc.thePlayer.isDead && this.getState()){
            this.setState(false);
            Logger.logChat("Killaura 死亡自动关闭!");
        }else{
            if (Helper.mc().getCurrentServerData().serverIP.toLowerCase().contains("hypixel") && mc.thePlayer.inventory.hasItem(bed) && mc.thePlayer.inventory.hasItem(comparator) && mc.thePlayer.inventory.hasItem(compass) && mc.thePlayer.inventory.hasItem(paper)) {
                this.setState(false);
                Logger.logChat("Killaura 死亡自动关闭!");
            }
        }
    }
	
	@EventTarget(0)
    public void postTick(final EventTick e) {


    //    if (this.isToggled()) {
            if (silent.value) {
                mc.thePlayer.rotationPitch = this.oldPitch;
                mc.thePlayer.rotationYaw = this.oldYaw;
                
            }
        	//mc.thePlayer.swingProgressInt = 5;
        	//mc.thePlayer.swingProgressInt = 3;
   //     }
    }
    
    private void swap(final int slot, final int hotbar) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbar, 2, mc.thePlayer);
    }
    
    private void rotateAAC(final EntityPlayerSP player, final EntityLivingBase target) {

    }
    
    private int randomDelay()
    {
      Random randy = new Random();
      int randyInt = randy.nextInt(2000) + 2000;
      return randyInt;
    }
    
}

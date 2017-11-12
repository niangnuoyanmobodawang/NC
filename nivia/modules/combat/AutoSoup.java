package nivia.modules.combat;

import static net.minecraft.init.Items.bed;
import static net.minecraft.init.Items.comparator;
import static net.minecraft.init.Items.compass;
import static net.minecraft.init.Items.paper;

import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import nivia.commands.Command;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventPacketReceive;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.CommandManager;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.utils.Timer;

public class AutoSoup extends Module {
    public static int pots;
    private static boolean souping = false;
    private final Timer timer = new Timer();
    public DoubleProperty delay = new DoubleProperty(this, "Delay", 4050L, 3, 10000);
    public Property<Boolean> death = new Property<>(this, "AutoDisable", false);
    public DoubleProperty health = new DoubleProperty(this, "Health", 10, 1, 40);
    public Property<Boolean> head = new Property<>(this, "UHCHead", true);
    public Property<Boolean> AGF = new Property<>(this, "AutoGetFromInv", false);
    private float oldYaw, oldPitch;
    private boolean needsToPot = false;
    private int stage = 1;
    private int oldSlot; 

    @EventTarget(Priority.HIGH)
    public void onPre(EventPreMotionUpdates e) {
        if(mc.thePlayer.isDead && death.value && this.getState()){
            this.setState(false);
            Logger.logChat("AutoHeal 死亡自动关闭!");
        }else{
            if (Helper.mc().getCurrentServerData().serverIP.toLowerCase().contains("hypixel") && mc.thePlayer.inventory.hasItem(bed) && mc.thePlayer.inventory.hasItem(comparator) && mc.thePlayer.inventory.hasItem(compass) && mc.thePlayer.inventory.hasItem(paper)) {
                this.setState(false);
                Logger.logChat("AutoHeal 死亡自动关闭!");
            }
        }
    }
    
    
    public AutoSoup() {
        super("AutoHeal", 0, 0xFFCCAADD, Category.COMBAT, "Throws potions automatically",
                new String[]{"as", "autos", "asoup"}, true);
    }

    public static boolean isSouping() {
        return souping;
    }

    @EventTarget
    public void EventPreMotionUpdates(EventPreMotionUpdates event) {
        setSuffix(String.valueOf(pots));
        if (this.updateCounter() == 0) {
            souping = false;
            return;
        }
        if (this.timer.hasTimeElapsed((long) this.delay.getValue(), false) && this.needsToPot) {
            if (this.doesHotbarHaveSoups())
                souping = true;

        }
    }

    @EventTarget
    public void onPost(EventPostMotionUpdates event) {
        this.needsToPot = mc.thePlayer.getHealth() <= this.health.getValue();
        if (this.needsToPot) {       	
            if (this.doesHotbarHaveSoups()) {
                if (souping) {
                    if (this.needsToPot) {
                        for (int i = 36; i < 45; i++) {
                            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (stack != null) {
                                if (this.isSoup(stack)) {                                    
                                    if (stage == 1) {
                                        if (this.timer.hasTimeElapsed((long) (this.delay.getValue()), true)) {
                                            stage++;
                                        }
                                    } else if (stage == 2) {
                                        stage = 1;
                                        oldSlot = mc.thePlayer.inventory.currentItem;
                                        Helper.sendPacket(new C09PacketHeldItemChange(i - 36));
                                        Helper.sendPacket(new C08PacketPlayerBlockPlacement(stack));
                                    //    Helper.sendPacket(new C09PacketHeldItemChange(oldSlot));
                                        if(this.isSoup(mc.thePlayer.inventoryContainer.getSlot(mc.thePlayer.inventory.currentItem).getStack()))
                                        	Helper.sendPacket(new C09PacketHeldItemChange(0));
                                        	else  
                                        	Helper.sendPacket(new C09PacketHeldItemChange(oldSlot));
                                        
                                        this.needsToPot = false;
                                        souping = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    this.timer.reset();
                }
            } else if(AGF.value){
                getSoupsFromInventory();               
            }
        }
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive e) {
        final EventPacketReceive packet = e;
        if (packet.getPacket() instanceof S06PacketUpdateHealth) {
            final S06PacketUpdateHealth packetUpdateHealth = (S06PacketUpdateHealth) packet.getPacket();
            if (!needsToPot)
                this.needsToPot = packetUpdateHealth.getHealth() <= this.health.getValue();

        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        souping = false;
        this.needsToPot = false;
    }

    private boolean doesHotbarHaveSoups() {
        for (int i = 36; i < 45; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null) {
                if (this.isSoup(stack))
                    return true;
            }
        }
        return false;
    }

    private void getSoupsFromInventory() {
        for (int i = 9; i < 36; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null) {
                if (this.isSoup(stack)) {
                	oldSlot = mc.thePlayer.inventory.currentItem;                    
                    mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, i, 1, 2, mc.thePlayer);
                    Helper.sendPacket(new C09PacketHeldItemChange(oldSlot));
                    ///
                    break;
                }
            }
        }
    }

    private boolean isSoup(final ItemStack stack) {
        if (stack != null) {
            if (stack.getItem() instanceof ItemSoup || head.value ? stack.getItem() instanceof ItemSkull : stack.getItem() instanceof ItemSoup )
                return true;
        }
        return false;
    }

    private int updateCounter() {
        pots = 0;
        for (int i = 9; i < 45; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null) {
                if (isSoup(stack))
                    pots += stack.stackSize;
            }
        }
        return pots;
    }

    protected void addCommand() {
        CommandManager.cmds.add(new Command("AutoSoup", "Manages AutoSoup stuff",
                Logger.LogExecutionFail("Option, Options:", new String[]{"Health", "Delay", "Values"}), "as",
                "autos", "asoup") {
            @Override
            public void execute(String commandName, String[] arguments) {
                String message = arguments[1];
                switch (message) {
                    case "health":
                    case "hp":
                    case "Health":
                    case "h":
                        try {
                            String message2 = arguments[2];
                            switch (message2) {
                                case "actual":
                                case "value":
                                    logValue(health);
                                    break;
                                case "reset":
                                    health.value = health.getDefaultValue();
                                    Logger.logSetMessage("AutoSoup", "Health", health);
                                    break;
                                default:
                                    Double nHP = Double.parseDouble(message2);
                                    health.setValue(nHP);
                                    Logger.logSetMessage("AutoSoup", "Health", health);
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                        }

                    case "Delay":
                    case "delay":
                    case "d":
                        String message21 = arguments[2];
                        switch (message21) {
                            case "actual":
                            case "value":
                                logValue(delay);
                                break;
                            case "reset":
                                delay.reset();
                                Logger.logSetMessage("AutoSoup", "delay", delay);
                                break;
                            default:
                                Long nD = Long.parseLong(message21);
                                delay.setValue(nD);
                                Logger.logSetMessage("AutoSoup", "delay", delay);
                                break;
                        }
                        break;
                    case "values":
                    case "actual":
                        logValues();
                        break;
                    default:
                        Logger.LogExecutionFail("Option, Options:", new String[]{"Health", "Delay", "Values"});
                        break;

                }
            }
        });
    }
}
package nivia.modules.combat;

import java.util.Random;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import nivia.events.EventTarget;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.modules.Module;
import nivia.utils.utils.Timer;

public class AutoArmor extends Module {
    public Timer timer = new Timer();
    private DoubleProperty delay = new DoubleProperty(this, "delay", 120.0D, 3.00, 222.00, 1);
    
    public AutoArmor() {
	super("AutoArmor", 0, 0xE6B800, Category.COMBAT,
		"Automatically equips the next available armor on the inventory.",
		new String[] { "aar", "aa", "autoa", "aarmor" }, true);
	this.addHook(EventPreMotionUpdates.class, this::onPre);
    }

    @EventTarget
    public void onPre(EventPreMotionUpdates e) {

        if (mc.thePlayer != null && (mc.currentScreen instanceof GuiInventory)) {
            int slotID = -1;
            double maxProt = -1.0;
            int switchArmor = -1;
            for (int i = 9; i < 45; ++i) {
                double protValue;
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack == null || !this.canEquip(stack) && (!this.betterCheck(stack) || this.canEquip(stack))) continue;
                if (this.betterCheck(stack) && switchArmor == -1) {
                    switchArmor = this.betterSwap(stack);
                }
                if ((protValue = this.getProtectionValue(stack)) < maxProt) continue;
                slotID = i;
                maxProt = protValue;
            }
            if (slotID != -1 && this.timer.hasTimeElapsed((long) delay.getValue())) {
                if (switchArmor != -1) {
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 4 + switchArmor, 0, 0, mc.thePlayer);
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0, mc.thePlayer);
                }
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotID, 0, 1, mc.thePlayer);
                this.timer.reset();
            }
        }

    }

    private boolean betterCheck(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            if (mc.thePlayer.getEquipmentInSlot(1) != null && stack.getUnlocalizedName().contains("boots") && this.getProtectionValue(stack) + (double)((ItemArmor)stack.getItem()).damageReduceAmount > this.getProtectionValue(mc.thePlayer.getEquipmentInSlot(1)) + (double)((ItemArmor)mc.thePlayer.getEquipmentInSlot((int)1).getItem()).damageReduceAmount) {
                return true;
            }
            if (mc.thePlayer.getEquipmentInSlot(2) != null && stack.getUnlocalizedName().contains("leggings") && this.getProtectionValue(stack) + (double)((ItemArmor)stack.getItem()).damageReduceAmount > this.getProtectionValue(mc.thePlayer.getEquipmentInSlot(2)) + (double)((ItemArmor)mc.thePlayer.getEquipmentInSlot((int)2).getItem()).damageReduceAmount) {
                return true;
            }
            if (mc.thePlayer.getEquipmentInSlot(3) != null && stack.getUnlocalizedName().contains("chestplate") && this.getProtectionValue(stack) + (double)((ItemArmor)stack.getItem()).damageReduceAmount > this.getProtectionValue(mc.thePlayer.getEquipmentInSlot(3)) + (double)((ItemArmor)mc.thePlayer.getEquipmentInSlot((int)3).getItem()).damageReduceAmount) {
                return true;
            }
            if (mc.thePlayer.getEquipmentInSlot(4) != null && stack.getUnlocalizedName().contains("helmet") && this.getProtectionValue(stack) + (double)((ItemArmor)stack.getItem()).damageReduceAmount > this.getProtectionValue(mc.thePlayer.getEquipmentInSlot(4)) + (double)((ItemArmor)mc.thePlayer.getEquipmentInSlot((int)4).getItem()).damageReduceAmount) {
                return true;
            }
        }
        return false;
    }

    private int betterSwap(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor) {
            if (mc.thePlayer.getEquipmentInSlot(1) != null && stack.getUnlocalizedName().contains("boots") && this.getProtectionValue(stack) + (double)((ItemArmor)stack.getItem()).damageReduceAmount > this.getProtectionValue(mc.thePlayer.getEquipmentInSlot(1)) + (double)((ItemArmor)mc.thePlayer.getEquipmentInSlot((int)1).getItem()).damageReduceAmount) {
                return 4;
            }
            if (mc.thePlayer.getEquipmentInSlot(2) != null && stack.getUnlocalizedName().contains("leggings") && this.getProtectionValue(stack) + (double)((ItemArmor)stack.getItem()).damageReduceAmount > this.getProtectionValue(mc.thePlayer.getEquipmentInSlot(2)) + (double)((ItemArmor)mc.thePlayer.getEquipmentInSlot((int)2).getItem()).damageReduceAmount) {
                return 3;
            }
            if (mc.thePlayer.getEquipmentInSlot(3) != null && stack.getUnlocalizedName().contains("chestplate") && this.getProtectionValue(stack) + (double)((ItemArmor)stack.getItem()).damageReduceAmount > this.getProtectionValue(mc.thePlayer.getEquipmentInSlot(3)) + (double)((ItemArmor)mc.thePlayer.getEquipmentInSlot((int)3).getItem()).damageReduceAmount) {
                return 2;
            }
            if (mc.thePlayer.getEquipmentInSlot(4) != null && stack.getUnlocalizedName().contains("helmet") && this.getProtectionValue(stack) + (double)((ItemArmor)stack.getItem()).damageReduceAmount > this.getProtectionValue(mc.thePlayer.getEquipmentInSlot(4)) + (double)((ItemArmor)mc.thePlayer.getEquipmentInSlot((int)4).getItem()).damageReduceAmount) {
                return 1;
            }
        }
        return -1;
    }

    private boolean canEquip(ItemStack stack) {
        return mc.thePlayer.getEquipmentInSlot(1) == null && stack.getUnlocalizedName().contains("boots") || mc.thePlayer.getEquipmentInSlot(2) == null && stack.getUnlocalizedName().contains("leggings") || mc.thePlayer.getEquipmentInSlot(3) == null && stack.getUnlocalizedName().contains("chestplate") || mc.thePlayer.getEquipmentInSlot(4) == null && stack.getUnlocalizedName().contains("helmet");
    }

    private double getProtectionValue(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemArmor)) {
            return 0.0;
        }
        return (double)((ItemArmor)stack.getItem()).damageReduceAmount + (double)((100 - ((ItemArmor)stack.getItem()).damageReduceAmount * 4) * EnchantmentHelper.getEnchantmentLevel(Enchantment.field_180310_c.effectId, stack) * 4) * 0.0075;
    }
}
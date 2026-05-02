package yc.ycqin.nb.recipe;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yc.ycqin.nb.common.item.armor.AntiFogGoggles;


public class RecipeAntiFogAttachment extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean hasGoggles = false;
        boolean hasHelmet = false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof AntiFogGoggles && !hasGoggles) {
                    hasGoggles = true;
                } else if (stack.getItem() instanceof ItemArmor && ((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.HEAD && !hasHelmet) {
                    hasHelmet = true;
                } else {
                    return false; // 多余物品
                }
            }
        }
        return hasGoggles && hasHelmet;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack helmet = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && !(stack.getItem() instanceof AntiFogGoggles)) {
                helmet = stack.copy();
                break;
            }
        }
        if (helmet.isEmpty()) return ItemStack.EMPTY;
        // 添加防雾标记
        helmet.getOrCreateSubCompound("AntiFog").setBoolean("Active", true);
        return helmet;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        // 保证眼镜被消耗
        return remaining;
    }
}
package yc.ycqin.nb.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yc.ycqin.nb.event.ProtectedMobHandler;
import yc.ycqin.nb.register.ItemsRegister;
import yc.ycqin.nb.util.EntityClassifier;
import yc.ycqin.nb.ycqin;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRecruitmentOrder extends Item {
    public ItemRecruitmentOrder(){
        this.setRegistryName("recruitment_order");
        this.setUnlocalizedName(ycqin.MODID+"."+"recruitment_order");
        this.setCreativeTab(ItemsRegister.YCQIN_TABLE);
        this.setMaxStackSize(1);
    }



    public static boolean tryRecruit(EntityPlayer player, Entity target, ItemStack stack, boolean consume) {
        if (target.getEntityData().hasKey("yc_protectcoth") && !EntityClassifier.isTamed((EntityLivingBase) target)) {
            EntityClassifier.setOwner((EntityLivingBase) target, player.getName());
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "你已成功招募" + target.getName() + "！"));
            // 重新应用 AI（如果 AI 需要重新加载，调用 ProtectedMobHandler.applyAI 方法）
            if (target instanceof net.minecraft.entity.monster.EntityMob) {
                // 假设你有这个方法
                ProtectedMobHandler.applyCustomAI((EntityMob) target);
            }
            if (consume) {
                stack.shrink(1);
            }
            return true;
        }
        player.sendMessage(new TextComponentString(TextFormatting.RED + "这个生物无法被招募！"));
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("item.ycqin.recruitment_order.tooltip"));
    }
}

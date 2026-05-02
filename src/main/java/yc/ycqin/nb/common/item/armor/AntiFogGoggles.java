package yc.ycqin.nb.common.item.armor;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yc.ycqin.nb.register.ItemsRegister;
import yc.ycqin.nb.ycqin;

import javax.annotation.Nullable;
import java.util.List;

public class AntiFogGoggles extends ItemArmor {

    private static final ArmorMaterial GOGGLES_MATERIAL = EnumHelper.addArmorMaterial(
            "yc_goggles",                          // 内部名称
            "ycqin:yc_goggles",                    // 纹理前缀（可不存在）
            0,                                  // 耐久基数（设为0）
            new int[]{0, 0, 0, 0},              // 各部位护甲值（头盔、胸甲、护腿、靴子）
            0,                                  // 附魔能力
            SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, // 装备音效
            0.0F                                // 韧性
    );

    public AntiFogGoggles() {
        super(GOGGLES_MATERIAL, 0, EntityEquipmentSlot.HEAD);
        this.setRegistryName("fog_goggles");
        this.setUnlocalizedName(ycqin.MODID+"."+"fog_goggles");
        this.setCreativeTab(ItemsRegister.YCQIN_TABLE);
        this.setMaxStackSize(1);
    }

    // 检查玩家是否装备了眼镜（头盔槽）
    public static boolean isPlayerWearing(EntityPlayer player) {
        if (player == null) return false;
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        return !helmet.isEmpty() && helmet.getItem() instanceof AntiFogGoggles;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    // 隐藏耐久条
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("§7佩戴后免疫灰色迷雾");
        tooltip.add("§7可与任意头盔合成转移效果");
    }


}
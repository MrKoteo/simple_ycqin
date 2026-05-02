package yc.ycqin.nb.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PotionStun extends Potion {

    public PotionStun() {
        super(false, 0xAAAAAA); // 灰色药水颜色
        setRegistryName("ycqin:stun");
        setPotionName("effect.ycqin.stun");
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        // 每 tick 强制设置速度为 0，并阻止转向
        if (entity.motionY > 0) entity.motionY = -0.07;
        entity.prevRotationYaw = entity.rotationYaw;
        entity.prevRotationPitch = entity.rotationPitch;
        // 如果是玩家，取消移动输入
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.moveForward = 0;
            player.moveStrafing = 0;
            player.capabilities.isFlying = false;
        } else {
            if (entity instanceof EntityLiving){
                ((EntityLiving) entity).getNavigator().clearPath();
            }
        }
        // 清除攻击冷却（使无法攻击）
        entity.hurtResistantTime = 20;
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        // 每 tick 都需要执行效果
        return true;
    }

    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return true;
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    private final ResourceLocation icon = new ResourceLocation("ycqin", "textures/gui/potion_stun.png");

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        mc.getTextureManager().bindTexture(icon);
        Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        mc.getTextureManager().bindTexture(icon);
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
    }

    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(UUID.fromString("02f3eb25-00c0-4769-b2ab-40f1cccf90db"), "stun_speed", -1.0, 2).setSaved(false);

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap map, int p_111185_3_) {
        super.applyAttributesModifiersToEntity(entity, map, p_111185_3_);
        IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (speed != null && !speed.hasModifier(SPEED_MODIFIER)) {
            speed.applyModifier(SPEED_MODIFIER);
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap map, int p_111187_3_) {
        IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (speed != null && speed.hasModifier(SPEED_MODIFIER)) {
            speed.removeModifier(SPEED_MODIFIER);
        }
        super.removeAttributesModifiersFromEntity(entity,map,p_111187_3_);
    }
}
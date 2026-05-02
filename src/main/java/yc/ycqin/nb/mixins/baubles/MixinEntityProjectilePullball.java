package yc.ycqin.nb.mixins.baubles;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectilePullball;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.datafix.fixes.EntityId;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yc.ycqin.nb.common.item.ItemPullImmunityCharm;

@Mixin(EntityProjectilePullball.class)
public abstract class MixinEntityProjectilePullball {

    // 辅助方法：检查免疫
    @Optional.Method(modid = "baubles")
    private static boolean hasImmunity(EntityLivingBase target) {
        if (target instanceof EntityPlayer && Loader.isModLoaded("baubles")) {
            EntityPlayer player = (EntityPlayer) target;
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemPullImmunityCharm) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 重定向获取实体ID的函数，如果目标免疫则返回0（无效ID），避免被拉拽
     */
    @Optional.Method(modid = "baubles")
    @Redirect(
            method = "func_70071_h_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_145782_y()I"),
            remap = false
    )
    private int redirectGetEntityId(EntityLivingBase mob) {
        if (hasImmunity(mob)) {
            return 0; // 无效ID，不会触发锁定
        }
        return mob.getEntityId(); // 原ID

    }
}

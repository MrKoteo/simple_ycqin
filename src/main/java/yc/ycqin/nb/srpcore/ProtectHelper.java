package yc.ycqin.nb.srpcore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import yc.ycqin.nb.config.ModConfig;

import java.lang.reflect.Field;

public class ProtectHelper {

    private static final float DAMAGE_CAP_RATIO = ModConfig.damageCapRatio;       // 单次限伤比例
    private static final float WINDOW_DAMAGE_RATIO = ModConfig.damageCapRatio;    // 窗口总伤害上限比例
    private static final int WINDOW_TICKS = 40;                // 窗口时间2秒 = 40 tick

    public static float getHealth(EntityLivingBase self) {
        float current = getRealHealth(self);
        NBTTagCompound data = self.getEntityData();

        if (!data.hasKey("yc_protectcoth")) {
            return current;
        }

        float last = data.getFloat("yc_last_health");
        float diff = last - current;   // 减少量，正数表示受伤

        if (diff <= 0) {
            // 治疗或不变，只更新记录，不参与限伤
            data.setFloat("yc_last_health", current);
            return current;
        }

        // 单次限伤阈值
        float maxHealth = self.getMaxHealth();
        float singleCap = maxHealth * DAMAGE_CAP_RATIO;
        // 窗口总伤害上限
        float windowCap = maxHealth * WINDOW_DAMAGE_RATIO;

        // 读取并清理窗口内的伤害记录
        NBTTagList damageWindow = data.getTagList("yc_damage_window", 10);
        long now = self.world.getTotalWorldTime();
        float totalWindowDamage = 0;
        int i = 0;
        while (i < damageWindow.tagCount()) {
            NBTTagCompound rec = damageWindow.getCompoundTagAt(i);
            long time = rec.getLong("time");
            if (now - time <= WINDOW_TICKS) {
                totalWindowDamage += rec.getFloat("damage");
                i++;
            } else {
                damageWindow.removeTag(i); // 超出窗口，移除
            }
        }

        // 可允许的额外伤害（窗口剩余额度）
        float allowedWindowRemaining = Math.max(0, windowCap - totalWindowDamage);

        // 单次限伤后的理想伤害
        float limitedDamage = Math.min(diff, singleCap);
        // 再受窗口限制
        float finalDamage = Math.min(limitedDamage, allowedWindowRemaining);

        if (finalDamage < diff) {
            // 需要修改生命值
            float corrected = last - finalDamage;
            if (corrected < 0) corrected = 0;
            self.setHealth(corrected);
            // 记录本次实际扣除的伤害
            NBTTagCompound newRec = new NBTTagCompound();
            newRec.setLong("time", now);
            newRec.setFloat("damage", finalDamage);
            damageWindow.appendTag(newRec);
            // 限制列表长度（防止无限增长）
            while (damageWindow.tagCount() > 20) damageWindow.removeTag(0);
            data.setTag("yc_damage_window", damageWindow);
            data.setFloat("yc_last_health", corrected);
            return corrected;
        } else {
            // 未超过限制，正常记录
            data.setFloat("yc_last_health", current);
            // 但还是需要将本次伤害加入窗口（用于累计）
            NBTTagCompound newRec = new NBTTagCompound();
            newRec.setLong("time", now);
            newRec.setFloat("damage", diff);
            damageWindow.appendTag(newRec);
            while (damageWindow.tagCount() > 20) damageWindow.removeTag(0);
            data.setTag("yc_damage_window", damageWindow);
            return current;
        }
    }

    // 通过反射获取实时的真实生命值
    private static float getRealHealth(EntityLivingBase self) {
        try {
            // 映射字段 "HEALTH" (MCP name) 或 "field_184632_c" (SRG)
            java.lang.reflect.Field field = EntityLivingBase.class.getDeclaredField("field_184632_c");
            if (field == null) {
                field = EntityLivingBase.class.getDeclaredField("HEALTH");
            }
            field.setAccessible(true);
            net.minecraft.network.datasync.DataParameter<Float> key = (net.minecraft.network.datasync.DataParameter<Float>) field.get(null);
            return self.getDataManager().get(key);
        } catch (Exception e) {
            // 降级方案：直接调用 getHealth（可能触发递归，但递归风险低，因为我们已经重写了 getHealth）
            return self.getHealth();
        }
    }
}
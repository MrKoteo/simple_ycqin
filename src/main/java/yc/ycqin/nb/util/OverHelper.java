package yc.ycqin.nb.util;
import com.overlast.cap.courage.CourageProvider;
import com.overlast.cap.courage.ICourage;
import com.overlast.cap.sanity.ISanity;
import com.overlast.cap.sanity.SanityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;


public class OverHelper {
    // ==================== 理智（Sanity）相关 ====================

    /**
     * 获取玩家的当前理智值。
     * @param player 玩家实体
     * @return 理智值，如果无法获取则返回 -1
     */
    public static float getSanity(EntityPlayer player) {
        if (player == null) return -1;
        ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
        return sanity != null ? sanity.getSanity() : -1;
    }

    /**
     * 获取玩家的最大理智值。
     */
    public static float getMaxSanity(EntityPlayer player) {
        if (player == null) return -1;
        ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
        return sanity != null ? sanity.getMaxSanity() : -1;
    }

    /**
     * 设置玩家的理智值（直接赋值）。
     * 仅在服务端生效。
     * @param player 玩家实体（建议为 EntityPlayerMP）
     * @param value 目标理智值（会自动钳制在最小/最大值内，取决于实现）
     * @return 是否成功
     */
    public static boolean setSanity(EntityPlayer player, float value) {
        if (!isServerSide(player)) return false;
        ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
        if (sanity != null) {
            sanity.set(value);
            return true;
        }
        return false;
    }

    /**
     * 增加玩家的理智值（可为负数）。
     * 仅在服务端生效。
     */
    public static boolean addSanity(EntityPlayer player, float delta) {
        if (!isServerSide(player)) return false;
        ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
        if (sanity != null) {
            sanity.increase(delta);
            return true;
        }
        return false;
    }

    /**
     * 减少玩家的理智值（封装正数减少）。
     */
    public static boolean reduceSanity(EntityPlayer player, float amount) {
        return addSanity(player, -amount);
    }

    // ==================== 勇气（Courage）相关 ====================

    /**
     * 获取玩家的当前勇气值。
     */
    public static float getCourage(EntityPlayer player) {
        if (player == null) return -1;
        ICourage courage = player.getCapability(CourageProvider.COURAGE_CAP, null);
        return courage != null ? courage.getCourage() : -1;
    }

    /**
     * 获取玩家的最大勇气值。
     */
    public static float getMaxCourage(EntityPlayer player) {
        if (player == null) return -1;
        ICourage courage = player.getCapability(CourageProvider.COURAGE_CAP, null);
        return courage != null ? courage.getMaxCourage() : -1;
    }

    /**
     * 设置玩家的勇气值。
     * 仅在服务端生效。
     */
    public static boolean setCourage(EntityPlayer player, float value) {
        if (!isServerSide(player)) return false;
        ICourage courage = player.getCapability(CourageProvider.COURAGE_CAP, null);
        if (courage != null) {
            courage.set(value);
            return true;
        }
        return false;
    }

    /**
     * 增加玩家的勇气值（可为负数）。
     * 仅在服务端生效。
     */
    public static boolean addCourage(EntityPlayer player, float delta) {
        if (!isServerSide(player)) return false;
        ICourage courage = player.getCapability(CourageProvider.COURAGE_CAP, null);
        if (courage != null) {
            courage.increase(delta);
            return true;
        }
        return false;
    }

    /**
     * 减少玩家的勇气值。
     */
    public static boolean reduceCourage(EntityPlayer player, float amount) {
        return addCourage(player, -amount);
    }

    // ==================== 私有辅助 ====================

    /**
     * 判断是否在逻辑服务端，并且玩家是 EntityPlayerMP。
     * 因为 Capability 的数据修改必须发生在服务端。
     */
    private static boolean isServerSide(EntityPlayer player) {
        if (player == null) return false;
        // 检查是否服务端线程，且玩家是 MP 类型
        return !player.world.isRemote && player instanceof EntityPlayerMP;
    }
}

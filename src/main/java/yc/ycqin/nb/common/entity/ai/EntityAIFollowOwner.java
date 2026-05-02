package yc.ycqin.nb.common.entity.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import yc.ycqin.nb.util.EntityClassifier;

public class EntityAIFollowOwner extends EntityAIBase {
    private final EntityMob tameable;
    private EntityPlayer owner;
    private final double followSpeed;
    private final PathNavigate navigator;
    private int timeToRecalcPath;
    private final float minDist;
    private final float maxDist;
    private float oldWaterCost;

    public EntityAIFollowOwner(EntityMob tameable, double speed, float minDist, float maxDist) {
        this.tameable = tameable;
        this.followSpeed = speed;
        this.navigator = tameable.getNavigator();
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (!EntityClassifier.isTamed(tameable)) return false;
        EntityPlayer ownerCandidate = EntityClassifier.getOwner(tameable, tameable.world);
        if (ownerCandidate == null) return false;
        if (ownerCandidate.isSpectator()) return false;
        if (tameable.getAttackTarget() != null) return false; // 战斗中不跟随
        if (tameable.getDistanceSq(ownerCandidate) < (double)(minDist * minDist)) return false;
        this.owner = ownerCandidate;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !navigator.noPath() && tameable.getDistanceSq(owner) > (double)(maxDist * maxDist) &&
                tameable.getAttackTarget() == null && !tameable.isRiding();
    }

    @Override
    public void startExecuting() {
        timeToRecalcPath = 0;
        oldWaterCost = tameable.getPathPriority(PathNodeType.WATER);
        tameable.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void resetTask() {
        owner = null;
        navigator.clearPath();
        tameable.setPathPriority(PathNodeType.WATER, oldWaterCost);
    }

    @Override
    public void updateTask() {
        tameable.getLookHelper().setLookPositionWithEntity(owner, 10.0F, tameable.getVerticalFaceSpeed());
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10;
            if (!navigator.tryMoveToEntityLiving(owner, followSpeed)) {
                if (tameable.getDistanceSq(owner) > 144.0D) {
                    int x = MathHelper.floor(owner.posX) - 2;
                    int z = MathHelper.floor(owner.posZ) - 2;
                    int y = MathHelper.floor(owner.getEntityBoundingBox().minY);
                    for (int i = 0; i <= 4; i++) {
                        for (int j = 0; j <= 4; j++) {
                            if ((i < 1 || j < 1 || i > 3 || j > 3) && isTeleportFriendlyBlock(x, z, y, i, j)) {
                                tameable.setPositionAndRotation(x + i + 0.5, y, z + j + 0.5, tameable.rotationYaw, tameable.rotationPitch);
                                navigator.clearPath();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isTeleportFriendlyBlock(int x, int z, int y, int dx, int dz) {
        BlockPos pos = new BlockPos(x + dx, y - 1, z + dz);
        IBlockState state = tameable.world.getBlockState(pos);
        return state.getBlockFaceShape(tameable.world, pos, EnumFacing.DOWN) == BlockFaceShape.SOLID &&
                state.canEntitySpawn(tameable) &&
                tameable.world.isAirBlock(pos.up()) &&
                tameable.world.isAirBlock(pos.up(2));
    }
}

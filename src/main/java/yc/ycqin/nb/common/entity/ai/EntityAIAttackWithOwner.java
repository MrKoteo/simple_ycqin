package yc.ycqin.nb.common.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import yc.ycqin.nb.util.EntityClassifier;

public class EntityAIAttackWithOwner extends EntityAITarget {
    private final EntityMob entity;
    private EntityLivingBase ownerTarget;

    public EntityAIAttackWithOwner(EntityMob entity) {
        super(entity, false, false);
        this.entity = entity;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!EntityClassifier.isTamed(entity)) return false;
        EntityPlayer owner = EntityClassifier.getOwner(entity, entity.world);
        if (owner == null) return false;

        if (entity.getAttackTarget() != null && entity.getAttackTarget().isEntityAlive()) return false;

        EntityLivingBase target = owner.getLastAttackedEntity();
        if (target != null && target != entity && target.isEntityAlive()) {
            this.ownerTarget = target;
            return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        entity.setAttackTarget(ownerTarget);
        super.startExecuting();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return ownerTarget != null && ownerTarget.isEntityAlive() && entity.getAttackTarget() == ownerTarget;
    }
}
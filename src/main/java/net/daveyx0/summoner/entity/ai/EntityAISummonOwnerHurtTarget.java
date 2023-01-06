package net.daveyx0.summoner.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import net.daveyx0.multimob.entity.ai.EntityAICustomTarget;
import net.daveyx0.multimob.util.EntityUtil;
import net.daveyx0.summoner.common.capabilities.CapabilitySummonableEntity;
import net.daveyx0.summoner.common.capabilities.ISummonableEntity;

public class EntityAISummonOwnerHurtTarget extends EntityAICustomTarget
{
    EntityLiving summon;
    EntityLivingBase attacker;
    private int timestamp;

    public EntityAISummonOwnerHurtTarget(EntityLiving entitySummoned)
    {
        super(entitySummoned, false);
        this.summon = entitySummoned;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.summon.hasCapability(CapabilitySummonableEntity.SUMMONABLE_ENTITY_CAPABILITY, null))
        {
            return false;
        }
        else
        {
            ISummonableEntity summonable = EntityUtil.getCapability(summon, CapabilitySummonableEntity.SUMMONABLE_ENTITY_CAPABILITY, null);
            EntityLivingBase entitylivingbase = summonable.getSummoner(summon);

            if (entitylivingbase == null)
            {
                return false;
            }
            else if ((entitylivingbase instanceof EntityLiving) && ((EntityLiving) entitylivingbase).getAttackTarget() != null)
            {
                this.attacker = ((EntityLiving) entitylivingbase).getAttackTarget();
                return this.isSuitableTarget(this.attacker, false);
            }
            else
            {
                this.attacker = entitylivingbase.getLastAttackedEntity();
                int i = entitylivingbase.getLastAttackedEntityTime();
                return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.attacker);
        ISummonableEntity summonable = EntityUtil.getCapability(summon, CapabilitySummonableEntity.SUMMONABLE_ENTITY_CAPABILITY, null);
        EntityLivingBase entitylivingbase = summonable.getSummoner(summon);

        if (entitylivingbase != null)
        {
            this.timestamp = entitylivingbase.getLastAttackedEntityTime();
        }

        super.startExecuting();
    }
}
package net.daveyx0.summoner.entity.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.daveyx0.multimob.core.MultiMob;
import net.daveyx0.multimob.util.EntityUtil;
import net.daveyx0.summoner.common.capabilities.CapabilitySummonableEntity;
import net.daveyx0.summoner.common.capabilities.ISummonableEntity;

public class EntityAISummonFollowOwner extends EntityAIBase
{
    private final EntityLiving summon;
    private final double followSpeed;
    private final PathNavigate petPathfinder;
    World world;
    float maxDist;
    float minDist;
    private EntityLivingBase owner;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public EntityAISummonFollowOwner(EntityLiving summon, double followSpeedIn, float minDistIn, float maxDistIn)
    {
        this.summon = summon;
        this.world = summon.world;
        this.followSpeed = followSpeedIn;
        this.petPathfinder = summon.getNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexBits(3);

        if (!(summon.getNavigator() instanceof PathNavigateGround) && !(summon.getNavigator() instanceof PathNavigateFlying))
        {
            MultiMob.LOGGER.error("Unsupported mob for FollowOwnerGoal: " + summon.getName());
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.summon != null && this.summon.hasCapability(CapabilitySummonableEntity.SUMMONABLE_ENTITY_CAPABILITY, null))
        {
            ISummonableEntity summonable = EntityUtil.getCapability(summon, CapabilitySummonableEntity.SUMMONABLE_ENTITY_CAPABILITY, null);
            EntityLivingBase entitylivingbase = summonable.getSummoner(summon);

            if (entitylivingbase == null)
            {
                return false;
            }
            else if (summon.getAttackTarget() != null)
            {
                return false;
            }
            else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator())
            {
                return false;
            }
            else if (this.summon.getDistanceSq(entitylivingbase) < (double) (this.minDist * this.minDist))
            {
                return false;
            }
            else
            {
                this.owner = entitylivingbase;
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return !this.petPathfinder.noPath() && this.summon.getDistanceSq(this.owner) > (double) (this.maxDist * this.maxDist);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.summon.getPathPriority(PathNodeType.WATER);
        this.summon.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.owner = null;
        this.petPathfinder.clearPath();
        this.summon.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        this.summon.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float) this.summon.getVerticalFaceSpeed());

        if (--this.timeToRecalcPath <= 0)
        {
            this.timeToRecalcPath = 10;

            if (!this.petPathfinder.tryMoveToEntityLiving(this.owner, this.followSpeed))
            {
                if (!this.summon.getLeashed() && !this.summon.isRiding())
                {
                    if (this.summon.getDistanceSq(this.owner) >= 144.0D && this.summon.getAttackTarget() == null)
                    {
                        int i = MathHelper.floor(this.owner.posX) - 2;
                        int j = MathHelper.floor(this.owner.posZ) - 2;
                        int k = MathHelper.floor(this.owner.getEntityBoundingBox().minY);

                        for (int l = 0; l <= 4; ++l)
                        {
                            for (int i1 = 0; i1 <= 4; ++i1)
                            {
                                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1))
                                {
                                    this.summon.setLocationAndAngles((float) (i + l) + 0.5F, k, (float) (j + i1) + 0.5F, this.summon.rotationYaw, this.summon.rotationPitch);
                                    this.petPathfinder.clearPath();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean isTeleportFriendlyBlock(int x, int p_192381_2_, int y, int p_192381_4_, int p_192381_5_)
    {
        BlockPos blockpos = new BlockPos(x + p_192381_4_, y - 1, p_192381_2_ + p_192381_5_);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.summon) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
    }
}
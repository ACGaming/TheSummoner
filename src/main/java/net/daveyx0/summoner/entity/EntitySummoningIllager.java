package net.daveyx0.summoner.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.daveyx0.multimob.util.EntityUtil;
import net.daveyx0.summoner.common.capabilities.CapabilitySummonableEntity;
import net.daveyx0.summoner.common.capabilities.ISummonableEntity;
import net.daveyx0.summoner.core.SummonGroup;
import net.daveyx0.summoner.core.SummonGroupRegistry;

public abstract class EntitySummoningIllager extends AbstractIllager
{
    private static final DataParameter<Byte> SPELL = EntityDataManager.createKey(EntitySummoningIllager.class, DataSerializers.BYTE);
    protected int spellTicks;
    private SummonGroup activeSpell = SummonGroupRegistry.getFromId(0);

    public EntitySummoningIllager(World p_i47506_1_)
    {
        super(p_i47506_1_);
    }

    public void onLivingUpdate()
    {
        super.onLivingUpdate();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.world.isRemote && this.isSpellcasting())
        {
            SummonGroup entityspellcasterillager$spelltype = this.getSummonType();
            double d0 = entityspellcasterillager$spelltype.particleSpeed[0];
            double d1 = entityspellcasterillager$spelltype.particleSpeed[1];
            double d2 = entityspellcasterillager$spelltype.particleSpeed[2];
            float f = this.renderYawOffset * 0.017453292F + MathHelper.cos((float) this.ticksExisted * 0.6662F) * 0.25F;
            float f1 = MathHelper.cos(f);
            float f2 = MathHelper.sin(f);
            this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + (double) f1 * 0.6D, this.posY + 1.8D, this.posZ + (double) f2 * 0.6D, d0, d1, d2);
            this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX - (double) f1 * 0.6D, this.posY + 1.8D, this.posZ - (double) f2 * 0.6D, d0, d1, d2);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("SpellTicks", this.spellTicks);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.spellTicks = compound.getInteger("SpellTicks");
    }

    protected void updateAITasks()
    {
        super.updateAITasks();

        if (this.spellTicks > 0)
        {
            --this.spellTicks;
            if (spellTicks <= 0)
            {
                this.setSummonType(SummonGroupRegistry.getFromId(0));
            }
        }
    }

    public boolean isSpellcasting()
    {
        if (this.world.isRemote)
        {
            return this.dataManager.get(SPELL) > 0;
        }
        else
        {
            return this.spellTicks > 0;
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(SPELL, (byte) 0);
    }

    @SideOnly(Side.CLIENT)
    public AbstractIllager.IllagerArmPose getArmPose()
    {
        return this.isSpellcasting() ? AbstractIllager.IllagerArmPose.SPELLCASTING : AbstractIllager.IllagerArmPose.CROSSED;
    }

    protected SummonGroup getSummonType()
    {
        return !this.world.isRemote ? this.activeSpell : SummonGroupRegistry.getFromId(this.dataManager.get(SPELL));
    }

    public void setSummonType(SummonGroup spellType)
    {
        this.activeSpell = spellType;
        this.dataManager.set(SPELL, (byte) spellType.id);
    }

    protected int getSpellTicks()
    {
        return this.spellTicks;
    }

    protected abstract SoundEvent getSpellSound();

    public class AICastingSpell extends EntityAIBase
    {
        public AICastingSpell()
        {
            this.setMutexBits(3);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            return EntitySummoningIllager.this.getSpellTicks() > 0;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            super.startExecuting();
            EntitySummoningIllager.this.navigator.clearPath();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask()
        {
            super.resetTask();
            EntitySummoningIllager.this.setSummonType(SummonGroupRegistry.getFromId(0));
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask()
        {
            if (EntitySummoningIllager.this.getAttackTarget() != null)
            {
                EntitySummoningIllager.this.getLookHelper().setLookPositionWithEntity(EntitySummoningIllager.this.getAttackTarget(), (float) EntitySummoningIllager.this.getHorizontalFaceSpeed(), (float) EntitySummoningIllager.this.getVerticalFaceSpeed());
            }
        }
    }

    public abstract class AIUseSummon extends EntityAIBase
    {
        protected int spellWarmup;
        protected int spellCooldown;
        protected int maxAmountOfSummons = 6;

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            if (EntitySummoningIllager.this.getAttackTarget() == null)
            {
                return false;
            }
            else if (EntitySummoningIllager.this.isSpellcasting())
            {
                return false;
            }
            else if (EntitySummoningIllager.this.ticksExisted >= this.spellCooldown)
            {
                int currentSummonAmount = 0;
                for (Entity entity : EntitySummoningIllager.this.world.loadedEntityList)
                {
                    if (CapabilitySummonableEntity.EventHandler.isEntitySuitableForSummon(entity))
                    {
                        ISummonableEntity summonable = EntityUtil.getCapability(entity, CapabilitySummonableEntity.SUMMONABLE_ENTITY_CAPABILITY, null);
                        if (summonable != null && summonable.isSummonedEntity() && summonable.getSummonerId().equals(EntitySummoningIllager.this.getUniqueID()))
                        {
                            currentSummonAmount++;
                            if (currentSummonAmount > maxAmountOfSummons)
                            {
                                this.spellCooldown = EntitySummoningIllager.this.ticksExisted + this.getCastingInterval();
                                return false;
                            }
                        }
                    }
                }

                return true;
            }
            else
            {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return EntitySummoningIllager.this.getAttackTarget() != null && this.spellWarmup > 0;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            this.spellWarmup = this.getCastWarmupTime();
            EntitySummoningIllager.this.spellTicks = this.getCastingTime();
            this.spellCooldown = EntitySummoningIllager.this.ticksExisted + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();

            if (soundevent != null)
            {
                EntitySummoningIllager.this.playSound(soundevent, 1.0F, 1.0F);
            }

            EntitySummoningIllager.this.setSummonType(this.getSummonGroup());
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask()
        {
            --this.spellWarmup;

            if (this.spellWarmup == 0)
            {
                this.castSpell();
                EntitySummoningIllager.this.playSound(EntitySummoningIllager.this.getSpellSound(), 1.0F, 1.0F);
            }
        }

        protected abstract void castSpell();

        protected int getCastWarmupTime()
        {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract SummonGroup getSummonGroup();
    }
}
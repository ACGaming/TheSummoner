package net.daveyx0.summoner.common.capabilities;

import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.daveyx0.multimob.util.EntityUtil;

/**
 * @author Daveyx0
 **/
public class SummonableEntityHandler implements ISummonableEntity
{
    protected UUID summonerID;
    protected boolean isSummoned;
    protected boolean isFollowing;
    protected int timeLimit = 1000;

    public SummonableEntityHandler()
    {
        summonerID = null;
        isSummoned = false;
        isFollowing = false;
    }

    public SummonableEntityHandler(UUID id)
    {
        summonerID = id;
        isSummoned = true;
        isFollowing = true;
    }

    public boolean isOwner(EntityLivingBase thisEntity, EntityLivingBase entityIn)
    {
        return entityIn == this.getSummoner(thisEntity);
    }

    @Override
    public boolean isSummonedEntity()
    {

        return isSummoned;
    }

    @Override
    public void setSummonedEntity(boolean set)
    {
        isSummoned = set;
    }

    @Override
    @Nullable
    public EntityLivingBase getSummoner(EntityLivingBase entityIn)
    {
        try
        {
            UUID uuid = this.getSummonerId();
            if (uuid != null)
            {
                EntityPlayer player = entityIn.world.getPlayerEntityByUUID(uuid);
                if (player != null) {return player;}
                else
                {
                    return EntityUtil.getLoadedEntityByUUID(uuid, entityIn.world);
                }
            }
            else {return null;}
        }
        catch (IllegalArgumentException var2)
        {
            return null;
        }
    }

    @Override
    public UUID getSummonerId()
    {

        return summonerID;
    }

    @Override
    public void setSummoner(UUID id)
    {
        summonerID = id;
    }

    @Override
    public boolean isFollowing()
    {

        return isFollowing;
    }

    @Override
    public void setFollowing(boolean set)
    {

        isFollowing = set;
    }

    @Override
    public int getTimeLimit()
    {

        return timeLimit;
    }

    @Override
    public void setTimeLimit(int ticks)
    {
        timeLimit = ticks;
    }

    private static class Factory implements Callable<ISummonableEntity>
    {
        @Override
        public ISummonableEntity call() throws Exception
        {
            return new SummonableEntityHandler();
        }
    }
}
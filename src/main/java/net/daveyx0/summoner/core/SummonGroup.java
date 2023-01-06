package net.daveyx0.summoner.core;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.daveyx0.summoner.item.ItemSummonerOrb;

public class SummonGroup
{
    private final List<NBTTagCompound> entityTags;
    public int id;
    public double[] particleSpeed;
    public int weight;

    public SummonGroup(int idIn, List<NBTTagCompound> tags, int weight)
    {
        this.id = idIn;
        this.particleSpeed = new double[] {0.7D, 0.7D, 0.8D};
        this.weight = weight;
        this.entityTags = tags;
    }

    public int getAmountToSummon()
    {
        if (entityTags == null || entityTags.isEmpty()) {return 0;}
        return entityTags.size();
    }

    public EntityLiving getEntityFromIndex(int index, World worldServerIn, EntityLivingBase owner)
    {
        if (index > entityTags.size()) {return null;}

        EntityLiving entity = null;

        if (entityTags.get(index) != null)
        {
            entity = ItemSummonerOrb.getEntityWithData(entityTags.get(index), worldServerIn, owner);
        }

        return entity;
    }
}
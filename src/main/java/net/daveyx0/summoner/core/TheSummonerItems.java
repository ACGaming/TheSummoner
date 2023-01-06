package net.daveyx0.summoner.core;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.daveyx0.multimob.client.MMItemModelManager;
import net.daveyx0.multimob.core.MMItemRegistry;
import net.daveyx0.summoner.item.ItemSummonerOrb;

public class TheSummonerItems extends MMItemRegistry
{
    public static final ItemSummonerOrb SUMMONER_ORB = new ItemSummonerOrb("summoner_orb", 0);
    public static final ItemSummonerOrb ENCHANCED_SUMMONER_ORB = new ItemSummonerOrb("enhanced_summoner_orb", 1);

    public static void registerItemColors()
    {
        Item[] coloredItems = new Item[] {SUMMONER_ORB, ENCHANCED_SUMMONER_ORB};
        MMItemModelManager.INSTANCE.registerItemColors(coloredItems);
    }

    @Mod.EventBusSubscriber(modid = TheSummonerReference.MODID)
    public static class RegistrationHandler
    {

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event)
        {
            Item[] items = {SUMMONER_ORB, ENCHANCED_SUMMONER_ORB};

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final Item item : items)
            {
                registry.register(item);
                ITEMS.add(item);
            }
        }
    }
}
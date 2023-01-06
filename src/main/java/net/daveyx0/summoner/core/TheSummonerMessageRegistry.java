package net.daveyx0.summoner.core;

import net.minecraftforge.fml.relauncher.Side;

import net.daveyx0.multimob.message.MMMessageRegistry;
import net.daveyx0.summoner.message.MessageSummonable;

public class TheSummonerMessageRegistry extends MMMessageRegistry
{
    public static void registerMessages()
    {
        registerMessage(MessageSummonable.Handler.class, MessageSummonable.class, Side.CLIENT);
    }
}
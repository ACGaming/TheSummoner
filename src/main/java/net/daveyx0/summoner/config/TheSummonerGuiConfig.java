package net.daveyx0.summoner.config;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import net.daveyx0.summoner.core.TheSummonerReference;

public class TheSummonerGuiConfig extends GuiConfig
{
    private static List<IConfigElement> getConfigElements()
    {
        return TheSummonerConfig.config.getCategoryNames().stream().map(categoryName -> new ConfigElement(TheSummonerConfig.config.getCategory(categoryName).setLanguageKey("thesummoner.config." + categoryName))).collect(Collectors.toList());
    }

    public TheSummonerGuiConfig(GuiScreen parentScreen)
    {
        super(parentScreen, getConfigElements(), TheSummonerReference.MODID, false, false, "thesummoner.config.title");
    }
}
package net.daveyx0.summoner.modint;

import jeresources.api.conditionals.LightLevel;
import net.daveyx0.multimob.modint.JustEnoughResourcesIntegration;
import net.daveyx0.summoner.core.TheSummonerLootTables;
import net.daveyx0.summoner.entity.EntitySummoner;

public class TheSummonerJERIntegration extends JustEnoughResourcesIntegration {

	@Override
	public void init() {		
				super.init();
				//Loottable mob loot
				jerAPI.getMobRegistry().register(new EntitySummoner(world), LightLevel.hostile, TheSummonerLootTables.ENTITIES_SUMMONER);
				jerAPI.getMobRegistry().register(new EntitySummoner(world).setBoss(true), LightLevel.hostile, TheSummonerLootTables.ENTITIES_SUMMONER_BOSS);
	}
}
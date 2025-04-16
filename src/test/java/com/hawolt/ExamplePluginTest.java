package com.hawolt;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GuardianOfTheRiftOptimizerPlugin.class);
		RuneLite.main(args);
	}
}
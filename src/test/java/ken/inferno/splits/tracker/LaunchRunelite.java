package ken.inferno.splits.tracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class LaunchRunelite
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(InfernoSplitsTrackerPlugin.class);
		RuneLite.main(args);
	}
}

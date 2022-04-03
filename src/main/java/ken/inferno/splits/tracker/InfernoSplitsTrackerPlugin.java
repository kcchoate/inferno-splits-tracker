package ken.inferno.splits.tracker;

import javax.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
        name = "Inferno Splits Tracker",
        description = "Offers different tracking capabilities for inferno wave splits. Requires the \"Inferno Split Timer\" plugin.",
        tags = {"inferno", "splits", "tracker", "logger", "discord", "webhook"}
)
public class InfernoSplitsTrackerPlugin extends Plugin{

    @Inject private MessageProcessorCollection processorCollection;

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        processorCollection.handleMessage(event);
    }

    @Provides
    InfernoSplitsTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(InfernoSplitsTrackerConfig.class);
    }

    @Override
    protected void shutDown() throws Exception {
        processorCollection.reset();
    }
}


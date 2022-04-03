package ken.inferno.splits.tracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(InfernoSplitsTrackerConfig.GROUP)
public interface InfernoSplitsTrackerConfig extends Config
{
    String GROUP = "ken.infernosplitstracker";

    @ConfigItem(
            keyName = "shouldWriteToFile",
            name = "Write to File?",
            description = "Enable to have your splits written to a log file"
    )
    default boolean getShouldWriteToFile()
    {
        return true;
    }

    @ConfigItem(
            keyName = "shouldUploadToDiscord",
            name = "Upload to Discord?",
            description = "Enable to upload your splits to a discord channel using webhooks"
    )
    default boolean getShouldUploadToDiscord()
    {
        return false;
    }

    @ConfigItem(
            keyName = "discordWebhookUrl",
            name = "Webhook URL",
            description = "The Discord Webhook URL to send messages to"
    )
    String getDiscordWebhookUrl();
}

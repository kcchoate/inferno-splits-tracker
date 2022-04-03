package ken.inferno.splits.tracker;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@Slf4j
public class FileLoggerMessageProcessor extends BaseMessageProcessor {
    @Inject private InfernoSplitsTrackerConfig config;
    @Inject private Client client;

    @Override
    public void onCompletionMessage(ChatMessage message, InfernoState state) {
        writeSplitsToFile(state);
    }

    @Override
    public void onDefeatedMessage(ChatMessage message, InfernoState state) {
        writeSplitsToFile(state);
    }

    private void writeSplitsToFile(InfernoState state) {
        if (!config.getShouldWriteToFile()) {
            return;
        }

        File dir = new File(RUNELITE_DIR, "infernoSplitsTracker/" + client.getLocalPlayer().getName());
        dir.mkdirs();

        String fileName = getFileName(state);
        try (FileWriter fw = new FileWriter(new File(dir, fileName)))
        {
            fw.write(state.getSplitsCsv());
        }
        catch (IOException ex)
        {
            log.debug("Error writing file: {}", ex.getMessage());
        }
    }

    private String getFileName(InfernoState state) {
        if (state.getKillCount() == 0) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm");
            LocalDateTime now = LocalDateTime.now();
            return  "failed_run_on_wave_" + state.getCurrentWave() + "_at_" + dtf.format(now) + ".csv";
        }
        return "kc_" + state.getKillCount() + "_in_" + state.getDuration() + ".csv";
    }
}

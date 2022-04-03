package ken.inferno.splits.tracker;

import net.runelite.api.events.ChatMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class InfernoStateTests {

    private InfernoState sut = new InfernoState();

    @Test
    public void GivenSuccessfulKill_CalculatesSplitsCorrectly()
    {
        sendStart();
        sendWave(2);
        sendSplit("01:01");
        sendWaves(3, 4);
        sendSplit("02:02");
        sendSuccessfulKill(999, "10:01");

        assertEquals("Wave,Split\n2,01:01\n4,02:02\nend,10:01\n", sut.getSplitsCsv());
    }

    @Test
    public void GivenOneDecimalInDuration_ProducesDecimalCorrectly()
    {
        sendStart();
        sendWave(2);
        sendSplit("01:01");
        sendWaves(3, 4);
        sendSplit("02:02.1");
        sendSuccessfulKill(231, "10:01");

        assertEquals("Wave,Split\n2,01:01\n4,02:02.1\nend,10:01\n", sut.getSplitsCsv());
    }

    @Test
    public void GivenTwoDecimalsInDuration_ProducesDecimalCorrectly()
    {
        sendStart();
        sendWave(2);
        sendSplit("01:01");
        sendWaves(3, 4);
        sendSplit("02:02.10");
        sendSuccessfulKill(231, "10:01");

        assertEquals("Wave,Split\n2,01:01\n4,02:02.10\nend,10:01\n", sut.getSplitsCsv());
    }

    @Test
    public void GivenPlayerDiesAfterWaveSplit_CalculatesSplitsCorrectly()
    {
        sendStart();
        sendWave(2);
        sendSplit("01:01");
        sendWaves(3, 4);
        sendSplit("02:02");
        sendDefeated("10:01");

        assertEquals("Wave,Split\n2,01:01\n4,02:02\nend,10:01\n", sut.getSplitsCsv());
    }

    private void sendStart() {
        ChatMessage message = new ChatMessage();
        message.setMessage("Wave: 1");
        sut.processMessage(message);
    }

    private void sendWave(int waveNumber) {
        ChatMessage message = new ChatMessage();
        message.setMessage("Wave: " + waveNumber);
        sut.processMessage(message);
    }

    private void sendWaves(int startWaveNumber, int endWaveNumber) {
        for (int i = startWaveNumber; i <= endWaveNumber; i++) {
            sendWave(i);
        }
    }

    private void sendSplit(String split) {
        ChatMessage message = new ChatMessage();
        message.setMessage("Wave Split: " + split);
        sut.processMessage(message);
    }

    private void sendKillCount(int kc) {
        ChatMessage message = new ChatMessage();
        message.setMessage("Your TzKal-Zuk kill count is: " + kc);
        sut.processMessage(message);
    }

    private void sendDuration(String split) {
        ChatMessage message = new ChatMessage();
        message.setMessage("Duration: " + split);
        sut.processMessage(message);
    }

    private void sendSuccessfulKill(int kc, String split) {
        sendKillCount(kc);
        sendDuration(split);
    }

    private void sendDefeated(String split) {
        ChatMessage message = new ChatMessage();
        message.setMessage("You have been defeated");
        sut.processMessage(message);
        sendDuration(split);
    }
}

package ken.inferno.splits.tracker;

import net.runelite.api.events.ChatMessage;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfernoState {
    private static final String splitPattern = "\\d+:\\d+(\\.\\d{1,2})?";
    private final Pattern wavePattern = Pattern.compile("(?<=wave: )\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern killCountPattern = Pattern.compile("(?<=your tzkal-zuk kill count is: )\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern waveSplitPattern = Pattern.compile("(?<=wave split: )" + splitPattern, Pattern.CASE_INSENSITIVE);
    private final Pattern durationPattern = Pattern.compile("(?<=duration: )" + splitPattern, Pattern.CASE_INSENSITIVE);
    private final Pattern pbPattern = Pattern.compile("(?<=personal best: )" + splitPattern, Pattern.CASE_INSENSITIVE);

    private int currentWave;
    private int killCount;
    private String duration;
    private String personalBest;
    SortedMap<Integer, String> waveSplits = new TreeMap<>();

    public int getCurrentWave() {
        return currentWave;
    }

    private void setCurrentWave(int newValue) {
        currentWave = newValue;
    }

    public int getKillCount() {
        return killCount;
    }

    private void setKillCount(int newValue) {
        killCount = newValue;
    }

    public String getDuration() {
        return duration;
    }

    private void setDuration(String newValue) {
        duration = newValue;
    }

    public String getPersonalBest() {
        return personalBest;
    }

    private void setPersonalBest(String newValue) {
        personalBest = newValue;
    }

    public String getSplitsCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wave,Split");
        sb.append('\n');

        for (Integer waveNumber : waveSplits.keySet()) {
            sb.append(waveNumber);
            sb.append(',');
            sb.append(waveSplits.get(waveNumber));
            sb.append('\n');
        }

        sb.append("end,");
        sb.append(duration);
        sb.append('\n');

        return sb.toString();
    }

    private void addSplit(String split) {
        if (!waveSplits.containsKey(currentWave)) {
            waveSplits.put(currentWave, split);
        }
    }

    public MessageType processMessage(ChatMessage message) {
        final MessageType messageType = getMessageType(message);

        switch (messageType) {
            case FirstWave:
            case GenericWave:
                handleGenericWaveMessage(message);
                break;
            case WaveSplit:
                handleWaveSplitMessage(message);
                break;
            case Kc:
                handleKcMessage(message);
                break;
            case Completion:
                handleCompletionMessage(message);
                break;
        }

        return messageType;
    }

    private void handleGenericWaveMessage(ChatMessage message) {
        Matcher matcher = wavePattern.matcher(message.getMessage());
        if (matcher.find()) {
            setCurrentWave(Integer.parseInt(matcher.group()));
        }
    }

    private void handleWaveSplitMessage(ChatMessage message) {
        Matcher matcher = waveSplitPattern.matcher(message.getMessage());
        if (matcher.find()) {
            addSplit(matcher.group());
        }
    }

    private void handleKcMessage(ChatMessage message) {
        Matcher matcher = killCountPattern.matcher(message.getMessage());
        if (matcher.find()) {
            setKillCount(Integer.parseInt(matcher.group()));
        }
    }

    private void handleCompletionMessage(ChatMessage message) {
        final String text = message.getMessage();
        Matcher matcher = durationPattern.matcher(text);
        if (!matcher.find()) {
            return;
        }

        setDuration(matcher.group());
        if (text.toLowerCase(Locale.ROOT).contains("new personal best")) {
            setPersonalBest(getDuration());
        }
        else {
            Matcher pbMatcher = pbPattern.matcher(text);
            if (pbMatcher.find()) {
                setPersonalBest(pbMatcher.group());
            }
        }
    }

    public void reset() {
        waveSplits.clear();
        setPersonalBest("");
        setDuration("");
        setCurrentWave(0);
        setKillCount(0);
    }

    private MessageType getMessageType(ChatMessage message) {
        String text = message.getMessage().toLowerCase(Locale.ROOT);
        if (text.contains("wave: 1")) {
            return MessageType.FirstWave;
        }
        if (text.contains("wave:")) {
            return MessageType.GenericWave;
        }
        if (text.contains("wave split:")) {
            return MessageType.WaveSplit;
        }
        if (text.contains("your tzkal-zuk kill count is:")) {
            return MessageType.Kc;
        }
        if (text.contains("duration:")) {
            return MessageType.Completion;
        }
        if (text.contains("you have been defeated")) {
            return MessageType.Defeated;
        }
        return MessageType.Unknown;
    }

}

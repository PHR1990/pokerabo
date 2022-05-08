package nl.rabobank.pirates.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class StatMultiplier {
    private Stat stat;
    private int stageModification;

    public boolean add(int stage) {
        if (stage > 0 && stageModification == 6) {
            return false;
        }
        if (stage < 0 && stageModification == -6) {
            return false;
        }
        stageModification+=stage;
        return true;

    }
}

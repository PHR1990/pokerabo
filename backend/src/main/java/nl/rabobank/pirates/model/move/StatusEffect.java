package nl.rabobank.pirates.model.move;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StatusEffect {

    private Condition condition;
    private int chance;

    public enum Condition {
        NONE, BURN, POISON, BADLY_POISONED, SLEEP, PARALYZED, FROZEN, CONFUSED, SEEDED;
    }

}

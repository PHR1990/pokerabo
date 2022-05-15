package nl.rabobank.pirates.model.move;

import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static nl.rabobank.pirates.model.move.StatusEffect.Condition.*;

@Builder
@Getter
public class StatusEffect {

    public static final List<Condition> PRIMARY_CONDITIONS = Arrays.asList(BURN, POISON, BADLY_POISONED, SLEEP, PARALYZED, FROZEN);
    public static final List<Condition> SECONDARY_CONDITIONS = Arrays.asList(CONFUSED, SEEDED);

    private Condition condition;
    private int chance;

    public enum Condition {
        NONE, BURN, POISON, BADLY_POISONED, SLEEP, PARALYZED, FROZEN, CONFUSED, SEEDED;
    }

}

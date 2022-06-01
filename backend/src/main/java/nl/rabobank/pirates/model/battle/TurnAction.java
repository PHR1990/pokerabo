package nl.rabobank.pirates.model.battle;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import nl.rabobank.pirates.model.move.StatusEffect;

@Builder(toBuilder = true, access = AccessLevel.PACKAGE)
@Getter
@ToString
public class TurnAction {
    private TurnActionType type;
    private StatusEffect.Condition statusEffectCondition;
    private int damage;
    private String text;
    private Subject subject;
    public enum Subject {
        NONE, OWN, ENEMY
    }

}




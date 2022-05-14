package nl.rabobank.pirates.model.battle;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import nl.rabobank.pirates.model.move.StatusEffect;

@Builder(access = AccessLevel.PACKAGE)
@Getter
public class TurnAction {
    // TODO add length?
    private TurnActionType type;
    private StatusEffect statusEffect;
    private int damage;
    private String text;
    private Subject subject;
    public enum Subject {
        NONE, OWN, ENEMY
    }

}




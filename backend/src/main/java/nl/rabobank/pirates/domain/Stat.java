package nl.rabobank.pirates.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Stat {
    ATTACK("attack"),
    DEFENSE("defense"),
    SPECIAL_ATTACK("special-attack"),
    SPECIAL_DEFENSE("special-defense"),
    SPEED("speed"),
    HP("hp");

    private final String label;

    public static Stat valueOfLabel(String label) {
        for (Stat e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }

}

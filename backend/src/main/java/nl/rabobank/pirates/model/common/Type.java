package nl.rabobank.pirates.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Type {
    FIRE("fire"),
    WATER("water"),
    GRASS("grass"),
    ELECTRIC("electric"),
    PSYCHIC("psychic"),
    NORMAL("normal"),
    POISON("poison"),
    GROUND("ground"),
    ROCK("rock"),
    ICE("ice"),
    GHOST("ghost"),
    DRAGON("dragon");

    private final String label;

    public static Type valueOfLabel(String label) {
        for (Type e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return NORMAL;
    }
}

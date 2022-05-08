package nl.rabobank.pirates.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TurnAction {
    private TurnActionType type;
    private int damage;
    private String text;

}


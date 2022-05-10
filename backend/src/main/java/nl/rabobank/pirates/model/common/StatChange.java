package nl.rabobank.pirates.model.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StatChange {

    private Stat stat;
    private int changeAmount;
}

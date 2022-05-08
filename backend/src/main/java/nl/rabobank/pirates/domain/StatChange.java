package nl.rabobank.pirates.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StatChange {

    private Stat stat;
    private int changeAmount;
}

package nl.rabobank.pirates.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatAmount {

    private Stat stat;
    private int amount;

}



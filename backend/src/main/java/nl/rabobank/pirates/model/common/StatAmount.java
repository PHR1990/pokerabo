package nl.rabobank.pirates.model.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatAmount {

    private Stat stat;
    private int amount;

}



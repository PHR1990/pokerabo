package nl.rabobank.pirates.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TurnInformation {

    private List<TurnAction> actions;
}

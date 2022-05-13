package nl.rabobank.pirates.model.move;

import lombok.Builder;
import lombok.Getter;
import nl.rabobank.pirates.model.common.StatChange;
import nl.rabobank.pirates.model.common.Type;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class Move {
    private String name;
    private int accuracy;
    private int priority;
    private Integer power;
    private Target target;
    private Type type;
    private DamageClass damageClass;
    private HitTimes hitTimes;
    private List<StatChange> statChanges;
}



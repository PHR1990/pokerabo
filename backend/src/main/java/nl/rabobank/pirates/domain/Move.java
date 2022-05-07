package nl.rabobank.pirates.domain;

import lombok.Builder;
import lombok.Getter;
import nl.rabobank.pirates.client.common.Type;

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
    private Stat effectedStat;
}



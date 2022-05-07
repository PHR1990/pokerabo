package nl.rabobank.pirates.client.move;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.rabobank.pirates.client.pokemon.TypeDto;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoveDto {

    private String name;
    private int accuracy;
    private int power;
    private int priority;

    private TargetDto target;
    private List<StatChangeDto> statChanges;
    private DamageClassDto damageClass;
    private TypeDto type;
}

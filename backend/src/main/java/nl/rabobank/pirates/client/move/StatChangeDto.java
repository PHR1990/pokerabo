package nl.rabobank.pirates.client.move;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.rabobank.pirates.client.pokemon.StatDto;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StatChangeDto {
    private int change;
    private StatDto stat;
}

package nl.rabobank.pirates.client.pokemon;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PokemonDto {

    private String name;
    private List<StatDtoWrapper> stats;
    private SpriteDto sprites;
    private List<TypeDtoWrapper> types;
    private List<ThinMoveWrapperDto> moves;
}


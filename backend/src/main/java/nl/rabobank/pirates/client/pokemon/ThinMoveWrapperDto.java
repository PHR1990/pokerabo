package nl.rabobank.pirates.client.pokemon;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ThinMoveWrapperDto {
    private List<VersionGroupDetailsDto> versionGroupDetails;
    private ThinMoveDto move;
}

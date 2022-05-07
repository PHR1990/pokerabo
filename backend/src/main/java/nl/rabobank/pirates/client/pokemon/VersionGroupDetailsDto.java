package nl.rabobank.pirates.client.pokemon;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VersionGroupDetailsDto {
    private int levelLearnedAt;
    private MoveLearnMethodDto moveLearnMethod;
    private VersionGroupDto versionGroup;
}

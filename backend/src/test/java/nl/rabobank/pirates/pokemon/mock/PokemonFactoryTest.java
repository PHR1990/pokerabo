package nl.rabobank.pirates.pokemon.mock;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.pirates.client.move.MoveDto;
import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatAmount;
import nl.rabobank.pirates.model.common.Type;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PokemonFactoryTest {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static PokemonDto makeCharmanderDto() throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(new File("src/test/resources/pokemon/charmander.json"), PokemonDto.class);
    }

    public static PokemonDto makeSquirtleDto() throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(new File("src/test/resources/pokemon/squirtle.json"), PokemonDto.class);
    }

    public static Pokemon makeLevel5Charmander() {

        List<StatAmount> stats = Arrays.asList(
                StatAmount.builder().stat(Stat.HP).amount(8).build(),
                StatAmount.builder().stat(Stat.ATTACK).amount(10).build(),
                StatAmount.builder().stat(Stat.DEFENSE).amount(9).build(),
                StatAmount.builder().stat(Stat.SPECIAL_ATTACK).amount(11).build(),
                StatAmount.builder().stat(Stat.SPECIAL_DEFENSE).amount(10).build(),
                StatAmount.builder().stat(Stat.SPEED).amount(11).build(),
                StatAmount.builder().stat(Stat.ACCURACY).amount(100).build(),
                StatAmount.builder().stat(Stat.EVASION).amount(100).build()
        );

        return Pokemon.builder()
                .name("Charmander")
                .level(5)
                .type(Type.FIRE)
                .maxHp(18)
                .currentHp(18)
                .stats(stats)
            .build();
    }

    public static Pokemon makeLevel5Squirtle() {

        List<StatAmount> stats = Arrays.asList(
                StatAmount.builder().stat(Stat.HP).amount(9).build(),
                StatAmount.builder().stat(Stat.ATTACK).amount(9).build(),
                StatAmount.builder().stat(Stat.DEFENSE).amount(11).build(),
                StatAmount.builder().stat(Stat.SPECIAL_ATTACK).amount(10).build(),
                StatAmount.builder().stat(Stat.SPECIAL_DEFENSE).amount(11).build(),
                StatAmount.builder().stat(Stat.SPEED).amount(9).build(),
                StatAmount.builder().stat(Stat.ACCURACY).amount(100).build(),
                StatAmount.builder().stat(Stat.EVASION).amount(100).build()
        );

        return Pokemon.builder()
                .name("Squirtle")
                .level(5)
                .type(Type.WATER)
                .maxHp(19)
                .currentHp(19)
                .stats(stats)
                .build();
    }
}

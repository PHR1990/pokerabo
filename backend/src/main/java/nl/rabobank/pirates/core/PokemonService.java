package nl.rabobank.pirates.core;

import lombok.Getter;
import nl.rabobank.pirates.client.common.Type;
import nl.rabobank.pirates.client.move.MoveDto;
import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.client.pokemon.StatDtoWrapper;
import nl.rabobank.pirates.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PokemonService {

    private Map<String, PokemonDto> pokemonStorage = new ConcurrentHashMap<>();

    @Getter
    private AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private PokemonApiRestClient pokemonApiRestClient;

    @Autowired
    private MoveService moveService;

    public Pokemon getPokemonByName(final String pokemonName, int level) {
        if (level == 0) level = 5;
        PokemonDto pokemonDto;
        if (pokemonStorage.containsKey(pokemonName)) {
            pokemonDto = pokemonStorage.get(pokemonName);
        } else {
            pokemonDto = pokemonApiRestClient.getPokemonByName(pokemonName);
        }
        pokemonStorage.put(pokemonName, pokemonDto);

        return Pokemon.builder()
                .name(pokemonDto.getName().toUpperCase())
                .backSpriteUrl(pokemonDto.getSprites().getBackDefault())
                .frontSpriteUrl(pokemonDto.getSprites().getFrontDefault())
                .maxHp(calculateMaxHp(level, pokemonDto))
                .currentHp(calculateMaxHp(level, pokemonDto))
                .stats(convertAndCalculateToStatsAmount(pokemonDto.getStats(), level))
                .moves(moveService.getFourRandomMoves(pokemonDto, level))
                .level(level)
                .build();
    }

    private List<StatAmount> convertAndCalculateToStatsAmount(List<StatDtoWrapper> statDtoWrapperList, int level) {
        List<StatAmount> statAmountList = new ArrayList<>();

        for (StatDtoWrapper statDtoWrapper : statDtoWrapperList) {
            int statAmount = calculateStat(level, statDtoWrapper.getBaseStat());
            statAmountList.add(
                    StatAmount.builder()
                            .amount(statAmount)
                            .stat(Stat.valueOfLabel(statDtoWrapper.getStat().getName()))
                            .build()
            );
        }

        return statAmountList;
    }



    /**
     * Stats Formula for gen3+
     * Stats = (floor(0.01 x (2 x Base) x Level) + 5)
     */
    private int calculateStat(int level, int baseStat) {
        return Math.round((float)Math.floor(
                0.01 * (2 * baseStat * level) + level + 5
        ));
    }

    /**
     * HP we will be using:
     * HP = floor(0.01 x (2 x Base) x Level) + Level + 10
     */
    private int calculateMaxHp(int level, PokemonDto pokemonDto) {

        for (StatDtoWrapper statDtoWrapper : pokemonDto.getStats()) {
            if ("hp".equals(statDtoWrapper.getStat().getName())) {
                return Math.round((float)Math.floor(
                        0.01 * (2 * Integer.valueOf(statDtoWrapper.getBaseStat()) * level) + level + 10
                ));
            }
        }

        throw new RuntimeException("HP BASE STAT WASNT FOUND");
    }

    private int getRandomValue(int rangeStart, int rangeEnd) {
        final Random random = new Random();
        return random.ints(rangeStart, rangeEnd).findFirst().getAsInt();
    }

}

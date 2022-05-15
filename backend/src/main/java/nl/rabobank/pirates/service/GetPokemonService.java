package nl.rabobank.pirates.service;

import lombok.Getter;
import nl.rabobank.pirates.client.PokemonApiRestClient;
import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.client.pokemon.StatDtoWrapper;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatAmount;
import nl.rabobank.pirates.model.common.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GetPokemonService {

    private final Map<String, PokemonDto> pokemonStorage = new ConcurrentHashMap<>();

    @Getter
    private final AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private PokemonApiRestClient pokemonApiRestClient;

    @Autowired
    private GetMoveService moveService;

    @Autowired
    private CalculationService calculationService;

    public Pokemon getPokemonByName(final String pokemonName, int level) {
        if (level == 0) level = 5;
        PokemonDto pokemonDto;
        if (pokemonStorage.containsKey(pokemonName)) {
            pokemonDto = pokemonStorage.get(pokemonName);
        } else {
            pokemonDto = pokemonApiRestClient.getPokemonByName(pokemonName);
        }
        pokemonStorage.put(pokemonName, pokemonDto);

        final int maxHp = calculationService.calculateMaxHp(level, pokemonDto);

        return Pokemon.builder()
                .name(pokemonDto.getName().toUpperCase())
                .backSpriteUrl(pokemonDto.getSprites().getBackDefault())
                .frontSpriteUrl(pokemonDto.getSprites().getFrontDefault())
                .maxHp(maxHp)
                .currentHp(maxHp)
                .stats(convertAndCalculateToStatsAmount(pokemonDto.getStats(), level))
                .moves(moveService.getFourRandomMoves(pokemonDto, level))
                .type(Type.valueOfLabel(pokemonDto.getTypes().get(0).getType().getName()))
                .level(level)
            .build();
    }

    private List<StatAmount> convertAndCalculateToStatsAmount(List<StatDtoWrapper> statDtoWrapperList, int level) {
        final List<StatAmount> statAmountList = new ArrayList<>();

        for (StatDtoWrapper statDtoWrapper : statDtoWrapperList) {
            int statAmount = calculationService.calculateStat(level, statDtoWrapper.getBaseStat());
            statAmountList.add(
                    StatAmount.builder()
                            .amount(statAmount)
                            .stat(Stat.valueOfLabel(statDtoWrapper.getStat().getName()))
                            .build()
            );
        }

        statAmountList.add(StatAmount.builder().stat(Stat.ACCURACY).amount(100).build());
        statAmountList.add(StatAmount.builder().stat(Stat.EVASION).amount(100).build());

        return statAmountList;
    }

}

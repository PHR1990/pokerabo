package nl.rabobank.pirates.service;

import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.client.pokemon.StatDtoWrapper;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.move.Move;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CalculationService {

    final static Random random = new Random();

    public int getRandomValue(int rangeStart, int rangeEnd) {

        return random.ints(rangeStart, rangeEnd).findFirst().getAsInt();
    }

    public boolean calculateAccuracyAndRollIfMoveHits(int pokemonAccuracy, int moveAccuracy) {
        int hitChance =  pokemonAccuracy * (moveAccuracy/100);

        int rangeRoll = getRandomValue(0, 101);

        return hitChance >= rangeRoll;
    }

    public int calculateDamage(int level, int movePower, int attack, int defense) {
        if (movePower == 0) return 0;

        int damage = Math.round(
                ((((level * 2)/5 + 2) * movePower * attack/defense)/50) + 2);
        return damage;
    }

    public int calculateStat(int level, int baseStat) {
        return Math.round((float)Math.floor(
                0.01 * (2 * baseStat * level) + 5
        ));
    }

    public int calculateMaxHp(int level, PokemonDto pokemonDto) {

        for (StatDtoWrapper statDtoWrapper : pokemonDto.getStats()) {
            if ("hp".equals(statDtoWrapper.getStat().getName())) {
                return Math.round((float)Math.floor(
                        0.01 * (2 * Integer.valueOf(statDtoWrapper.getBaseStat()) * level) + level + 10
                ));
            }
        }

        throw new RuntimeException("HP BASE STAT WASNT FOUND");
    }
}

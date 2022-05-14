package nl.rabobank.pirates.service;

import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.client.pokemon.StatDtoWrapper;
import nl.rabobank.pirates.model.move.HitTimes;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CalculationService {

    final static Random random = new Random();

    public int getRandomValue(int rangeStart, int rangeEnd) {

        return random.ints(rangeStart, rangeEnd).findFirst().getAsInt();
    }

    public boolean calculateAccuracyAndRollIfMoveHits(int pokemonAccuracy, int moveAccuracy) {
        int hitChance =  Math.round((float)pokemonAccuracy * ((float)moveAccuracy/100f));

        int rangeRoll = getRandomValue(0, 101);

        return hitChance >= rangeRoll;
    }

    public int calculateDamage(int level, int movePower, int attack, int defense) {
        if (movePower == 0) return 0;

        return Math.round(
                ((((level * 2)/5 + 2) * movePower * attack/defense)/50) + 2);
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
                        0.01 * (2 * statDtoWrapper.getBaseStat() * level) + level + 10
                ));
            }
        }

        throw new RuntimeException("HP BASE STAT WASN'T FOUND");
    }

    public int calculateNumberOfHitTimes(HitTimes hitTimes) {
        if (hitTimes.equals(HitTimes.TWO_TO_FIVE)) {
            int diceRoll = this.getRandomValue(0, 101);

            if (diceRoll < 37) return 2;
            if (diceRoll > 37 && diceRoll < 75) return 3;
            if (diceRoll > 75 && diceRoll < 87) return 4;
            return 5;
        }

        if (hitTimes.equals(HitTimes.TWICE)) return 2;

        return 1;
    }
}

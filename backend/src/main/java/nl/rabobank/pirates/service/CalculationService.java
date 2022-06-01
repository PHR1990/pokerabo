package nl.rabobank.pirates.service;

import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.move.HitTimes;
import nl.rabobank.pirates.model.move.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculationService {

    @Autowired
    private RollService rollService;

    public boolean isRollSuccessful(int chance) {
        return rollService.isRollSuccessful(chance);
    }

    public boolean calculateAccuracyAndRollIfMoveHits(int pokemonAccuracy, int moveAccuracy) {
        int hitChance =  Math.round((float)pokemonAccuracy * ((float)moveAccuracy/100f));

        return rollService.isRollSuccessful(hitChance);
    }

    public int calculateSpecialDamage(final Pokemon attackingPokemon, final Pokemon defendingPokemon, final Move move) {
        return calculateDamage(
                attackingPokemon.getLevel(),
                move.getPower(),
                attackingPokemon.getStatAmount(Stat.SPECIAL_ATTACK),
                defendingPokemon.getStatAmount(Stat.SPECIAL_DEFENSE),
                attackingPokemon.getType().equals(move.getType())
        );
    }

    public int calculatePhysicalDamage(final Pokemon attackingPokemon, final Pokemon defendingPokemon, final Move move) {
        return calculateDamage(
                attackingPokemon.getLevel(),
                move.getPower(),
                attackingPokemon.getStatAmount(Stat.ATTACK),
                defendingPokemon.getStatAmount(Stat.DEFENSE),
                attackingPokemon.getType().equals(move.getType())
        );
    }

    int calculateDamage(int level, int movePower, int attack, int defense, boolean applySTAB) {
        if (movePower == 0) return 0;
        
        //STAB (an abbreviated form of Same-type attack bonus) amplifies a move's power when a Pokémon's type matches the move's type. This boost in power is increased by 50%
    	//Source: https://pokemon.fandom.com/wiki/STAB
        if (applySTAB) movePower *= 1.5;

        return Math.round(
                ((((level * 2)/5 + 2) * movePower * attack/defense)/50) + 2);
    }

    public int calculateStat(int level, int baseStat) {
        return Math.round((float)Math.floor(
                0.01 * (2 * baseStat * level) + 5
        ));
    }

    public int calculateMaxHp(int level, final int baseHpStat) {

        return Math.round((float)Math.floor(
                0.01 * (2 * baseHpStat * level) + level + 10
        ));
    }

    public int calculateNumberOfHitTimes(HitTimes hitTimes) {
        if (hitTimes.equals(HitTimes.TWO_TO_FIVE)) {
            int diceRoll = rollService.getRandomValue(0, 101);

            if (diceRoll < 37) return 2;
            if (diceRoll > 37 && diceRoll < 75) return 3;
            if (diceRoll > 75 && diceRoll < 87) return 4;
            return 5;
        }

        if (hitTimes.equals(HitTimes.TWICE)) return 2;

        return 1;
    }

    public int randomSleepOrConfusedTurns() {
        return rollService.getRandomValue(2,6);
    }
}

package nl.rabobank.pirates.core;

import lombok.Getter;
import nl.rabobank.pirates.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class BattleService {

    @Autowired
    private PokemonService pokemonService;

    @Getter
    private Pokemon currentOwnPokemon;

    @Getter
    private Pokemon currentEnemyPokemon;

    public TurnInformation executeTurn() {
        if (currentOwnPokemon == null || currentEnemyPokemon == null) return null;
        // Pokemons must have been set before

        List<TurnAction> actions = new ArrayList<>();
        
        int ownPokemonMoveIndex = getRandomValue(0, currentOwnPokemon.getMoves().size());
        int enemyPokemonMoveIndex = getRandomValue(0, currentEnemyPokemon.getMoves().size());
        Move ownPokemonMove = currentOwnPokemon.getMoves().get(ownPokemonMoveIndex);
        Move enemyPokemonMove = currentEnemyPokemon.getMoves().get(enemyPokemonMoveIndex);

        // Decide who goes first
        boolean ownPokemonGoesFirst = false;
        if (currentOwnPokemon.getStatAmount(Stat.SPEED) > currentEnemyPokemon.getStatAmount(Stat.SPEED)) {
            ownPokemonGoesFirst = true;
        }

        if (ownPokemonGoesFirst) {

            processDamageOwnPokemonAgainstEnemyAndPutIntoActions(actions, ownPokemonMove);

            processDamageEnemyPokemonAgainstOwnAndPutIntoActions(actions, enemyPokemonMove);
        } else {

            processDamageEnemyPokemonAgainstOwnAndPutIntoActions(actions, enemyPokemonMove);

            processDamageOwnPokemonAgainstEnemyAndPutIntoActions(actions, ownPokemonMove);
        }

        actions.add(TurnAction.builder()
                .text("What will " + currentOwnPokemon.getName().toUpperCase() + " do?")
                .type(TurnActionType.TEXT_ONLY)
                .build());

        return TurnInformation.builder().actions(actions).build();
    }

    private void processDamageEnemyPokemonAgainstOwnAndPutIntoActions(final List<TurnAction> actions, Move pokemonMove) {

        int damageAgainstOwnPokemon = calculateDamage(currentEnemyPokemon.getLevel(), pokemonMove.getPower(), currentEnemyPokemon.getStatAmount(Stat.ATTACK), currentOwnPokemon.getStatAmount(Stat.DEFENSE));

        actions.add(TurnAction.builder()
                .text(buildPokemonUsedMove(currentEnemyPokemon.getName(), pokemonMove.getName(), true))
                .type(TurnActionType.TEXT_ONLY)
                .build());

        currentOwnPokemon.dealDamage(damageAgainstOwnPokemon);

        actions.add(TurnAction.builder()
                .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_OWN)
                .damage(damageAgainstOwnPokemon)
                .build());
    }

    private void processDamageOwnPokemonAgainstEnemyAndPutIntoActions(final List<TurnAction> actions, Move pokemonMove) {
        int damageAgainstEnemyPokemon = calculateDamage(currentOwnPokemon.getLevel(), pokemonMove.getPower()
                , currentOwnPokemon.getStatAmount(Stat.ATTACK), currentEnemyPokemon.getStatAmount(Stat.DEFENSE));
        // Own pokemon
        actions.add(TurnAction.builder()
                .text(buildPokemonUsedMove(currentOwnPokemon.getName(), pokemonMove.getName(), false))
                .type(TurnActionType.TEXT_ONLY)
                .build());

        currentEnemyPokemon.dealDamage(damageAgainstEnemyPokemon);
        actions.add(TurnAction.builder()
                .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY)
                .damage(damageAgainstEnemyPokemon)
                .build());
    }

    private int calculateDamage(int level, int movePower, int attack, int defense) {
        if (movePower == 0) return 0;
        return Math.round(
                ((((level * 2)/5 + 2) * movePower * attack/defense)/50) + 2
        );
    }

    private String buildPokemonUsedMove(String pokemonName, String moveName, boolean isFoe) {
        String prefix = isFoe ? "Foe " : "";
        return prefix + pokemonName + " used " + moveName.toUpperCase();
    }

    private int getRandomValue(int rangeStart, int rangeEnd) {
        final Random random = new Random();
        return random.ints(rangeStart, rangeEnd).findFirst().getAsInt();
    }

    public Pokemon selectOwnPokemonByName(final String pokemonName, final int level) {
        currentOwnPokemon = pokemonService.getPokemonByName(pokemonName, level);
        return currentOwnPokemon;
    }

    public Pokemon selectEnemyPokemonByName(final String pokemonName, int level) {
        currentEnemyPokemon = pokemonService.getPokemonByName(pokemonName, level);
        return currentEnemyPokemon;
    }

}

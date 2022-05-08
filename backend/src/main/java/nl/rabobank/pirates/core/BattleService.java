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
        return executeTurn(currentOwnPokemon, currentEnemyPokemon);
    }
    private TurnInformation executeTurn(Pokemon ownPokemon, Pokemon enemyPokemon) {
        List<TurnAction> actions = new ArrayList<>();
        
        int ownPokemonMoveIndex = getRandomValue(0, ownPokemon.getMoves().size());
        int enemyPokemonMoveIndex = getRandomValue(0, enemyPokemon.getMoves().size());
        Move ownPokemonMove = ownPokemon.getMoves().get(ownPokemonMoveIndex);
        Move enemyPokemonMove = enemyPokemon.getMoves().get(enemyPokemonMoveIndex);

        // Decide who goes first
        boolean ownPokemonGoesFirst = false;
        if (ownPokemon.getStatAmount(Stat.SPEED) > enemyPokemon.getStatAmount(Stat.SPEED)) {
            ownPokemonGoesFirst = true;
        }
        int damageAgainstEnemyPokemon = calculateDamage(ownPokemon.getLevel(), ownPokemonMove.getPower(), ownPokemon.getStatAmount(Stat.ATTACK), enemyPokemon.getStatAmount(Stat.DEFENSE));
        int damageAgainstOwnPokemon = calculateDamage(enemyPokemon.getLevel(), enemyPokemonMove.getPower(), enemyPokemon.getStatAmount(Stat.ATTACK), ownPokemon.getStatAmount(Stat.DEFENSE));
        if (ownPokemonGoesFirst) {
            // Execute own move against foe
            // calculate damage of own pokemon
            actions.add(TurnAction.builder()
                    .text(buildPokemonUsedMove(ownPokemon.getName(), ownPokemonMove.getName(), false))
                    .type(TurnActionType.TEXT_ONLY)
                    .build());

            enemyPokemon.dealDamage(damageAgainstEnemyPokemon);
            actions.add(TurnAction.builder()
                    .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY)
                    .damage(damageAgainstEnemyPokemon)
                    .build());
            // Execute enemy turn
            // Calculate their damage
            actions.add(TurnAction.builder()
                    .text(buildPokemonUsedMove(enemyPokemon.getName(), enemyPokemonMove.getName(), true))
                    .type(TurnActionType.TEXT_ONLY)
                    .build());

            ownPokemon.dealDamage(damageAgainstOwnPokemon);

            actions.add(TurnAction.builder()
                    .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_OWN)
                    .damage(damageAgainstOwnPokemon)
                    .build());
        } else {
            // Execute enemy turn
            // Calculate their damage
            actions.add(TurnAction.builder()
                    .text(buildPokemonUsedMove(enemyPokemon.getName(), enemyPokemonMove.getName(), true))
                    .type(TurnActionType.TEXT_ONLY)
                    .build());

            ownPokemon.dealDamage(damageAgainstOwnPokemon);

            actions.add(TurnAction.builder()
                    .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_OWN)
                    .damage(damageAgainstOwnPokemon)
                    .build());

            // Execute own move against foe
            // calculate damage of own pokemon
            actions.add(TurnAction.builder()
                    .text(buildPokemonUsedMove(ownPokemon.getName(), ownPokemonMove.getName(), false))
                    .type(TurnActionType.TEXT_ONLY)
                    .build());

            enemyPokemon.dealDamage(damageAgainstEnemyPokemon);
            actions.add(TurnAction.builder()
                    .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY)
                    .damage(damageAgainstEnemyPokemon)
                    .build());
        }

        actions.add(TurnAction.builder()
                .text("What will " + ownPokemon.getName().toUpperCase() + " do?")
                .type(TurnActionType.TEXT_ONLY)
                .build());

        return TurnInformation.builder().actions(actions).build();
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

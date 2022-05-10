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

    @Autowired
    private TurnActionService turnActionService;

    @Getter
    private Pokemon currentOwnPokemon;

    @Getter
    private Pokemon currentEnemyPokemon;

    public TurnInformation executeTurn() {
        if (currentOwnPokemon == null || currentEnemyPokemon == null) return null;

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

            turnActionService.processMoveAndAddToActions(actions, ownPokemonMove, currentOwnPokemon, currentEnemyPokemon, true);

            turnActionService.processMoveAndAddToActions(actions, enemyPokemonMove, currentEnemyPokemon, currentOwnPokemon, false);
        } else {
            turnActionService.processMoveAndAddToActions(actions, enemyPokemonMove, currentEnemyPokemon, currentOwnPokemon, false);

            turnActionService.processMoveAndAddToActions(actions, ownPokemonMove, currentOwnPokemon, currentEnemyPokemon, true);
        }

        actions.add(TurnAction.builder()
                .text("What will " + currentOwnPokemon.getName().toUpperCase() + " do?")
                .type(TurnActionType.TEXT_ONLY)
                .build());

        return TurnInformation.builder().actions(actions).build();
    }

    public Pokemon selectOwnPokemonByName(final String pokemonName, final int level) {
        currentOwnPokemon = pokemonService.getPokemonByName(pokemonName, level);
        return currentOwnPokemon;
    }

    public Pokemon selectEnemyPokemonByName(final String pokemonName, int level) {
        currentEnemyPokemon = pokemonService.getPokemonByName(pokemonName, level);
        return currentEnemyPokemon;
    }

    private int getRandomValue(int rangeStart, int rangeEnd) {
        final Random random = new Random();
        return random.ints(rangeStart, rangeEnd).findFirst().getAsInt();
    }

}

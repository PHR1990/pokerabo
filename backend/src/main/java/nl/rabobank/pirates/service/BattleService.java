package nl.rabobank.pirates.service;

import lombok.Getter;
import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionType;
import nl.rabobank.pirates.model.battle.TurnInformation;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.move.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BattleService {

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private TurnActionService turnActionService;

    @Autowired
    private CalculationService calculationService;

    @Getter
    private Pokemon currentOwnPokemon;

    @Getter
    private Pokemon currentEnemyPokemon;

    public TurnInformation executeTurn() {
        if (currentOwnPokemon == null || currentEnemyPokemon == null) return null;

        List<TurnAction> actions = new ArrayList<>();
        
        int ownPokemonMoveIndex = calculationService.getRandomValue(0, currentOwnPokemon.getMoves().size());
        int enemyPokemonMoveIndex = calculationService.getRandomValue(0, currentEnemyPokemon.getMoves().size());
        Move ownPokemonMove = currentOwnPokemon.getMoves().get(ownPokemonMoveIndex);
        Move enemyPokemonMove = currentEnemyPokemon.getMoves().get(enemyPokemonMoveIndex);

        // Decide who goes first
        boolean ownPokemonGoesFirst = false;
        if (currentOwnPokemon.getStatAmount(Stat.SPEED) > currentEnemyPokemon.getStatAmount(Stat.SPEED)) {
            ownPokemonGoesFirst = true;
        }

        if (ownPokemonGoesFirst) {

            turnActionService.processMoveAndAddToActions(actions, ownPokemonMove, currentOwnPokemon, currentEnemyPokemon, true);

            if (currentEnemyPokemon.getCurrentHp() > 0) {
                turnActionService.processMoveAndAddToActions(actions, enemyPokemonMove, currentEnemyPokemon, currentOwnPokemon, false);
            }
        } else {

            turnActionService.processMoveAndAddToActions(actions, enemyPokemonMove, currentEnemyPokemon, currentOwnPokemon, false);

            if (currentOwnPokemon.getCurrentHp() > 0) {
                turnActionService.processMoveAndAddToActions(actions, ownPokemonMove, currentOwnPokemon, currentEnemyPokemon, true);
            }

        }

        if (currentOwnPokemon.getCurrentHp() > 0 && currentEnemyPokemon.getCurrentHp() > 0) {
            actions.add(TurnAction.builder()
                    .text("What will " + currentOwnPokemon.getName().toUpperCase() + " do?")
                    .type(TurnActionType.TEXT_ONLY)
                    .build());
        }

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

}

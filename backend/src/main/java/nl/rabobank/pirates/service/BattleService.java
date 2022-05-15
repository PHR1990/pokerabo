package nl.rabobank.pirates.service;

import lombok.Getter;
import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionFactory;
import nl.rabobank.pirates.model.battle.TurnInformation;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.move.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static nl.rabobank.pirates.model.move.StatusEffect.Condition.BURN;
import static nl.rabobank.pirates.model.move.StatusEffect.Condition.POISON;

@Component
public class BattleService {

    @Autowired
    private GetPokemonService pokemonService;

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
        boolean ownPokemonGoesFirst = currentOwnPokemon.getStatAmount(Stat.SPEED) > currentEnemyPokemon.getStatAmount(Stat.SPEED);

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

        if (areBothPokemonStillAlive()) {

            applyBurnOrPoison(currentEnemyPokemon, actions);
            turnActionService.checkIfDefendingPokemonFaintedAndProcessFainting(currentEnemyPokemon, false, actions);
        }
        if (areBothPokemonStillAlive()) {
            applyBurnOrPoison(currentOwnPokemon, actions);
            turnActionService.checkIfDefendingPokemonFaintedAndProcessFainting(currentOwnPokemon, true, actions);
        }

        if (areBothPokemonStillAlive()) {

            actions.add(TurnActionFactory.makeWhatWillPokemonDo(currentOwnPokemon.getName()));
        }

        return TurnInformation.builder().actions(actions).build();
    }

    private boolean areBothPokemonStillAlive() {
        return currentOwnPokemon.getCurrentHp() > 0 && currentEnemyPokemon.getCurrentHp() > 0;
    }

    public void applyBurnOrPoison(Pokemon pokemon, List<TurnAction> actions) {

        final TurnAction.Subject target = pokemon == currentEnemyPokemon ? TurnAction.Subject.ENEMY : TurnAction.Subject.OWN;

        int damage = pokemon.getMaxHp()/8;
        pokemon.dealDamage(damage);
        if (pokemon.isPokemonAfflictedBy(BURN)) {

            actions.add(TurnActionFactory.makeWithTextPokemonIsHurtByItsBurn(pokemon.getName(), target));

            actions.add(TurnActionFactory.makeDamageOnlyAnimation(damage,target));

        } else if (pokemon.isPokemonAfflictedBy(POISON)) {

            actions.add(TurnActionFactory.makeWithTextPokemonIsHurtByPoison(pokemon.getName(), target));

            actions.add(TurnActionFactory.makeDamageOnlyAnimation(damage, target));
        }

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

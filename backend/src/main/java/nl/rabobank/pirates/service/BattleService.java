package nl.rabobank.pirates.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static nl.rabobank.pirates.model.move.StatusEffect.Condition.BURN;
import static nl.rabobank.pirates.model.move.StatusEffect.Condition.POISON;

@Component
@RequiredArgsConstructor
public class BattleService {

    private final GetPokemonService pokemonService;

    private final TurnInformationService turnInformationService;

    private final RollService rollService;

    @Getter
    private Pokemon currentOwnPokemon;

    @Getter
    private Pokemon currentEnemyPokemon;
    
    private AtomicInteger counter = new AtomicInteger(0);

    public TurnInformation executeTurn() {
        if (currentOwnPokemon == null || currentEnemyPokemon == null) return null;

        List<TurnAction> actions = new ArrayList<>();
        
        System.out.println("Calls to battleService: " + counter.incrementAndGet());
        
        int ownPokemonMoveIndex = rollService.getRandomValue(0, currentOwnPokemon.getMoves().size());
        int enemyPokemonMoveIndex = rollService.getRandomValue(0, currentEnemyPokemon.getMoves().size());
        Move ownPokemonMove = currentOwnPokemon.getMoves().get(ownPokemonMoveIndex);
        Move enemyPokemonMove = currentEnemyPokemon.getMoves().get(enemyPokemonMoveIndex);

        // Decide who goes first
        boolean ownPokemonGoesFirst = currentOwnPokemon.getStatAmount(Stat.SPEED) > currentEnemyPokemon.getStatAmount(Stat.SPEED);

        if (ownPokemonGoesFirst) {
            turnInformationService.processMoveAndAddToActions(actions, ownPokemonMove, currentOwnPokemon, currentEnemyPokemon, true);
            if (areBothPokemonStillAlive()) {
                turnInformationService.processMoveAndAddToActions(actions, enemyPokemonMove, currentEnemyPokemon, currentOwnPokemon, false);
            }
        } else {
            turnInformationService.processMoveAndAddToActions(actions, enemyPokemonMove, currentEnemyPokemon, currentOwnPokemon, false);
            if (areBothPokemonStillAlive()) {
                turnInformationService.processMoveAndAddToActions(actions, ownPokemonMove, currentOwnPokemon, currentEnemyPokemon, true);
            }
        }

        if (areBothPokemonStillAlive()) {

            applyBurnOrPoison(currentEnemyPokemon, actions);
            // Apply seeding
            turnInformationService.checkIfDefendingPokemonFaintedAndAddFaintingToActions(currentEnemyPokemon, false, actions);
        }
        if (areBothPokemonStillAlive()) {
            applyBurnOrPoison(currentOwnPokemon, actions);
            // Apply seeding
            turnInformationService.checkIfDefendingPokemonFaintedAndAddFaintingToActions(currentOwnPokemon, true, actions);
        }

        if (areBothPokemonStillAlive()) {

            actions.add(TurnActionFactory.makeWhatWillPokemonDo(currentOwnPokemon.getName()));
        }
        
        //TODO: once adding of actions has been properly refactored (see TurnInformationService) this can be removed
        actions.removeIf(Objects::isNull);
        
        //Below is used for debugging purposes and would not be part of PR
        System.out.println("Number of actions: " + actions.size());
        for (TurnAction turnAction : actions) {
        	System.out.println(turnAction);
		}
        
        return TurnInformation.builder().actions(actions).build();
    }

    private boolean areBothPokemonStillAlive() {
        return currentOwnPokemon.getCurrentHp() > 0 && currentEnemyPokemon.getCurrentHp() > 0;
    }

    public void applyBurnOrPoison(Pokemon pokemon, List<TurnAction> actions) {

        final TurnAction.Subject target = pokemon == currentEnemyPokemon ? TurnAction.Subject.ENEMY : TurnAction.Subject.OWN;

        if (pokemon.isPokemonAfflictedBy(BURN)) {

            int damage = pokemon.getMaxHp()/8;
            pokemon.dealDamage(damage);

            actions.add(TurnActionFactory.makeWithTextPokemonIsHurtByItsBurn(pokemon.getName(), target));

            actions.add(TurnActionFactory.makeDamageOnlyAnimation(damage,target));

        } else if (pokemon.isPokemonAfflictedBy(POISON)) {

            int damage = pokemon.getMaxHp()/8;
            pokemon.dealDamage(damage);

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

package nl.rabobank.pirates.service;

import static nl.rabobank.pirates.model.battle.TurnActionFactory.makeFaintAnimation;
import static nl.rabobank.pirates.model.move.StatusEffect.Condition.CONFUSED;
import static nl.rabobank.pirates.model.move.StatusEffect.Condition.PARALYZED;
import static nl.rabobank.pirates.model.move.StatusEffect.Condition.SLEEP;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nl.rabobank.pirates.handlers.MoveHandler;
import nl.rabobank.pirates.handlers.MoveHandlerFactory;
import nl.rabobank.pirates.helper.TurnActionHelper;
import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionFactory;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.move.Move;

@Component
@RequiredArgsConstructor
public class TurnInformationService {

    private final CalculationService calculationService;

    /*
     * TODO: more to refactor.
     * 1) Refactor the below 3 checks into the handlers (add isMoveAllowed method to interface that checks whether the specific move
     * is allowed for the attacking pokemon based on its current status effect). Separate any checks from the adding of actions to prevent null actions being added.
     * 2) Also refactor each method in such a way that only one action is determined/added so that it is better unit testable (did this for only part of the methods)
     */
    public void processMoveAndAddToActions(final List<TurnAction> actions, Move pokemonMove, Pokemon attackingPokemon, Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {
        // Check if pokemon awakes/freeze
        // check confusion
    	
        if (!checkIfPokemonIsParalyzedAndCanAttackAndAddAction(attackingPokemon, actions)) {
            return;
        }
        if (!checkIfPokemonIsAsleepAndDecrementCounterAndAddAction(attackingPokemon, actions, isOwnPokemonAttacking)) {
            return;
        }
        if (!checkIfPokemonIsConfusedAndDecrementCounterAndAddAction(attackingPokemon, actions, isOwnPokemonAttacking, pokemonMove)) {
            return;
        }
        
        MoveHandler moveHandler = MoveHandlerFactory.getHandler(pokemonMove.getDamageClass());
        
        System.out.println("MoveHandler is of type " + moveHandler.getClass());
    	
        actions.addAll(moveHandler.handleMove(pokemonMove, attackingPokemon, defendingPokemon, isOwnPokemonAttacking));
    }


    private boolean checkIfPokemonIsParalyzedAndCanAttackAndAddAction(final Pokemon attackingPokemon, final List<TurnAction> actions) {
        if (attackingPokemon.isPokemonAfflictedBy(PARALYZED)) {
            boolean cantAttack = calculationService.isRollSuccessful(25);

            if (cantAttack) {
                actions.add(TurnActionFactory.makePokemonIsFullyParalyzed(attackingPokemon.getName()));
                return false;
            }
        }

        return true;
    }

    private boolean checkIfPokemonIsConfusedAndDecrementCounterAndAddAction(final Pokemon attackingPokemon, final List<TurnAction> actions, boolean isOwnPokemonAttacking, Move pokemonMove) {
        if (attackingPokemon.isPokemonAfflictedBy(CONFUSED)) {

            if (attackingPokemon.decrementConfusionCounterAndReturn() > 0) {
                boolean cantAttack = calculationService.isRollSuccessful(50);

                if (cantAttack) {
                    int confusionDamage = calculationService.calculateDamage(attackingPokemon.getLevel(), 40,
                            attackingPokemon.getStatAmount(Stat.ATTACK),
                            attackingPokemon.getStatAmount(Stat.DEFENSE), 
                            attackingPokemon.getType().equals(pokemonMove.getType()));

                    attackingPokemon.dealDamage(confusionDamage);

                    actions.add(TurnActionFactory.makePokemonHurtItselfInConfusion(confusionDamage, TurnActionHelper.getSubject(isOwnPokemonAttacking)));

                    checkIfDefendingPokemonFaintedAndAddFaintingToActions(attackingPokemon, isOwnPokemonAttacking, actions);

                    return false;
                }
            } else {
                actions.add(TurnActionFactory.makePokemonSnappedOutOfConfusion(attackingPokemon.getName()));
            }
        }
        return true;
    }

    private boolean checkIfPokemonIsAsleepAndDecrementCounterAndAddAction(final Pokemon attackingPokemon, final List<TurnAction> actions, boolean isOwnPokemonAttacking) {
        if (attackingPokemon.isPokemonAfflictedBy(SLEEP)) {
            if (attackingPokemon.decrementSleepCounterAndReturn() > 0) {
                actions.add(TurnActionFactory.makePokemonIsStillAsleep(attackingPokemon.getName()));
                return false;
            }

            actions.add(TurnActionFactory.makePokemonWokeUp(attackingPokemon.getName(), TurnActionHelper.getSubject(isOwnPokemonAttacking)));
        }
        return true;
    }

    public void checkIfDefendingPokemonFaintedAndAddFaintingToActions(final Pokemon defendingPokemon, boolean isOwnPokemonAttacking,
                                                                      final List<TurnAction> actions) {
        if (defendingPokemon.getCurrentHp() <= 0) {
            actions.add(makeFaintAnimation(defendingPokemon.getName().toUpperCase(), TurnActionHelper.getSubject(isOwnPokemonAttacking)));
        }
    }
}

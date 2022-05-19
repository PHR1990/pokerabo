package nl.rabobank.pirates.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionFactory;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatChange;
import nl.rabobank.pirates.model.common.StatMultiplier;
import nl.rabobank.pirates.model.move.Move;
import org.springframework.stereotype.Component;

import java.util.List;

import static nl.rabobank.pirates.model.battle.TurnActionFactory.makeFaintAnimation;
import static nl.rabobank.pirates.model.move.StatusEffect.Condition.*;

@Component
@RequiredArgsConstructor
public class TurnInformationService {

    private final CalculationService calculationService;

    public void processMoveAndAddToActions(final List<TurnAction> actions, Move pokemonMove, Pokemon attackingPokemon, Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {
        // Check if pokemon awakes/freeze
        // check confusion
        if (!checkIfPokemonIsParalyzedAndCanAttackAndAddAction(attackingPokemon, actions)) {
            return;
        }
        if (!checkIfPokemonIsAsleepAndDecrementCounterAndAddAction(attackingPokemon, actions, isOwnPokemonAttacking)) {
            return;
        }
        if (!checkIfPokemonIsConfusedAndDecrementCounterAndAddAction(attackingPokemon, actions, isOwnPokemonAttacking)) {
            return;
        }
        switch (pokemonMove.getDamageClass()) {
            case SPECIAL -> {
                int damage = calculationService.calculateSpecialDamage(attackingPokemon, defendingPokemon, pokemonMove);
                processDamageClassAndAddIntoActions(actions, pokemonMove, damage, attackingPokemon, defendingPokemon, isOwnPokemonAttacking);
            }
            case PHYSICAL -> {
                int damage = calculationService.calculatePhysicalDamage(attackingPokemon, defendingPokemon, pokemonMove);
                processDamageClassAndAddIntoActions(actions, pokemonMove, damage, attackingPokemon, defendingPokemon, isOwnPokemonAttacking);
            }
            case STATUS -> processStatusChangeClassAndAddIntoActions(actions, pokemonMove, attackingPokemon, defendingPokemon, isOwnPokemonAttacking);
        }
    }

    private void processDamageClassAndAddIntoActions(
            final List<TurnAction> actions, final Move pokemonMove, final int damage,
            final Pokemon attackingPokemon, final Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {

        addToActionsPokemonUsedMove(attackingPokemon, pokemonMove, actions, isOwnPokemonAttacking);

        if (!willMoveHit(pokemonMove, attackingPokemon, defendingPokemon)) {
            actions.add(TurnActionFactory.makeTextOnly("The attack missed!"));
            return;
        }

        // TODO calculate critical
        // TODO calculate weakness/strenghts

        checkNumberOfHitsAndProcessNumberOfHitsAndAddAction(pokemonMove, defendingPokemon, isOwnPokemonAttacking, damage, actions);

        checkIfDefendingPokemonFaintedAndAddFaintingToActions(defendingPokemon, isOwnPokemonAttacking, actions);

        processMoveStatusEffectAndAddToAction(defendingPokemon, pokemonMove, actions, isOwnPokemonAttacking);
    }
    
    private void processStatusChangeClassAndAddIntoActions(final List<TurnAction> actions, final Move pokemonMove,
                                                           final Pokemon attackingPokemon, final Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {
        
        addToActionsPokemonUsedMove(attackingPokemon, pokemonMove, actions, isOwnPokemonAttacking);

        if (!willMoveHit(pokemonMove, attackingPokemon, defendingPokemon)) {
            actions.add(TurnActionFactory.makeTextOnly("The attack missed!"));
            return;
        }
        processMoveStatChangesAndAddToAction(attackingPokemon, defendingPokemon, pokemonMove, actions);

        processMoveStatusEffectAndAddToAction(defendingPokemon, pokemonMove, actions, isOwnPokemonAttacking);
    }

    private void processMoveStatChangesAndAddToAction(final Pokemon attackingPokemon, final Pokemon defendingPokemon, final Move pokemonMove, final List<TurnAction> actions) {
        for (StatChange statChange : pokemonMove.getStatChanges()) {

            if (statChange.getChangeAmount() > 0) {

                boolean wasModified = attackingPokemon
                        .addStatMultiplier(StatMultiplier.builder().stat(statChange.getStat())
                                .stageModification(statChange.getChangeAmount()).build());

                if (wasModified) {
                    actions.add(TurnActionFactory.makeTextStatRose(attackingPokemon.getName(), statChange.getStat().getLabel()));

                } else {
                    actions.add(TurnActionFactory.makeTextStatWontRaiseHigher(attackingPokemon.getName(), statChange.getStat().getLabel()));

                }

            } else {
                boolean wasModified = defendingPokemon
                        .addStatMultiplier(StatMultiplier.builder().stat(statChange.getStat())
                                .stageModification(statChange.getChangeAmount()).build());
                if (wasModified) {

                    actions.add(TurnActionFactory.makeTextStatFell(defendingPokemon.getName(), statChange.getStat().getLabel()));
                } else {
                    actions.add(TurnActionFactory.makeTextStatWontFallLower(defendingPokemon.getName(), statChange.getStat().getLabel()));
                }
            }

        }
    }

    private void processMoveStatusEffectAndAddToAction(final Pokemon defendingPokemon, final Move pokemonMove, final List<TurnAction> actions, boolean isOwnPokemonAttacking) {
        if (pokemonMove.getStatusEffect() == null) {
            return;
        }

        boolean isSuccessful = calculationService.isRollSuccessful(pokemonMove.getStatusEffect().getChance());
        if (!isSuccessful) {
            return;
        }

        boolean canApplyStatusEffect = defendingPokemon.addStatusEffect(pokemonMove.getStatusEffect().getCondition());

        if (!canApplyStatusEffect) {
            actions.add(TurnActionFactory.makeTextOnly("The attack missed!"));
            return;
        }
        if (SLEEP.equals(pokemonMove.getStatusEffect().getCondition())) {
            defendingPokemon.putPokemonToSleep(calculationService.randomSleepOrConfusedTurns());
        }
        if (CONFUSED.equals(pokemonMove.getStatusEffect().getCondition())) {
            defendingPokemon.confusePokemon(calculationService.randomSleepOrConfusedTurns());
        }
        TurnAction.Subject subjectDefendingPokemon = isOwnPokemonAttacking ?  TurnAction.Subject.ENEMY : TurnAction.Subject.OWN;

        actions.add(TurnActionFactory.makeStatusEffect(defendingPokemon.getName(), pokemonMove.getStatusEffect().getCondition(), subjectDefendingPokemon));
    }

    private void checkNumberOfHitsAndProcessNumberOfHitsAndAddAction(final Move pokemonMove, final Pokemon defendingPokemon, final boolean isOwnPokemonAttacking, final int damage, final List<TurnAction> actions) {
        int numberHits = calculationService.calculateNumberOfHitTimes(pokemonMove.getHitTimes());

        for (int x = 0; x < numberHits; x++) {

            defendingPokemon.dealDamage(damage);

            TurnAction.Subject subjectDefendingPokemon = isOwnPokemonAttacking ? TurnAction.Subject.ENEMY : TurnAction.Subject.OWN;

            actions.add(TurnActionFactory.makeDamageOnlyAnimation(damage, subjectDefendingPokemon));
        }

        if (numberHits > 1) {
            actions.add(TurnActionFactory.makeTextHitXTimes(numberHits));
        }
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

    private boolean checkIfPokemonIsConfusedAndDecrementCounterAndAddAction(final Pokemon attackingPokemon, final List<TurnAction> actions, boolean isOwnPokemonAttacking) {
        if (attackingPokemon.isPokemonAfflictedBy(CONFUSED)) {

            if (attackingPokemon.decrementConfusionCounterAndReturn() > 0) {
                boolean cantAttack = calculationService.isRollSuccessful(50);

                if (cantAttack) {
                    int confusionDamage = calculationService.calculateDamage(attackingPokemon.getLevel(), 40,
                            attackingPokemon.getStatAmount(Stat.ATTACK),
                            attackingPokemon.getStatAmount(Stat.DEFENSE));

                    attackingPokemon.dealDamage(confusionDamage);

                    TurnAction.Subject subjectAttackingPokemon =
                            isOwnPokemonAttacking ?
                                    TurnAction.Subject.OWN:
                                    TurnAction.Subject.ENEMY;

                    actions.add(TurnActionFactory.makePokemonHurtItselfInConfusion(confusionDamage, subjectAttackingPokemon));

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

            TurnAction.Subject subjectAttackingPokemon =
                    isOwnPokemonAttacking ?
                            TurnAction.Subject.OWN:
                            TurnAction.Subject.ENEMY;

            actions.add(TurnActionFactory.makePokemonWokeUp(attackingPokemon.getName(), subjectAttackingPokemon));
        }
        return true;
    }

    public void checkIfDefendingPokemonFaintedAndAddFaintingToActions(final Pokemon defendingPokemon, boolean isOwnPokemonAttacking,
                                                                      final List<TurnAction> actions) {
        if (defendingPokemon.getCurrentHp() <= 0) {

            TurnAction.Subject subjectDefendingPokemon =
                    isOwnPokemonAttacking ?
                            TurnAction.Subject.ENEMY:
                            TurnAction.Subject.OWN;

            actions.add(makeFaintAnimation(defendingPokemon.getName().toUpperCase(), subjectDefendingPokemon));
        }
    }

    private boolean willMoveHit(Move move, Pokemon attackingPokemon, Pokemon defendingPokemon) {
        if (move.getAccuracy() == 0) return true;
        int moveAccuracy = move.getAccuracy();
        int pokemonAccuracy = attackingPokemon.getStatAmount(Stat.ACCURACY);

        return calculationService.calculateAccuracyAndRollIfMoveHits(pokemonAccuracy, moveAccuracy);
    }

    private void addToActionsPokemonUsedMove(final Pokemon attackingPokemon, final Move pokemonMove, final List<TurnAction> actions, final boolean isOwnPokemonAttacking) {
        TurnAction.Subject subjectAttackingPokemon =
                isOwnPokemonAttacking ?
                        TurnAction.Subject.OWN:
                        TurnAction.Subject.ENEMY;

        actions.add(
                TurnActionFactory.makePokemonUsedMove(attackingPokemon.getName().toUpperCase(), pokemonMove.getName(), subjectAttackingPokemon));
    }
}

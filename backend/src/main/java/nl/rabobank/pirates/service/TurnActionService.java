package nl.rabobank.pirates.service;

import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionFactory;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatChange;
import nl.rabobank.pirates.model.common.StatMultiplier;
import nl.rabobank.pirates.model.move.Move;
import nl.rabobank.pirates.model.move.StatusEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static nl.rabobank.pirates.model.battle.TurnActionFactory.makeFaintAnimation;

@Component
public class TurnActionService {

    @Autowired
    private CalculationService calculationService;

    public void processMoveAndAddToActions(final List<TurnAction> actions, Move pokemonMove, Pokemon attackingPokemon, Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {

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

        checkNumberOfHitsAndProcessNumberOfHits(pokemonMove, defendingPokemon, isOwnPokemonAttacking, damage, actions);

        checkIfDefendingPokemonFaintedAndProcessFainting(defendingPokemon, isOwnPokemonAttacking, actions);

        processMoveStatusEffect(defendingPokemon, pokemonMove, actions, isOwnPokemonAttacking);
    }
    
    private void processStatusChangeClassAndAddIntoActions(final List<TurnAction> actions, final Move pokemonMove,
                                                           final Pokemon attackingPokemon, final Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {
        
        addToActionsPokemonUsedMove(attackingPokemon, pokemonMove, actions, isOwnPokemonAttacking);

        if (!willMoveHit(pokemonMove, attackingPokemon, defendingPokemon)) {
            actions.add(TurnActionFactory.makeTextOnly("The attack missed!"));
            return;
        }

        processMoveStatChanges(attackingPokemon, defendingPokemon, pokemonMove, actions);

        processMoveStatusEffect(defendingPokemon, pokemonMove, actions, isOwnPokemonAttacking);
    }

    private void processMoveStatChanges(final Pokemon attackingPokemon, final Pokemon defendingPokemon, final Move pokemonMove, final List<TurnAction> actions) {
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

    private void processMoveStatusEffect(final Pokemon defendingPokemon, final Move pokemonMove, final List<TurnAction> actions, boolean isOwnPokemonAttacking) {
        if (pokemonMove.getStatusEffect() == null) {
            return;
        }
        boolean isSuccessful = calculationService.isRollSuccessful(pokemonMove.getStatusEffect().getChance());
        if (!isSuccessful) {
            return;
        }

        defendingPokemon.addStatusEffect(pokemonMove.getStatusEffect().getCondition());

        TurnAction.Subject subjectDefendingPokemon = isOwnPokemonAttacking ?  TurnAction.Subject.ENEMY : TurnAction.Subject.OWN;

        actions.add(TurnActionFactory.makeStatusEffect(defendingPokemon.getName(), defendingPokemon.getStatusEffects().get(0), subjectDefendingPokemon));

    }

    private void checkNumberOfHitsAndProcessNumberOfHits(final Move pokemonMove, final Pokemon defendingPokemon, final boolean isOwnPokemonAttacking, final int damage, final List<TurnAction> actions) {
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

    public void checkIfDefendingPokemonFaintedAndProcessFainting(final Pokemon defendingPokemon, boolean isOwnPokemonAttacking,
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

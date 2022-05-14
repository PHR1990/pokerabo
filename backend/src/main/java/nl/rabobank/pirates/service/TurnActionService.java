package nl.rabobank.pirates.service;

import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionFactory;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatChange;
import nl.rabobank.pirates.model.common.StatMultiplier;
import nl.rabobank.pirates.model.move.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static nl.rabobank.pirates.model.battle.TurnActionFactory.makeFaintAnimation;

@Component
public class TurnActionService {
    /*
    TODO in order to process the burn effect, we need to read from effectEntries.effect and roll for effect_change
     */
    @Autowired
    private CalculationService calculationService;

    public void processMoveAndAddToActions(final List<TurnAction> actions, Move pokemonMove, Pokemon attackingPokemon, Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {

        switch (pokemonMove.getDamageClass()) {
            case SPECIAL -> {
                int damage = calculationService.calculateDamage(
                        attackingPokemon.getLevel(), pokemonMove.getPower(),
                        attackingPokemon.getStatAmount(Stat.SPECIAL_ATTACK),
                        defendingPokemon.getStatAmount(Stat.SPECIAL_DEFENSE));
                processDamageClassAndAddIntoActions(actions, pokemonMove, damage, attackingPokemon, defendingPokemon, isOwnPokemonAttacking);
                break;
            }
            case PHYSICAL -> {
                int damage = calculationService.calculateDamage(
                        attackingPokemon.getLevel(), pokemonMove.getPower(),
                        attackingPokemon.getStatAmount(Stat.ATTACK),
                        defendingPokemon.getStatAmount(Stat.DEFENSE));
                processDamageClassAndAddIntoActions(actions, pokemonMove, damage, attackingPokemon, defendingPokemon, isOwnPokemonAttacking);
                break;
            }
            case STATUS ->  {
                processStatusChangeClassAndAddIntoActions(actions, pokemonMove, attackingPokemon, defendingPokemon, isOwnPokemonAttacking);
                break;
            }
        }
    }

    private void processDamageClassAndAddIntoActions(
            final List<TurnAction> actions, final Move pokemonMove, final int damage,
            final Pokemon attackingPokemon, final Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {

        TurnAction.Subject subjectAttackingPokemon = isOwnPokemonAttacking ? TurnAction.Subject.OWN : TurnAction.Subject.ENEMY;

        actions.add(TurnActionFactory.makePokemonUsedMove(attackingPokemon.getName().toUpperCase(), pokemonMove.getName(), subjectAttackingPokemon));

        if (!willMoveHit(pokemonMove, attackingPokemon, defendingPokemon)) {
            actions.add(TurnActionFactory.makeTextOnly("The attack missed!"));
            return;
        }

        int numberHits = calculationService.calculateNumberOfHitTimes(pokemonMove.getHitTimes());

        for (int x = 0; x < numberHits; x++) {

            defendingPokemon.dealDamage(damage);

            TurnAction.Subject subjectDefendingPokemon = isOwnPokemonAttacking ? TurnAction.Subject.ENEMY : TurnAction.Subject.OWN;

            actions.add(TurnActionFactory.makeDamageOnlyAnimation(damage, subjectDefendingPokemon));
        }

        if (numberHits > 1) {
            actions.add(TurnActionFactory.makeTextHitXTimes(numberHits));
        }

        if (defendingPokemon.getCurrentHp() <= 0) {

            TurnAction.Subject subjectDefendingPokemon =
                    isOwnPokemonAttacking ?
                            TurnAction.Subject.ENEMY:
                            TurnAction.Subject.OWN;

            actions.add(makeFaintAnimation(defendingPokemon.getName().toUpperCase(), subjectDefendingPokemon));
        }
    }

    private void processStatusChangeClassAndAddIntoActions(final List<TurnAction> actions, final Move pokemonMove,
                                                           final Pokemon attackingPokemon, final Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {

        TurnAction.Subject subjectAttackingPokemon =
                isOwnPokemonAttacking ?
                        TurnAction.Subject.OWN:
                        TurnAction.Subject.ENEMY;

        actions.add(
                TurnActionFactory.makePokemonUsedMove(attackingPokemon.getName().toUpperCase(), pokemonMove.getName(), subjectAttackingPokemon));

        if (!willMoveHit(pokemonMove, attackingPokemon, defendingPokemon)) {

            actions.add(TurnActionFactory.makeTextOnly("The attack missed!"));

            return;
        }

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

        if (pokemonMove.getStatusEffect() != null) {
            defendingPokemon.addStatusEffect(pokemonMove.getStatusEffect());

            TurnAction.Subject subjectDefendingPokemon = isOwnPokemonAttacking ?  TurnAction.Subject.ENEMY : TurnAction.Subject.OWN;

            actions.add(TurnActionFactory.makeStatusEffect(defendingPokemon.getStatusEffects().get(0), subjectDefendingPokemon));
        }
    }

    private boolean willMoveHit(Move move, Pokemon attackingPokemon, Pokemon defendingPokemon) {
        int moveAccuracy = move.getAccuracy();
        int pokemonAccuracy = attackingPokemon.getStatAmount(Stat.ACCURACY);

        return calculationService.calculateAccuracyAndRollIfMoveHits(pokemonAccuracy, moveAccuracy);
    }
}

package nl.rabobank.pirates.service;

import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionType;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatChange;
import nl.rabobank.pirates.model.common.StatMultiplier;
import nl.rabobank.pirates.model.move.HitTimes;
import nl.rabobank.pirates.model.move.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TurnActionService {

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


        actions.add(TurnAction.builder()
                .text(buildPokemonUsedMove(attackingPokemon.getName(), pokemonMove.getName(), !isOwnPokemonAttacking))
                .type(TurnActionType.TEXT_ONLY)
                .build());

        if (!willMoveHit(pokemonMove, attackingPokemon, defendingPokemon)) {

            actions.add(TurnAction.builder()
                    .text("The attack missed!")
                    .type(TurnActionType.TEXT_ONLY)
                    .build());

            return;
        }

        int numberHits = calculationService.calculateNumberOfHitTimes(pokemonMove.getHitTimes());

        for (int x = 0; x < numberHits; x++) {

            defendingPokemon.dealDamage(damage);

            TurnActionType targetPokemonAnimationType =
                    isOwnPokemonAttacking ?
                            TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY:
                            TurnActionType.DAMAGE_ANIMATION_AGAINST_OWN;

            actions.add(TurnAction.builder()
                    .type(targetPokemonAnimationType)
                    .damage(damage)
                    .build());
        }

        if (numberHits > 1) {
            actions.add(TurnAction.builder()
                    .text("Hit the enemy " + numberHits + " times!")
                    .type(TurnActionType.TEXT_ONLY)
                    .build());
        }


        if (defendingPokemon.getCurrentHp() <= 0) {

            TurnActionType targetPokemonFaint =
                    isOwnPokemonAttacking ?
                            TurnActionType.FAINT_ANIMATION_ENEMY_POKEMON :
                            TurnActionType.FAINT_ANIMATION_OWN_POKEMON;

            final String appendingMessage = isOwnPokemonAttacking ? "" : "Enemy ";
            final String message = appendingMessage + defendingPokemon.getName() + " fainted!";
            actions.add(TurnAction.builder()
                        .type(targetPokemonFaint)
                        .text(message)
                    .build());
        }
    }

    private boolean willMoveHit(Move move, Pokemon attackingPokemon, Pokemon defendingPokemon) {
        int moveAccuracy = move.getAccuracy();
        int pokemonAccuracy = attackingPokemon.getStatAmount(Stat.ACCURACY);

        return calculationService.calculateAccuracyAndRollIfMoveHits(pokemonAccuracy, moveAccuracy);
    }

    private void processStatusChangeClassAndAddIntoActions(final List<TurnAction> actions, final Move pokemonMove,
                                                           final Pokemon attackingPokemon, final Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {

        actions.add(TurnAction.builder()
                .text(buildPokemonUsedMove(attackingPokemon.getName(), pokemonMove.getName(), !isOwnPokemonAttacking))
                .type(TurnActionType.TEXT_ONLY)
                .build());

        for (StatChange statChange : pokemonMove.getStatChanges()) {
            TurnActionType targetPokemonAnimationType;
            String text;
            if (statChange.getChangeAmount() > 0) {

                targetPokemonAnimationType = TurnActionType.STAT_EFFECT_AGAINST_OWN;
                boolean wasModified = attackingPokemon
                        .addStatMultiplier(StatMultiplier.builder().stat(statChange.getStat())
                                .stageModification(statChange.getChangeAmount()).build());

                if (wasModified) {
                    text = attackingPokemon.getName() + " " + statChange.getStat().getLabel() + " rose!";
                } else {
                    text = attackingPokemon.getName() + " " + statChange.getStat().getLabel() + " won't go any higher!";
                }

            } else {

                targetPokemonAnimationType = TurnActionType.STAT_EFFECT_AGAINST_ENEMY;
                boolean wasModified = defendingPokemon
                        .addStatMultiplier(StatMultiplier.builder().stat(statChange.getStat())
                                .stageModification(statChange.getChangeAmount()).build());
                if (wasModified) {
                    text = defendingPokemon.getName() + " " + statChange.getStat().getLabel() + " fell!";
                } else {
                    text = defendingPokemon.getName() + " " + statChange.getStat().getLabel() + " won't go any lower!";
                }
            }

            actions.add(TurnAction.builder()
                    .text(text)
                    .type(TurnActionType.TEXT_ONLY)
                    .build());
        }
    }

    private String buildPokemonUsedMove(String pokemonName, String moveName, boolean shouldAppendIsFoeText) {
        String prefix = shouldAppendIsFoeText ? "Foe " : "";
        return prefix + pokemonName + " used " + moveName.toUpperCase();
    }
}

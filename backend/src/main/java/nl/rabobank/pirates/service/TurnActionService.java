package nl.rabobank.pirates.service;

import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionType;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatChange;
import nl.rabobank.pirates.model.common.StatMultiplier;
import nl.rabobank.pirates.model.move.Move;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class TurnActionService {

    public void processMoveAndAddToActions(final List<TurnAction> actions, Move pokemonMove, Pokemon attackingPokemon, Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {

        switch (pokemonMove.getDamageClass()) {
            case SPECIAL -> {
                int damage = calculateDamage(
                        attackingPokemon.getLevel(), pokemonMove.getPower(),
                        attackingPokemon.getStatAmount(Stat.SPECIAL_ATTACK),
                        defendingPokemon.getStatAmount(Stat.SPECIAL_DEFENSE));
                processDamageClassAndAddIntoActions(actions, pokemonMove, damage, attackingPokemon, defendingPokemon, isOwnPokemonAttacking);
                break;
            }
            case PHYSICAL -> {
                int damage = calculateDamage(
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

    private boolean willMoveHit(Move move, Pokemon attackingPokemon, Pokemon defendingPokemon) {
        // To properly calculate accuracy must account for evasion
        int moveAccuracy = move.getAccuracy();
        int pokemonAccuracy = attackingPokemon.getStatAmount(Stat.ACCURACY);

        int hitChance =  pokemonAccuracy * (moveAccuracy/100);

        int rangeRoll = getRandomValue(0, 101);

        return hitChance >= rangeRoll;
    }


    private int calculateDamage(int level, int movePower, int attack, int defense) {
        if (movePower == 0) return 0;

        int damage = Math.round(
                ((((level * 2)/5 + 2) * movePower * attack/defense)/50) + 2);
        return damage;
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

    private int getRandomValue(int rangeStart, int rangeEnd) {
        final Random random = new Random();
        return random.ints(rangeStart, rangeEnd).findFirst().getAsInt();
    }

    private String buildPokemonUsedMove(String pokemonName, String moveName, boolean shouldAppendIsFoeText) {
        String prefix = shouldAppendIsFoeText ? "Foe " : "";
        return prefix + pokemonName + " used " + moveName.toUpperCase();
    }
}

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

            processMoveAndAddToActions(actions, ownPokemonMove, currentOwnPokemon, currentEnemyPokemon);

            processMoveAndAddToActions(actions, enemyPokemonMove, currentEnemyPokemon, currentOwnPokemon);
        } else {
            processMoveAndAddToActions(actions, enemyPokemonMove, currentEnemyPokemon, currentOwnPokemon);

            processMoveAndAddToActions(actions, ownPokemonMove, currentOwnPokemon, currentEnemyPokemon);
        }

        actions.add(TurnAction.builder()
                .text("What will " + currentOwnPokemon.getName().toUpperCase() + " do?")
                .type(TurnActionType.TEXT_ONLY)
                .build());

        return TurnInformation.builder().actions(actions).build();
    }

    private void processMoveAndAddToActions(final List<TurnAction> actions, Move pokemonMove, Pokemon attackingPokemon, Pokemon defendingPokemon) {
        switch (pokemonMove.getDamageClass()) {
            case SPECIAL -> {
                int damage = calculateDamage(
                        attackingPokemon.getLevel(), pokemonMove.getPower(),
                        attackingPokemon.getStatAmount(Stat.SPECIAL_ATTACK),
                        defendingPokemon.getStatAmount(Stat.SPECIAL_DEFENSE));
                processDamageClassAndAddIntoActions(actions, pokemonMove, damage, attackingPokemon, defendingPokemon);
                break;
            }
            case PHYSICAL -> {
                int damage = calculateDamage(
                        attackingPokemon.getLevel(), pokemonMove.getPower(),
                        attackingPokemon.getStatAmount(Stat.ATTACK),
                        defendingPokemon.getStatAmount(Stat.DEFENSE));
                processDamageClassAndAddIntoActions(actions, pokemonMove, damage, attackingPokemon, defendingPokemon);
                break;
            }
            case STATUS ->  {
                processStatusChangeClassAndAddIntoActions(actions, pokemonMove, attackingPokemon, defendingPokemon);
                break;
            }
        }
    }

    private void processStatusChangeClassAndAddIntoActions(final List<TurnAction> actions, final Move pokemonMove,
                                                           final Pokemon attackingPokemon, final Pokemon defendingPokemon) {
        boolean isAttackingPokemonOwn = attackingPokemon.equals(currentOwnPokemon);

        actions.add(TurnAction.builder()
                .text(buildPokemonUsedMove(attackingPokemon.getName(), pokemonMove.getName(), !isAttackingPokemonOwn))
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

    private void processDamageClassAndAddIntoActions(
            final List<TurnAction> actions, final Move pokemonMove, final int damage,
            final Pokemon attackingPokemon, final Pokemon defendingPokemon) {

        boolean isAttackingPokemonOwn = attackingPokemon.equals(currentOwnPokemon);

        actions.add(TurnAction.builder()
                .text(buildPokemonUsedMove(attackingPokemon.getName(), pokemonMove.getName(), !isAttackingPokemonOwn))
                .type(TurnActionType.TEXT_ONLY)
                .build());

        defendingPokemon.dealDamage(damage);

        TurnActionType targetPokemonAnimationType =
                isAttackingPokemonOwn ?
                        TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY:
                        TurnActionType.DAMAGE_ANIMATION_AGAINST_OWN;

        actions.add(TurnAction.builder()
                .type(targetPokemonAnimationType)
                .damage(damage)
                .build());
    }

    private int calculateDamage(int level, int movePower, int attack, int defense) {
        if (movePower == 0) return 0;
        return Math.round(
                ((((level * 2)/5 + 2) * movePower * attack/defense)/50) + 2
        );
    }

    private String buildPokemonUsedMove(String pokemonName, String moveName, boolean shouldAppendIsFoeText) {
        String prefix = shouldAppendIsFoeText ? "Foe " : "";
        return prefix + pokemonName + " used " + moveName.toUpperCase();
    }

    private int getRandomValue(int rangeStart, int rangeEnd) {
        final Random random = new Random();
        return random.ints(rangeStart, rangeEnd).findFirst().getAsInt();
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

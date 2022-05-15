package nl.rabobank.pirates.model.common;

import lombok.Builder;
import lombok.Getter;
import nl.rabobank.pirates.model.move.Move;
import nl.rabobank.pirates.model.move.StatusEffect;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static nl.rabobank.pirates.model.move.StatusEffect.Condition.PARALYZED;

@Getter
@Builder
public class Pokemon {

    private String name;

    private URL backSpriteUrl;

    private URL frontSpriteUrl;

    private int maxHp;

    private int currentHp;

    private List<Move> moves;

    private List<StatAmount> stats;

    private List<StatMultiplier> statMultipliers;

    private int level;

    private Type type;

    private List<StatusEffect.Condition> statusEffectConditions;

    public void dealDamage(int damage) {
        currentHp-=damage;
    }

    public boolean isPokemonAfflictedBy(StatusEffect.Condition condition) {
        return statusEffectConditions != null &&
                statusEffectConditions.stream().anyMatch(statusEffect -> statusEffect.equals(condition));
    }

    public int getStatAmount(Stat stat) {
        if (statMultipliers == null) {
            statMultipliers = new ArrayList<>();
        }
        final int baseModifier = stat.equals(Stat.ACCURACY) || stat.equals(Stat.EVASION) ? 3 : 2;

        int increaseModifier = baseModifier;
        int decreaseModifier = baseModifier;

        for (StatMultiplier statMultiplier : statMultipliers) {
            if (statMultiplier.getStat().equals(stat)) {

                if (statMultiplier.getStageModification() > 0) {
                    increaseModifier+=statMultiplier.getStageModification();
                } else {
                    decreaseModifier+= (statMultiplier.getStageModification() * -1);
                }
            }
        }

        for (StatAmount statAmount : stats) {
            if (stat.equals(statAmount.getStat())) {
                int partialAmount = Math.round(statAmount.getAmount() * increaseModifier/decreaseModifier);

                if (stat.equals(Stat.SPEED) && statusEffectConditions != null && statusEffectConditions.contains(PARALYZED)) {
                    return Math.round(partialAmount * 0.25f);
                }

                return partialAmount;
            }
        }
        throw new RuntimeException("Couldnt find stat" + stat + " on pokemon" + getName());
    }

    public boolean addStatMultiplier(StatMultiplier statMultiplier) {
        if (statMultipliers == null) statMultipliers = new ArrayList<>();
        for (StatMultiplier existingStatMultiplier : statMultipliers) {
            if (existingStatMultiplier.getStat().equals(statMultiplier.getStat())) {
                return existingStatMultiplier.add(statMultiplier.getStageModification());
            }
        }
        statMultipliers.add(statMultiplier);
        return true;
    }

    public boolean addStatusEffect(StatusEffect.Condition condition) {
        if (statusEffectConditions == null) {
            statusEffectConditions = new ArrayList<>();
        }
        if (StatusEffect.PRIMARY_CONDITIONS.contains(condition)) {
            for (StatusEffect.Condition existingCondition : statusEffectConditions) {
                if (StatusEffect.PRIMARY_CONDITIONS.contains(existingCondition)) {
                    return false;
                }
            }
        }

        if (StatusEffect.SECONDARY_CONDITIONS.contains(condition)) {
            for (StatusEffect.Condition existingCondition : statusEffectConditions) {
                if (StatusEffect.SECONDARY_CONDITIONS.contains(existingCondition)) {
                    return false;
                }
            }
        }

        return statusEffectConditions.add(condition);
    }



}

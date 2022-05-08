package nl.rabobank.pirates.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public void dealDamage(int damage) {
        currentHp-=damage;
    }

    public int getStatAmount(Stat stat) {
        if (statMultipliers == null) {
            statMultipliers = new ArrayList<>();
        }
        int increaseModifier = 2;
        int decreaseModifier = 2;

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
                return Math.round(
                        statAmount.getAmount() * increaseModifier/decreaseModifier
                );
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

}

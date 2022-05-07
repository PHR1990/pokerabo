package nl.rabobank.pirates.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
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

    public void dealDamage(int damage) {
        currentHp-=damage;
    }

    public int getStatAmount(Stat stat) {
        for (StatAmount statAmount : stats) {
            if (stat.equals(statAmount.getStat())) return statAmount.getAmount();
        }
        throw new RuntimeException("Couldnt find stat" + stat + " on pokemon" + getName());
    }

}

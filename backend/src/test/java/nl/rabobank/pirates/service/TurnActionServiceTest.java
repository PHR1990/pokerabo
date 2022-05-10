package nl.rabobank.pirates.service;

import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionType;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatAmount;
import nl.rabobank.pirates.model.common.Type;
import nl.rabobank.pirates.model.move.DamageClass;
import nl.rabobank.pirates.model.move.Move;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TurnActionServiceTest {

    private TurnActionService sut = new TurnActionService();

    @Test
    public void when_pokemon_is_same_type_as_move_include_stab() {
        List<TurnAction> turnAction = new ArrayList<>();

        sut.processMoveAndAddToActions(turnAction, buildEmber(), buildCharmander(), buildPidgey(), true);

        TurnAction turnActionWithDamageAgainstEnemy = turnAction.stream().filter(e -> e.getType().equals(TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY)).findFirst().get();// 5
        assertThat(turnActionWithDamageAgainstEnemy.getDamage()).isBetween(16, 17);
    }
    @Test
    public void pokemon_is_not_same_type_as_move_dont_include_stab() {
        List<TurnAction> turnAction = new ArrayList<>();

        sut.processMoveAndAddToActions(turnAction, buildScratch(), buildCharmander(), buildPidgey(), true);

        TurnAction turnActionWithDamageAgainstEnemy = turnAction.stream().filter(e -> e.getType().equals(TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY)).findFirst().get();// 5
        assertThat(turnActionWithDamageAgainstEnemy.getDamage()).isEqualTo(9);
    }


    private Pokemon buildPidgey() {
        List<StatAmount> stats = Arrays.asList(
                StatAmount.builder().stat(Stat.SPECIAL_DEFENSE).amount(15).build(),
                StatAmount.builder().stat(Stat.DEFENSE).amount(17).build(),
                StatAmount.builder().stat(Stat.SPEED).amount(21).build()
        );

        return Pokemon.builder()
            .type(Type.NORMAL)
            .level(15)
            .stats(stats)
            .build();

    }
    private Move buildEmber() {
        return Move.builder()
                    .accuracy(100)
                    .damageClass(DamageClass.SPECIAL)
                    .name("Ember")
                    .power(40)
                    .priority(0)
                    .type(Type.FIRE)
                    .build();

    }
    private Move buildScratch() {
        return Move.builder()
                .accuracy(100)
                .damageClass(DamageClass.PHYSICAL)
                .name("Scratch")
                .power(40)
                .priority(0)
                .type(Type.NORMAL)
                .build();

    }
    private Pokemon buildCharmander() {
        List<StatAmount> stats = Arrays.asList(
                StatAmount.builder().stat(Stat.ACCURACY).amount(100).build(),
                StatAmount.builder().stat(Stat.ATTACK).amount(20).build(),
                StatAmount.builder().stat(Stat.SPECIAL_ATTACK).amount(23).build(),
                StatAmount.builder().stat(Stat.SPEED).amount(24).build()
        );

        return Pokemon.builder()
                    .type(Type.FIRE)
                    .level(15)
                    .stats(stats)
                    .build();
    }
}
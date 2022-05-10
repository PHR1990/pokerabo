package nl.rabobank.pirates.service;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CalculationServiceTest {

    private CalculationService sut = new CalculationService();

    @Ignore("Needs to be implemented")
    @Test
    public void when_pokemon_is_same_type_as_move_include_stab() {

        int damage = sut.calculateDamage(15, 40, 23, 15);

        assertThat(damage).isBetween(16, 17);
    }
    @Test
    public void pokemon_is_not_same_type_as_move_dont_include_stab() {

        int damage = sut.calculateDamage(15, 40, 20, 17);

        assertThat(damage).isEqualTo(9);
    }

}
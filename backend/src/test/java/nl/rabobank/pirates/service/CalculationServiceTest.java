package nl.rabobank.pirates.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import nl.rabobank.pirates.smoke.TestConfig;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = TestConfig.class)
class CalculationServiceTest {

    @Mock
    private RollService rollService;

    @InjectMocks
    private CalculationService sut;

    @Test
    public void when_pokemon_is_same_type_as_move_include_stab() {

        int damage = sut.calculateDamage(15, 40, 23, 15, true);

        assertThat(damage).isBetween(16, 17);
    }
    @Test
    public void pokemon_is_not_same_type_as_move_dont_include_stab() {

        int damage = sut.calculateDamage(15, 40, 20, 17, false);

        assertThat(damage).isEqualTo(9);
    }

    @Test
    public void when_level_5_stat_being_calculate_is_not_hp() {
        int stat = sut.calculateStat(5, 52);

        assertThat(stat).isEqualTo(10);
    }

    @Test
    public void when_level_100_stat_being_calculate_is_not_hp() {
        int stat = sut.calculateStat(100, 52);

        assertThat(stat).isEqualTo(109);
    }

    @Test
    public void when_level_5_stat_being_calculate_is_hp() {
        int stat = sut.calculateMaxHp(5, 39);

        assertThat(stat).isEqualTo(18);
    }

    @Test
    public void when_level_100_stat_being_calculate_is_hp() {
        int stat = sut.calculateMaxHp(100, 39);

        assertThat(stat).isEqualTo(188);
    }

}
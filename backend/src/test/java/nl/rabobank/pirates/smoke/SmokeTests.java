package nl.rabobank.pirates.smoke;

import nl.rabobank.pirates.core.BattleService;
import nl.rabobank.pirates.core.MoveService;
import nl.rabobank.pirates.core.PokemonApiRestClient;
import nl.rabobank.pirates.core.PokemonService;
import nl.rabobank.pirates.domain.TurnInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

/**
 * These smoke tests are used to guarantee that the core functionality works when the project is started up.
 *
 * As in:
 * External API is consumed from.
 * Calculations are done
 * Battle turns are somewhat respected
 *
 */
@SpringBootTest(classes = {PokemonService.class, BattleService.class, MoveService.class, PokemonApiRestClient.class})
class SmokeTests {

    @Autowired
    private BattleService battleService;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private MoveService moveService;

    @SpyBean
    private PokemonApiRestClient apiRestClient;

    @Test
    public void do_smoke_test() {
        battleService.selectEnemyPokemonByName("charmander", 5);
        pokemonService.getPokemonByName("charmander", 5);
        battleService.selectOwnPokemonByName("bulbasaur", 5);
        moveService.getMoveByName("growl");
        moveService.getMoveByName("growl");

        TurnInformation turnInformation = battleService.executeTurn();

        assert_application_doesnt_call_external_api_for_known_resource();
        assert_turn_was_executed(turnInformation);

    }
    private void assert_turn_was_executed(TurnInformation turnInformation) {
        Assertions.assertNotNull(turnInformation);
        Assertions.assertNotNull(turnInformation.getActions());
        Assertions.assertTrue(turnInformation.getActions().size() > 0);
    }
    /**
     * BE VERY CAREFUL TO NOT OVERLY CALL THE API OR ELSE THEY MAY BAN U BY IP
     */
    private void assert_application_doesnt_call_external_api_for_known_resource() {
        Mockito.verify(apiRestClient, Mockito.atMost(1)).getPokemonByName("charmander");
        Mockito.verify(apiRestClient, Mockito.atMost(1)).getMoveByName("growl");
    }

}
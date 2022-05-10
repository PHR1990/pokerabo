package nl.rabobank.pirates.smoke;

import nl.rabobank.pirates.service.BattleService;
import nl.rabobank.pirates.service.MoveService;
import nl.rabobank.pirates.client.PokemonApiRestClient;
import nl.rabobank.pirates.service.PokemonService;
import nl.rabobank.pirates.model.battle.TurnInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * These smoke tests are used to guarantee that the core functionality works when the project is started up.
 *
 * As in:
 * External API is consumed from.
 * Calculations are done
 * Battle turns are done
 *
 */
@SpringBootTest(classes = TestConfig.class)
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

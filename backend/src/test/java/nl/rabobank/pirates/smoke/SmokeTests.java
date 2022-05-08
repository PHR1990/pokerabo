package nl.rabobank.pirates.smoke;

import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.core.BattleService;
import nl.rabobank.pirates.core.MoveService;
import nl.rabobank.pirates.core.PokemonApiRestClient;
import nl.rabobank.pirates.core.PokemonService;
import nl.rabobank.pirates.domain.TurnInformation;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

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

        assert_application_doesnt_call_external_api_for_known_resource();
    }

    /**
     * BE VERY CAREFUL TO NOT OVERLY CALL THE API OR ELSE THEY MAY BAN U BY IP
     */
    private void assert_application_doesnt_call_external_api_for_known_resource() {
        Mockito.verify(apiRestClient, Mockito.atMost(1)).getPokemonByName("charmander");
        Mockito.verify(apiRestClient, Mockito.atMost(1)).getMoveByName("growl");
    }

}
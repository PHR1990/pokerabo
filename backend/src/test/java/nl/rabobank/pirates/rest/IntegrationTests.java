package nl.rabobank.pirates.rest;

import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.core.PokemonService;
import nl.rabobank.pirates.domain.TurnInformation;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = PokemonService.class)
class IntegrationTests {

    @InjectMocks
    private PokemonService pokemonService;

    @Spy
    private RestTemplate restTemplate;

    /**
     * BE VERY CAREFUL TO NOT OVERLY CALL THE API OR ELSE THEY MAY BAN U BY IP
     */
    @Test
    public void when_same_pokemon_called_use_local_storage() {
        pokemonService.getPokemonByName("charmander", 5);
        pokemonService.getPokemonByName("charmander", 5);
        pokemonService.getPokemonByName("charmander", 5);

        Mockito.verify(restTemplate, Mockito.atMost(1)).getForObject("https://pokeapi.co/api/v2/pokemon/charmander", PokemonDto.class);
    }

    @Test
    public void when_execute_turn_is_called_must() {
        pokemonService.selectEnemyPokemonByName("charmander", 5);
        pokemonService.selectOwnPokemonByName("bulbasaur", 5);

        TurnInformation turnInformation = pokemonService.executeTurn();

    }
}
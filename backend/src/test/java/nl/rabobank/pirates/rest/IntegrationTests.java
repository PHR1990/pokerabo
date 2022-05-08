package nl.rabobank.pirates.rest;

import nl.rabobank.pirates.core.PokemonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PokemonService.class)
class PokemonControllerIT {

    @Autowired
    private PokemonService pokemonService;

    @Test
    public void test() throws Exception {
        pokemonService.getPokemonByName("charmander");

    }
}
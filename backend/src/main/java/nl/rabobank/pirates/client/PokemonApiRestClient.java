package nl.rabobank.pirates.client;

import nl.rabobank.pirates.client.move.MoveDto;
import nl.rabobank.pirates.client.pokemon.PokemonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PokemonApiRestClient {

    @Lazy
    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    private static final String POKEMON_URL = BASE_URL + "pokemon/";

    private static final String MOVE_URL = BASE_URL + "move/";

    public PokemonDto getPokemonByName(final String pokemonName) {
        return restTemplate.getForObject(POKEMON_URL + pokemonName, PokemonDto.class);
    }

    public MoveDto getMoveByName(final String moveName) {
        return restTemplate.getForObject(MOVE_URL + moveName,MoveDto.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

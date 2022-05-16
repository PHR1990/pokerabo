package nl.rabobank.pirates.service;

import nl.rabobank.pirates.client.PokemonApiRestClient;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.move.mock.MoveFactoryTest;
import nl.rabobank.pirates.pokemon.mock.PokemonFactoryTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class GetPokemonServiceTest {

    @InjectMocks
    @Spy
    private GetPokemonService pokemonService;

    @Spy
    private CalculationService calculationService;

    @Mock
    private GetMoveService moveService;

    @Mock
    private PokemonApiRestClient pokemonApiRestClient;

    @Test
    public void when_get_pokemon_return_model_with_calculated_stats() throws IOException {

        final String charmander = "Charmander";

        Mockito.when(pokemonApiRestClient.getPokemonByName(charmander)).thenReturn(PokemonFactoryTest.makeCharmanderDto());
        Mockito.when(moveService.getFourRandomMoves(Mockito.any(), Mockito.anyInt())).thenReturn(Arrays.asList(MoveFactoryTest.makeScratch()));

        Pokemon pokemon = pokemonService.getPokemonByName(charmander, 10);

        Assert.assertEquals(charmander.toUpperCase(), pokemon.getName());
        Assert.assertEquals(27, pokemon.getMaxHp());
        Assert.assertEquals(pokemon.getMaxHp(), pokemon.getCurrentHp());
        Assert.assertEquals(10, pokemon.getLevel());
        Assert.assertEquals(15, pokemon.getStatAmount(Stat.ATTACK));
        Assert.assertEquals(13, pokemon.getStatAmount(Stat.DEFENSE));
        Assert.assertEquals(17, pokemon.getStatAmount(Stat.SPECIAL_ATTACK));
        Assert.assertEquals(15, pokemon.getStatAmount(Stat.SPECIAL_DEFENSE));
        Assert.assertEquals(18, pokemon.getStatAmount(Stat.SPEED));
    }

}
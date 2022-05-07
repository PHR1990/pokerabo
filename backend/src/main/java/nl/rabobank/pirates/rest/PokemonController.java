package nl.rabobank.pirates.rest;

import nl.rabobank.pirates.core.PokemonService;
import nl.rabobank.pirates.domain.Pokemon;
import nl.rabobank.pirates.domain.TurnInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pokemon/")
@CrossOrigin
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;

    @GetMapping(path="{pokemonName}", produces = "application/json")
    public Pokemon getPokemon(@PathVariable final String pokemonName) {
        Pokemon pokemon = pokemonService.getPokemonByName(pokemonName);

        return pokemon;
    }

    @PostMapping(path="/own-pokemon/{pokemonName}", produces = "application/json")
    public Pokemon chooseOwnPokemon(@PathVariable final String pokemonName) {
        Pokemon pokemon = pokemonService.setOwnPokemonByName(pokemonName);
        return pokemon;
    }

    @PostMapping(path="/enemy-pokemon/{pokemonName}", produces = "application/json")
    public Pokemon chooseEnemyPokemon(@PathVariable final String pokemonName) {
        Pokemon pokemon = pokemonService.setEnemyPokemonByName(pokemonName);
        return pokemon;
    }

    @GetMapping(path="execute-turn", produces = "application/json")
    public TurnInformation executeTurn() {
        return pokemonService.executeTurn();
    }

    @GetMapping(path="/own-pokemon/", produces = "application/json")
    public Pokemon getOwnPokemon() {
        Pokemon pokemon = pokemonService.getCurrentOwnPokemon();
        return pokemon;
    }

    @GetMapping(path="/enemy-pokemon/", produces = "application/json")
    public Pokemon getEnemyPokemon() {
        Pokemon pokemon = pokemonService.getCurrentEnemyPokemon();
        return pokemon;
    }

}

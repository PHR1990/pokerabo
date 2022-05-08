package nl.rabobank.pirates.rest;

import nl.rabobank.pirates.core.BattleService;
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

    @Autowired
    private BattleService battleService;

    @GetMapping(path="{pokemonName}/level/{level}", produces = "application/json")
    public Pokemon getPokemonByName(@PathVariable final String pokemonName, @PathVariable final int level) {
        Pokemon pokemon = pokemonService.getPokemonByName(pokemonName, level);
        return pokemon;
    }
    @PostMapping(path="/own-pokemon/{pokemonName}/level/{level}", produces = "application/json")
    public Pokemon chooseOwnPokemon(@PathVariable final String pokemonName, @PathVariable final int level) {
        Pokemon pokemon = battleService.selectOwnPokemonByName(pokemonName, level);
        return pokemon;
    }
    @PostMapping(path="/enemy-pokemon/{pokemonName}/level/{level}", produces = "application/json")
    public Pokemon chooseEnemyPokemon(@PathVariable final String pokemonName, @PathVariable final int level) {
        Pokemon pokemon = battleService.selectEnemyPokemonByName(pokemonName, level);
        return pokemon;
    }
    @GetMapping(path="execute-turn", produces = "application/json")
    public TurnInformation executeTurn() {
        return battleService.executeTurn();
    }
    @GetMapping(path="/own-pokemon/", produces = "application/json")
    public Pokemon getSelectedOwnPokemon() {
        Pokemon pokemon = battleService.getCurrentOwnPokemon();
        return pokemon;
    }
    @GetMapping(path="/enemy-pokemon/", produces = "application/json")
    public Pokemon getSelectedEnemyPokemon() {
        Pokemon pokemon = battleService.getCurrentEnemyPokemon();
        return pokemon;
    }
}

package nl.rabobank.pirates.controller;

import nl.rabobank.pirates.service.BattleService;
import nl.rabobank.pirates.service.GetPokemonService;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.battle.TurnInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pokemon/")
@CrossOrigin
public class PokemonController {

    @Autowired
    private GetPokemonService pokemonService;

    @Autowired
    private BattleService battleService;

    @GetMapping(path="{pokemonName}/level/{level}", produces = "application/json")
    public Pokemon getPokemonByName(@PathVariable final String pokemonName, @PathVariable final int level) {
        return pokemonService.getPokemonByName(pokemonName, level);
    }
    @PostMapping(path="/own-pokemon/{pokemonName}/level/{level}", produces = "application/json")
    public Pokemon chooseOwnPokemon(@PathVariable final String pokemonName, @PathVariable final int level) {
        return battleService.selectOwnPokemonByName(pokemonName, level);
    }
    @PostMapping(path="/enemy-pokemon/{pokemonName}/level/{level}", produces = "application/json")
    public Pokemon chooseEnemyPokemon(@PathVariable final String pokemonName, @PathVariable final int level) {
        return battleService.selectEnemyPokemonByName(pokemonName, level);
    }
    @GetMapping(path="execute-turn", produces = "application/json")
    public TurnInformation executeTurn() {
        return battleService.executeTurn();
    }
    @GetMapping(path="/own-pokemon/", produces = "application/json")
    public Pokemon getSelectedOwnPokemon() {
        return battleService.getCurrentOwnPokemon();
    }
    @GetMapping(path="/enemy-pokemon/", produces = "application/json")
    public Pokemon getSelectedEnemyPokemon() {
        return battleService.getCurrentEnemyPokemon();
    }
}

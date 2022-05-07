package nl.rabobank.pirates.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PokemonBattleData {

    private Pokemon ownPokemon;
    private Pokemon enemyPokemon;
}

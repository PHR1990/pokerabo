package nl.rabobank.pirates.model.battle;

import lombok.Builder;
import lombok.Getter;
import nl.rabobank.pirates.model.common.Pokemon;

@Getter
@Builder
public class PokemonBattleData {
    private Pokemon ownPokemon;
    private Pokemon enemyPokemon;
}

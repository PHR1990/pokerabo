package nl.rabobank.pirates.handlers;

import java.util.List;

import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.move.DamageClass;
import nl.rabobank.pirates.model.move.Move;

public interface MoveHandler {
	
	public List<TurnAction> handleMove(final Move pokemonMove, final Pokemon attackingPokemon, final Pokemon defendingPokemon, boolean isOwnPokemonAttacking);

    public DamageClass getClassType();
}

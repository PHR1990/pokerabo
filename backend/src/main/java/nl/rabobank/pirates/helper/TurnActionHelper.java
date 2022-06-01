package nl.rabobank.pirates.helper;

import nl.rabobank.pirates.model.battle.TurnAction;

public class TurnActionHelper {
	
	// TODO move to TurnAction class?
	public static TurnAction.Subject getSubject(final boolean isOwnPokemonAttacking) {
		TurnAction.Subject subjectAttackingPokemon = isOwnPokemonAttacking ? TurnAction.Subject.OWN
				: TurnAction.Subject.ENEMY;
		return subjectAttackingPokemon;
	}
}

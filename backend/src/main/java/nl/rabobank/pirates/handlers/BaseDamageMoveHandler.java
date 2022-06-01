package nl.rabobank.pirates.handlers;

import static nl.rabobank.pirates.model.battle.TurnActionFactory.makeFaintAnimation;

import java.util.ArrayList;
import java.util.List;

import nl.rabobank.pirates.helper.TurnActionHelper;
import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionFactory;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.move.Move;

public abstract class BaseDamageMoveHandler extends BaseMoveHandler {

	public List<TurnAction> handleMove(Move pokemonMove, Pokemon attackingPokemon, Pokemon defendingPokemon,
			boolean isOwnPokemonAttacking) {

		final List<TurnAction> actions = new ArrayList<>();

		actions.add(TurnActionFactory.makePokemonUsedMove(attackingPokemon.getName().toUpperCase(),
				pokemonMove.getName(), TurnActionHelper.getSubject(isOwnPokemonAttacking)));

		if (!willMoveHit(pokemonMove, attackingPokemon, defendingPokemon)) {
			actions.add(TurnActionFactory.makeTextOnly("The attack missed!"));
			return actions;
		}

		// TODO calculate critical
		// TODO calculate weakness/strenghts
		
		actions.addAll(processNumberOfHits(pokemonMove, defendingPokemon, isOwnPokemonAttacking,
				determineDamage(pokemonMove, attackingPokemon, defendingPokemon)));

		actions.add(processFainting(defendingPokemon, isOwnPokemonAttacking));

		actions.add(processStatusEffect(defendingPokemon, pokemonMove, isOwnPokemonAttacking));

		return actions;
	}

	protected abstract int determineDamage(Move pokemonMove, Pokemon attackingPokemon, Pokemon defendingPokemon);

	private List<TurnAction> processNumberOfHits(final Move pokemonMove, final Pokemon defendingPokemon,
			final boolean isOwnPokemonAttacking, final int damage) {
		final List<TurnAction> actions = new ArrayList<>();

		int numberHits = calculationService.calculateNumberOfHitTimes(pokemonMove.getHitTimes());

		for (int x = 0; x < numberHits; x++) {

			defendingPokemon.dealDamage(damage);

			actions.add(TurnActionFactory.makeDamageOnlyAnimation(damage,
					TurnActionHelper.getSubject(isOwnPokemonAttacking)));
		}

		if (numberHits > 1) {
			actions.add(TurnActionFactory.makeTextHitXTimes(numberHits));
		}

		return actions;
	}

	public TurnAction processFainting(final Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {

		if (checkIfDefendingPokemonFainted(defendingPokemon)) {
			return makeFaintAnimation(defendingPokemon.getName().toUpperCase(),
					TurnActionHelper.getSubject(isOwnPokemonAttacking));
		}

		return null;
	}

	public boolean checkIfDefendingPokemonFainted(final Pokemon defendingPokemon) {
		return defendingPokemon.getCurrentHp() <= 0;
	}

}

package nl.rabobank.pirates.handlers;

import static nl.rabobank.pirates.model.move.StatusEffect.Condition.CONFUSED;
import static nl.rabobank.pirates.model.move.StatusEffect.Condition.SLEEP;

import org.springframework.beans.factory.annotation.Autowired;

import nl.rabobank.pirates.helper.TurnActionHelper;
import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionFactory;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.move.Move;
import nl.rabobank.pirates.service.CalculationService;

/**
 * Class containing methods for shared functionality across MoveHandlers.
 * 
 * @author Daan
 *
 */
public class BaseMoveHandler {

	@Autowired
	protected CalculationService calculationService;

	protected boolean willMoveHit(Move move, Pokemon attackingPokemon, Pokemon defendingPokemon) {
		int moveAccuracy = move.getAccuracy();
		int pokemonAccuracy = attackingPokemon.getStatAmount(Stat.ACCURACY);

		return calculationService.calculateAccuracyAndRollIfMoveHits(pokemonAccuracy, moveAccuracy);
	}
	
	protected TurnAction processStatusEffect(final Pokemon defendingPokemon, final Move pokemonMove,
			boolean isOwnPokemonAttacking) {

		if (pokemonMove.getStatusEffect() == null) {
			return null;
		}

		boolean isSuccessful = calculationService.isRollSuccessful(pokemonMove.getStatusEffect().getChance());
		if (!isSuccessful) {
			return null;
		}

		boolean canApplyStatusEffect = defendingPokemon.addStatusEffect(pokemonMove.getStatusEffect().getCondition());

		if (!canApplyStatusEffect) {
			return TurnActionFactory.makeTextOnly("The attack missed!");
		}
		if (SLEEP.equals(pokemonMove.getStatusEffect().getCondition())) {
			defendingPokemon.putPokemonToSleep(calculationService.randomSleepOrConfusedTurns());
		}
		if (CONFUSED.equals(pokemonMove.getStatusEffect().getCondition())) {
			defendingPokemon.confusePokemon(calculationService.randomSleepOrConfusedTurns());
		}

		return TurnActionFactory.makeStatusEffect(defendingPokemon.getName(),
				pokemonMove.getStatusEffect().getCondition(), TurnActionHelper.getSubject(isOwnPokemonAttacking));
	}

}

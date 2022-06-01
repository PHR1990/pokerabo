package nl.rabobank.pirates.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import nl.rabobank.pirates.helper.TurnActionHelper;
import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.battle.TurnActionFactory;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.common.StatChange;
import nl.rabobank.pirates.model.common.StatMultiplier;
import nl.rabobank.pirates.model.move.DamageClass;
import nl.rabobank.pirates.model.move.Move;

@Component
public class StatusMoveHandler extends BaseMoveHandler implements MoveHandler {

	@Override
	public List<TurnAction> handleMove(Move pokemonMove, Pokemon attackingPokemon,
			Pokemon defendingPokemon, boolean isOwnPokemonAttacking) {
		
		final List<TurnAction> actions = new ArrayList<>();
		
		actions.add(TurnActionFactory.makePokemonUsedMove(attackingPokemon.getName().toUpperCase(), pokemonMove.getName(), TurnActionHelper.getSubject(isOwnPokemonAttacking)));

        if (!willMoveHit(pokemonMove, attackingPokemon, defendingPokemon)) {
            actions.add(TurnActionFactory.makeTextOnly("The attack missed!"));
            return actions;
        }
        actions.addAll(processMoveStatChanges(attackingPokemon, defendingPokemon, pokemonMove));

        actions.add(processStatusEffect(defendingPokemon, pokemonMove, isOwnPokemonAttacking));
        
        return actions;
	}

	private List<TurnAction> processMoveStatChanges(final Pokemon attackingPokemon, final Pokemon defendingPokemon,
			final Move pokemonMove) {
		final List<TurnAction> actions = new ArrayList<>();
		
		for (StatChange statChange : pokemonMove.getStatChanges()) {

			if (statChange.getChangeAmount() > 0) {

				boolean wasModified = attackingPokemon.addStatMultiplier(StatMultiplier.builder()
						.stat(statChange.getStat()).stageModification(statChange.getChangeAmount()).build());

				if (wasModified) {
					actions.add(TurnActionFactory.makeTextStatRose(attackingPokemon.getName(),
							statChange.getStat().getLabel()));

				} else {
					actions.add(TurnActionFactory.makeTextStatWontRaiseHigher(attackingPokemon.getName(),
							statChange.getStat().getLabel()));

				}

			} else {
				boolean wasModified = defendingPokemon.addStatMultiplier(StatMultiplier.builder()
						.stat(statChange.getStat()).stageModification(statChange.getChangeAmount()).build());
				if (wasModified) {

					actions.add(TurnActionFactory.makeTextStatFell(defendingPokemon.getName(),
							statChange.getStat().getLabel()));
				} else {
					actions.add(TurnActionFactory.makeTextStatWontFallLower(defendingPokemon.getName(),
							statChange.getStat().getLabel()));
				}
			}

		}
		
		return actions;
	}

	@Override
	public DamageClass getClassType() {
		return DamageClass.STATUS;
	}
}

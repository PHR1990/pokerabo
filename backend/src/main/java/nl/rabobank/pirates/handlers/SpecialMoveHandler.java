package nl.rabobank.pirates.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.move.DamageClass;
import nl.rabobank.pirates.model.move.Move;
import nl.rabobank.pirates.service.CalculationService;

@Component
public class SpecialMoveHandler extends BaseDamageMoveHandler implements MoveHandler {

	@Autowired
	private CalculationService calculationService;

	@Override
	public DamageClass getClassType() {
		return DamageClass.SPECIAL;
	}

	@Override
	public int determineDamage(Move pokemonMove, Pokemon attackingPokemon, Pokemon defendingPokemon) {
		return calculationService.calculateSpecialDamage(attackingPokemon, defendingPokemon, pokemonMove);
	}
	
}

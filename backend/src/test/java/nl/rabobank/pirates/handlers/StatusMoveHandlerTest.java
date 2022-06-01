package nl.rabobank.pirates.handlers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.move.DamageClass;
import nl.rabobank.pirates.model.move.Move;
import nl.rabobank.pirates.service.BattleService;
import nl.rabobank.pirates.service.CalculationService;
import nl.rabobank.pirates.smoke.TestConfig;

@SpringBootTest(classes = TestConfig.class)
public class StatusMoveHandlerTest {
	
	@Autowired
	private BattleService battleService;
	
	@Autowired
	private StatusMoveHandler statusMoveHandler;
	
	@Test
    public void verify_number_of_actions() {
		Pokemon currentEnemyPokemon = battleService.selectEnemyPokemonByName("charmander", 5);
		Pokemon currentOwnPokemon = battleService.selectOwnPokemonByName("bulbasaur", 5);

		Move ownPokemonStatusMove = currentOwnPokemon.getMoves().get(1); // Status.growl
		
		List<TurnAction> actions = statusMoveHandler.handleMove(ownPokemonStatusMove, currentOwnPokemon, currentEnemyPokemon, true);
		
		assertEquals(3, actions.size());
	}
}

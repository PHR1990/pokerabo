package nl.rabobank.pirates.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import nl.rabobank.pirates.model.battle.TurnAction;
import nl.rabobank.pirates.model.common.Pokemon;
import nl.rabobank.pirates.model.move.Move;
import nl.rabobank.pirates.smoke.TestConfig;

@SpringBootTest(classes = TestConfig.class)
class TurnInformationServiceTest {
	@Autowired
	private BattleService battleService;

	@SpyBean
	private TurnInformationService turnInformationService;

	@Test
	public void verify_number_of_actions() {
		final List<TurnAction> actions = new ArrayList<>();
		
		Pokemon currentEnemyPokemon = battleService.selectEnemyPokemonByName("charmander", 5);
		Pokemon currentOwnPokemon = battleService.selectOwnPokemonByName("bulbasaur", 5);

		Move ownPokemonFirstMove = currentOwnPokemon.getMoves().get(0); // Physical.tackle
		Move enemyPokemonFirstMove = currentEnemyPokemon.getMoves().get(0); // Physical.scratch
		Move ownPokemonSecondMove = currentOwnPokemon.getMoves().get(1); // Status.growl
		Move enemyPokemonSecondMove = currentEnemyPokemon.getMoves().get(1); // Status.growl
		
		//TODO: split up in separate tests
		turnInformationService.processMoveAndAddToActions(actions, ownPokemonFirstMove, currentOwnPokemon,
				currentEnemyPokemon, true);

		assertThat(actions.size()).isEqualTo(4);
		
		// start anew with actions
		actions.clear();
		
		turnInformationService.processMoveAndAddToActions(actions, enemyPokemonFirstMove, currentEnemyPokemon,
				currentOwnPokemon, false);

		
		assertThat(actions.size()).isEqualTo(4);
		
		// start anew with actions
		actions.clear();
		
		turnInformationService.processMoveAndAddToActions(actions, ownPokemonSecondMove, currentOwnPokemon,
				currentEnemyPokemon, true);

		assertThat(actions.size()).isEqualTo(3);
		
		// start anew with actions
		actions.clear();
		
		turnInformationService.processMoveAndAddToActions(actions, enemyPokemonSecondMove, currentEnemyPokemon,
				currentOwnPokemon, false);
		
		assertThat(actions.size()).isEqualTo(3);
		
	}
}
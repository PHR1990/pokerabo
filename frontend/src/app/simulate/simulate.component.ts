import {Component, OnDestroy, OnInit} from '@angular/core';
import {PokemonService} from '../pokemon.service';
import {Pokemon, TurnActionType, TurnInformation} from './pokemon.interface';
import {forkJoin} from 'rxjs';

@Component({
  selector: 'app-plate',
  templateUrl: './simulate.component.html',
  styleUrls: ['./simulate.component.css']
})
export class SimulateComponent implements OnInit, OnDestroy {

  pokemonDataLoaded = false;

  enemyPokemonData: Pokemon;
  ownPokemonData: Pokemon;

  chosenOwnPokemonIndex = 0;
  chosenEnemyPokemonIndex = 1;

  private possiblePokemon = [
    'charmander', 'squirtle', 'pikachu', 'pidgey', 'mankey', 'beedrill',
    'charmeleon', 'wartortle', 'raichu', 'jolteon', 'flareon','sandschrew', 'sandslash','cubone','marowak',
    'vaporeon', 'kadabra', 'hitmonchan', 'hitmonlee', 'graveler', 'golem',
    'primeape', 'blastoise', 'charizard', "gyarados", "nidorino", "nidoqueen", "nidoran-m", "nidoran-f", "clefairy"
  ];

  ownPokemonLevel = 5;
  enemyPokemonLevel = 5;

  restartBattle = false;

  turnInformation: TurnInformation;

  text;

  messagesLeftToDisplay = 0;

  constructor(private pokemonService: PokemonService) { }
  ngOnInit(): void {
    this.selectPokemonAndStartBattle();
  }
  selectPokemonAndStartBattle(): void {
    let observables: Promise<Pokemon>[];
    observables =
      [this.pokemonService.chooseOwnPokemon(this.possiblePokemon[this.chosenOwnPokemonIndex], this.ownPokemonLevel),
        this.pokemonService.chooseEnemyPokemon(this.possiblePokemon[this.chosenEnemyPokemonIndex], this.enemyPokemonLevel)];

    forkJoin(observables).subscribe(res => {
      this.ownPokemonData = res[0];
      this.enemyPokemonData = res[1];
      this.text = this.enemyPokemonData.name + ' wants to battle!';
      this.pokemonDataLoaded = true;
    });
  }
  executeTurn(): void {
    if (this.restartBattle) {
      this.restartBattle = false;
      this.selectPokemonAndStartBattle();
      return;
    }
    if (this.messagesLeftToDisplay === 0) {
      this.pokemonService.executeTurn().then(res => {
        this.turnInformation = res;
        this.animateTurnAndDisplayTexts();
      });
    }
  }
  animateTurnAndDisplayTexts(): void {
    // Create A unit test for this. How can he guarantee that the butotn is only pressed once for every batch
    let timeMultiplier = 1;

    this.messagesLeftToDisplay = this.turnInformation.actions.length;
    const that = this;
    this.turnInformation.actions.forEach(action => {

      timeMultiplier++;

      setTimeout(() => {
        that.messagesLeftToDisplay--;
        if (action.type === TurnActionType.TEXT_ONLY) {
          that.text = action.text;
        } else if (action.type === TurnActionType.DAMAGE_ANIMATION_AGAINST_OWN) {
          that.ownPokemonData.currentHp -= action.damage;
        } else if (action.type === TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY) {
          that.enemyPokemonData.currentHp -= action.damage;
        } else if (action.type === TurnActionType.FAINT_ANIMATION_OWN_POKEMON ||
          action.type === TurnActionType.FAINT_ANIMATION_ENEMY_POKEMON){
          that.text = action.text;
          this.restartBattle = true;
        }

      }, timeMultiplier * 750);
    });
  }
  updateEnemyPokemonHp(): Promise<void> {
    return this.pokemonService.getEnemyPokemon().then(pokeRes => {
      this.enemyPokemonData.currentHp = pokeRes.currentHp;
    });
  }
  updateOwnPokemonHp(): Promise<void> {
    return this.pokemonService.getOwnPokemon().then(pokeRes => {
      this.ownPokemonData.currentHp = pokeRes.currentHp;
    });
  }

  getEnemyPokemonCurrentHpAsPercentage(): number {
    if (!this.enemyPokemonData) {
      return 0;
    }
    const percentage = (this.enemyPokemonData.currentHp * 100) / this.enemyPokemonData.maxHp;
    return percentage;
  }
  getOwnPokemonCurrentHpAsPercentage(): number {
    if (!this.ownPokemonData) {
      return 0;
    }
    const percentage = (this.ownPokemonData.currentHp * 100) / this.ownPokemonData.maxHp;
    return percentage;
  }

  increaseOwnPokemonLevel() {
    if (this.ownPokemonLevel === 100) { return; }
    this.restartBattle = true;
    this.ownPokemonLevel += 5;
  }
  decreaseOwnPokemonLevel() {
    if (this.ownPokemonLevel === 5) { return; }
    this.restartBattle = true;
    this.ownPokemonLevel -= 5;
  }
  increaseEnemyPokemonLevel() {
    if (this.enemyPokemonLevel === 100) { return; }
    this.restartBattle = true;
    this.enemyPokemonLevel += 5;
  }
  decreaseEnemyPokemonLevel() {
    if (this.enemyPokemonLevel === 5) { return; }
    this.restartBattle = true;
    this.enemyPokemonLevel -= 5;
  }
  selectNextOwnPokemon() {
    if (this.chosenOwnPokemonIndex === this.possiblePokemon.length - 1) {
      this.chosenOwnPokemonIndex = 0;
    } else {
      this.chosenOwnPokemonIndex++;
    }
    this.restartBattle = true;
    this.updateMessageUponNewPokemonSelection(this.chosenOwnPokemonIndex);
  }
  selectPreviousOwnPokemon() {
    if (this.chosenOwnPokemonIndex === 0) {
      this.chosenOwnPokemonIndex = this.possiblePokemon.length - 1;
    } else {
      this.chosenOwnPokemonIndex--;
    }
    this.restartBattle = true;
    this.updateMessageUponNewPokemonSelection(this.chosenOwnPokemonIndex);
  }
  selectNextEnemyPokemon() {
    if (this.chosenEnemyPokemonIndex === this.possiblePokemon.length - 1) {
      this.chosenEnemyPokemonIndex = 0;
    } else {
      this.chosenEnemyPokemonIndex++;
    }
    this.restartBattle = true;
    this.updateMessageUponNewPokemonSelection(this.chosenEnemyPokemonIndex);
  }
  selectPreviousEnemyPokemon() {
    if (this.chosenEnemyPokemonIndex === 0) {
      this.chosenEnemyPokemonIndex = this.possiblePokemon.length - 1;
    } else {
      this.chosenEnemyPokemonIndex--;
    }
    this.restartBattle = true;
    this.updateMessageUponNewPokemonSelection(this.chosenEnemyPokemonIndex);
  }
  private updateMessageUponNewPokemonSelection(pokemonIndex) {
    this.text = 'selected pokemon will be ' + this.possiblePokemon[pokemonIndex];
  }

  ngOnDestroy(): void {
  }
}

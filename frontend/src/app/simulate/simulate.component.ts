import {Component, OnDestroy, OnInit} from '@angular/core';
import {PokemonService} from '../pokemon.service';
import {Pokemon} from './pokemon.interface';
import {interval, Subscription} from 'rxjs';
import {startWith, switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-plate',
  templateUrl: './simulate.component.html',
  styleUrls: ['./simulate.component.css']
})
export class SimulateComponent implements OnInit, OnDestroy {

  pokemonDataLoaded = false;
  timeInterval: Subscription;
  enemyPokemonData: Pokemon;
  ownPokemonData: Pokemon;

  text;
/*
this.pokemonService.executeTurn().then(res => {
      console.log(res);
    });
 */
  constructor(private pokemonService: PokemonService) { }
  ngOnInit(): void {
    this.timeInterval = interval(1000)
      .pipe(
        startWith(0),
        switchMap(() => this.pokemonService.executeTurn()))
      .subscribe(res => {
        if (!this.ownPokemonData || this.ownPokemonData.currentHp <= 0) {
          return;
        }
        console.log('turn data', res);
        for (const x in res.actions) {
          this.text = res.actions[x].text;
        }
        this.pokemonService.getOwnPokemon().then(pokeRes => {
          this.ownPokemonData.currentHp = pokeRes.currentHp;
        });
        this.pokemonService.getEnemyPokemon().then(pokeRes => {
          this.enemyPokemonData.currentHp = pokeRes.currentHp;
        });
      }, err => console.log('error', err));

    this.pokemonService.chooseOwnPokemon('blastoise')
      .then(res => {
        this.ownPokemonData = res;
        this.pokemonService.chooseEnemyPokemon('charizard').then(enemy => {
          this.enemyPokemonData = enemy;
          this.text = enemy.name + ' wants to battle!';
          this.pokemonDataLoaded = true;
        });

    });
    /*
    this.pokemonService.executeTurn().then(res => {
      console.log(res);
    });
     */
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

  ngOnDestroy(): void {
    this.timeInterval.unsubscribe();
  }

}

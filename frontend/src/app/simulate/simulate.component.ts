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

  constructor(private pokemonService: PokemonService) { }
  ngOnInit(): void {
    this.timeInterval = interval(1000)
      .pipe(
        startWith(0),
        switchMap(() => this.pokemonService.getPokemon('charmander')))
      .subscribe(res => {
        if (!this.ownPokemonData || this.ownPokemonData.currentHp <= 0) {
          return;
        }
        this.ownPokemonData.currentHp -= 1;
      }, err => console.log('error', err));



    this.pokemonService.getPokemon('charmander')
      .then(res => {
        this.ownPokemonData = res;
        this.pokemonService.getPokemon('squirtle').then(enemy => {
          this.enemyPokemonData = enemy;
          this.text = enemy.name + ' wants to battle!';
          this.pokemonDataLoaded = true;
          console.log('own', this.ownPokemonData);
          console.log('enemy', this.enemyPokemonData);
        });

    });
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

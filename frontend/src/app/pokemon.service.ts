import { Injectable } from '@angular/core';
import {Pokemon, TurnInformation} from './simulate/pokemon.interface';

@Injectable({
  providedIn: 'root'
})
export class PokemonService {
  constructor() { }

  getOwnPokemon(): Promise<Pokemon> {
    return fetch('http://localhost:8080/api/pokemon/own-pokemon/')
      .then(res => res.json())
      .then(res => {
        return res as Pokemon;
      });
  }
  getEnemyPokemon(): Promise<Pokemon> {
    return fetch('http://localhost:8080/api/pokemon/enemy-pokemon/')
      .then(res => res.json())
      .then(res => {
        return res as Pokemon;
      });
  }
  chooseOwnPokemon(pokemonName: string, level: number = 5): Promise<Pokemon> {
    return fetch('http://localhost:8080/api/pokemon/own-pokemon/' + pokemonName + '/level/' + level, {method: 'POST'})
      .then(res => res.json())
      .then(res => {
      console.log('Chose own pokemon', res);
      return res as Pokemon;
    });
  }

  chooseEnemyPokemon(pokemonName: string, level: number = 5): Promise<Pokemon> {
    return fetch('http://localhost:8080/api/pokemon/enemy-pokemon/' + pokemonName + '/level/' + level, {method: 'POST'})
      .then(res => res.json())
      .then(res => {
        return res as Pokemon;
      });
  }

  executeTurn(): Promise<TurnInformation> {
    return fetch('http://localhost:8080/api/pokemon/execute-turn')
      .then(res => res.json())
      .then(res => {
        return res as TurnInformation;
      });
  }
}

import { Injectable } from '@angular/core';
import {Pokemon} from './simulate/pokemon.interface';

@Injectable({
  providedIn: 'root'
})
export class PokemonService {
  constructor() { }

  getPokemon(pokemonName: string): Promise<Pokemon> {
    return fetch('http://localhost:8080/api/pokemon/' + pokemonName)
      .then(res => res.json())
      .then(res => {
      return res as Pokemon;
    });
  }
}

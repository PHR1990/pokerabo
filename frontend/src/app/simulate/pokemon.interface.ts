export interface Pokemon {
  name: string;
  maxHp: number;
  currentHp: number;
  attack: number;
  defense: number;
  specialAttack: number;
  specialDefense: number;
  speed: number;
  primaryType: Type;
  secondaryType: Type;
  backSpriteUrl: any;
  frontSpriteUrl: any;
  moves: Move[];
}

export interface Move {
  name: string;
  power: number;
  accuracy: number;
  effects: Effect;
  type: Type;
}

export enum Effect {
  Damage = 0, LowerAttackOneStage = 1, LowerDefenseOneStage = 2
}

export enum Type {
  None = 0, Fire = 1, Water = 2, Grass = 3
}

export interface TurnInformation {
  actions: TurnAction[];
}

export interface TurnAction {
  text: string;
  damage: number;
  type: TurnActionType;
}

export enum TurnActionType {
  TEXT_ONLY = 'TEXT_ONLY',
  DAMAGE_ANIMATION_AGAINST_OWN = 'DAMAGE_ANIMATION_AGAINST_OWN',
  DAMAGE_ANIMATION_AGAINST_ENEMY = 'DAMAGE_ANIMATION_AGAINST_ENEMY' ,
  STAT_EFFECT = 'STAT_EFFECT'
}



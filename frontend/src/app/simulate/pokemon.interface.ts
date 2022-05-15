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
  statusEffects:StatusEffect[]
}
export interface StatusEffect {
  condition: Condition;
  chance: number;
}

export enum Condition {
  NONE = 'NONE', BURN = 'BURN', POISON = 'POISON', BADLY_POISONED = 'BADLY_POISONED', SLEEP = 'SLEEP', PARALYZED= 'PARALYZED'
  , FROZEN= 'FROZEN', CONFUSED= 'CONFUSED', SEEDED = 'SEEDED'
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
  statusEffectCondition: Condition;
  subject: Subject
}

export enum Subject {
  NONE= 'NONE', OWN='OWN', ENEMY='ENEMY'
}

export enum TurnActionType {
  TEXT_ONLY = 'TEXT_ONLY',
  DAMAGE_ANIMATION = 'DAMAGE_ANIMATION',
  STAT_EFFECT = 'STAT_EFFECT',
  FAINT_ANIMATION = 'FAINT_ANIMATION',
}



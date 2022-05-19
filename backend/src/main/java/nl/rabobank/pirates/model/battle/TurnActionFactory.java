package nl.rabobank.pirates.model.battle;

import nl.rabobank.pirates.model.move.StatusEffect;

import static nl.rabobank.pirates.model.move.StatusEffect.Condition.NONE;

public class TurnActionFactory {

    static final String WHAT_WILL_POKEMON_DO = "What will %s do?";
    static final String POKEMON_IS_HURT_BY_ITS_POISON = "%s is hurt by poison!";
    static final String POKEMON_IS_HURT_BY_ITS_BURN = "%s is hurt by its burn!";
    static final String POKEMON_FAINTED = "%s fainted!";
    static final String POKEMON_USED_MOVE = "%s used %s";
    static final String POKEMON_STAT_ROSE = "%s %s rose!";
    static final String POKEMON_STAT_WONT_GO_ANY_HIGHER = "%s %s won't go any higher!";
    static final String POKEMON_STAT_FELL = "%s %s fell!";
    static final String POKEMON_STAT_WONT_GO_ANY_LOWER = "%s %s won't go any lower!";
    static final String HIT_THE_ENEMY_TIMES = "Hit the enemy %d times!";
    static final String POKEMON_WAS_POISONED = "%s was poisoned!";
    static final String POKEMON_WAS_BURNED = "%s was burned!";
    static final String POKEMON_WAS_PARALYZED = "%s was paralyzed!";
    static final String POKEMON_IS_ASLEEP = "%s is fast asleep!";
    static final String POKEMON_WOKE_UP = "%s woke up!";
    static final String POKEMON_IS_FULLY_PARALYZED = "%s is fully paralyzed!";
    static final String POKEMON_IS_CONFUSED = "%s is confused!";
    static final String POKEMON_HURT_ITSELF_IN_CONFUSION = "It hurt itself in its confusion!";
    static final String POKEMON_IS_NO_LONGER_CONFUSED = "%s snapped out of its confusion!";

    public static TurnAction makeWhatWillPokemonDo(final String pokemonName) {
        return TurnAction.builder()
                .text(String.format(WHAT_WILL_POKEMON_DO, pokemonName.toUpperCase()))
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    public static TurnAction makeWithTextPokemonIsHurtByItsBurn(final String pokemonName, TurnAction.Subject target) {

        final String messageToFormat = appendableStringTheFoes(target) + POKEMON_IS_HURT_BY_ITS_BURN;

        return TurnAction.builder()
                .text(String.format(messageToFormat, pokemonName.toUpperCase()))
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    public static TurnAction makeWithTextPokemonIsHurtByPoison(final String pokemonName, TurnAction.Subject target) {

        final String messageToFormat = appendableStringTheFoes(target) + POKEMON_IS_HURT_BY_ITS_POISON;

        return TurnAction.builder()
                .text(String.format(messageToFormat, pokemonName.toUpperCase()))
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    public static TurnAction makeDamageOnlyAnimation(final int damage, final TurnAction.Subject subject) {
        return TurnAction.builder()
                .damage(damage)
                .subject(subject)
                .type(TurnActionType.DAMAGE_ANIMATION)
                .build();
    }

    public static TurnAction makeTextOnly(final String text) {
        return TurnAction.builder()
                .text(text)
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    public static TurnAction makeTextStatRose(final String pokemonName, final String stat) {
        return makeTextOnly(String.format(POKEMON_STAT_ROSE, pokemonName.toUpperCase(), stat.toUpperCase()));
    }

    public static TurnAction makeTextStatWontRaiseHigher(final String pokemonName, final String stat) {
        return makeTextOnly(String.format(POKEMON_STAT_WONT_GO_ANY_HIGHER, pokemonName.toUpperCase(), stat.toUpperCase()));
    }

    public static TurnAction makeTextStatFell(final String pokemonName, final String stat) {
        return makeTextOnly(String.format(POKEMON_STAT_FELL, pokemonName.toUpperCase(), stat.toUpperCase()));
    }

    public static TurnAction makeTextStatWontFallLower(final String pokemonName, final String stat) {
        return makeTextOnly(String.format(POKEMON_STAT_WONT_GO_ANY_LOWER, pokemonName.toUpperCase(), stat.toUpperCase()));
    }

    public static TurnAction makeTextHitXTimes(final int times) {
        return makeTextOnly(String.format(HIT_THE_ENEMY_TIMES, times));
    }


    public static TurnAction makeFaintAnimation(final String pokemonName, final TurnAction.Subject subject) {

        final String prefix = appendableStringFoe(subject);

        final String message = prefix + String.format(POKEMON_FAINTED, pokemonName.toUpperCase());

        return TurnAction.builder()
                .subject(subject)
                .text(message)
                .type(TurnActionType.FAINT_ANIMATION)
                .build();
    }

    public static TurnAction makeStatusEffect(final String pokemonName, final StatusEffect.Condition statusEffectCondition, final TurnAction.Subject subject) {

        TurnAction turnAction = TurnAction.builder()
                .statusEffectCondition(statusEffectCondition)
                .subject(subject)
                .type(TurnActionType.STAT_EFFECT)
                .build();

        if (statusEffectCondition.equals(StatusEffect.Condition.POISON)) {
            turnAction = turnAction.toBuilder().text(String.format(POKEMON_WAS_POISONED, pokemonName)).build();
        }
        if (statusEffectCondition.equals(StatusEffect.Condition.BURN)) {
            turnAction = turnAction.toBuilder().text(String.format(POKEMON_WAS_BURNED, pokemonName)).build();
        }
        if (statusEffectCondition.equals(StatusEffect.Condition.PARALYZED)) {
            turnAction = turnAction.toBuilder().text(String.format(POKEMON_WAS_PARALYZED, pokemonName)).build();
        }
        if (statusEffectCondition.equals(StatusEffect.Condition.SLEEP)) {
            turnAction = turnAction.toBuilder().text(String.format(POKEMON_IS_ASLEEP, pokemonName)).build();
        }
        if (statusEffectCondition.equals(StatusEffect.Condition.CONFUSED)) {
            turnAction = turnAction.toBuilder().text(String.format(POKEMON_IS_CONFUSED, pokemonName)).build();
        }
        return turnAction;
    }

    public static TurnAction makePokemonUsedMove(String pokemonName, String moveName, TurnAction.Subject subject) {
        final String prefix = appendableStringFoe(subject);

        final String moveUsedString = prefix + String.format(POKEMON_USED_MOVE, pokemonName.toUpperCase(), moveName.toUpperCase());

        return TurnAction.builder()
                .text(moveUsedString)
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    public static TurnAction makePokemonIsFullyParalyzed(String pokemonName) {

        final String message = String.format(POKEMON_IS_FULLY_PARALYZED, pokemonName.toUpperCase());

        return TurnAction.builder()
                .text(message)
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    public static TurnAction makePokemonIsStillAsleep(String pokemonName) {

        final String message = String.format(POKEMON_IS_ASLEEP, pokemonName.toUpperCase());

        return TurnAction.builder()
                .text(message)
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    public static TurnAction makePokemonHurtItselfInConfusion(final int damage, final TurnAction.Subject subject) {

        final String message = String.format(POKEMON_HURT_ITSELF_IN_CONFUSION);

        return TurnAction.builder()
                .text(message)
                .subject(subject)
                .type(TurnActionType.DAMAGE_ANIMATION)
                .damage(damage)
                .build();
    }

    public static TurnAction makePokemonWokeUp(String pokemonName, final TurnAction.Subject subject) {

        final String message = String.format(POKEMON_WOKE_UP, pokemonName.toUpperCase());

        return TurnAction.builder()
                .text(message)
                .statusEffectCondition(NONE)
                .subject(subject)
                .type(TurnActionType.STAT_EFFECT)
                .build();
    }

    public static TurnAction makePokemonSnappedOutOfConfusion(String pokemonName) {

        final String message = String.format(POKEMON_IS_NO_LONGER_CONFUSED, pokemonName.toUpperCase());

        return TurnAction.builder()
                .text(message)
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }



    private static String appendableStringFoe(TurnAction.Subject subject) {
        return subject.equals(TurnAction.Subject.ENEMY) ? "Foe " : "";
    }

    private static String appendableStringTheFoes(TurnAction.Subject target) {
        return target.equals(TurnAction.Subject.ENEMY)? "The foe's " : "";
    }
}

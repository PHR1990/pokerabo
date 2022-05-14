package nl.rabobank.pirates.model.battle;

import nl.rabobank.pirates.model.move.StatusEffect;

public class TurnActionFactory {

    static final String WHAT_WILL_POKEMON_DO = "what will %s do?";
    static final String POKEMON_IS_HURT_BY_ITS_POISON = "%s is hurt by poison!";
    static final String POKEMON_IS_HURT_BY_ITS_BURN = "%s is hurt by its burn!";

    public static TurnAction makeWhatWillPokemonDo(final String pokemonName) {
        return TurnAction.builder()
                .text(String.format(WHAT_WILL_POKEMON_DO, pokemonName.toUpperCase()))
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    public static TurnAction makeWithTextPokemonIsHurtByItsBurn(final String pokemonName, TurnAction.Subject target) {

        final String messageToFormat = appendingString(target) + POKEMON_IS_HURT_BY_ITS_BURN;

        return TurnAction.builder()
                .text(String.format(messageToFormat, pokemonName.toUpperCase()))
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    public static TurnAction makeWithTextPokemonIsHurtByPoison(final String pokemonName, TurnAction.Subject target) {

        final String messageToFormat = appendingString(target) + POKEMON_IS_HURT_BY_ITS_POISON;

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

    public static TurnAction makeFaintAnimation(final String pokemonName, final TurnAction.Subject subject) {

        final String prefix = getPrefix(subject);

        final String message = prefix + pokemonName + " fainted!";

        return TurnAction.builder()
                .subject(subject)
                .text(message)
                .type(TurnActionType.FAINT_ANIMATION)
                .build();
    }

    public static TurnAction makeStatusEffect(final StatusEffect statusEffect, final TurnAction.Subject subject) {
        return TurnAction.builder()
                .statusEffect(statusEffect)
                .subject(subject)
                .type(TurnActionType.STAT_EFFECT)
                .build();
    }

    public static TurnAction makePokemonUsedMove(String pokemonName, String moveName, TurnAction.Subject subject) {
        final String prefix = subject.equals(TurnAction.Subject.ENEMY) ? "Foe " : "";
        final String moveUsedString = prefix + pokemonName + " used " + moveName.toUpperCase();

        return TurnAction.builder()
                .text(moveUsedString)
                .type(TurnActionType.TEXT_ONLY)
                .build();
    }

    private static String getPrefix(TurnAction.Subject subject) {
        return subject.equals(TurnAction.Subject.ENEMY) ? "Foe " : "";
    }

    private static String appendingString(TurnAction.Subject target) {
        return target.equals(TurnAction.Subject.ENEMY)? "The foe's " : "";
    }
}

package nl.rabobank.pirates.core;

import lombok.Getter;
import nl.rabobank.pirates.client.common.Type;
import nl.rabobank.pirates.client.move.MoveDto;
import nl.rabobank.pirates.client.pokemon.*;
import nl.rabobank.pirates.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PokemonService {

    private Map<String, PokemonDto> pokemonStorage = new ConcurrentHashMap<>();
    private Map<String, MoveDto> moveStorage = new ConcurrentHashMap<>();

    @Getter
    private Pokemon currentOwnPokemon;
    @Getter
    private Pokemon currentEnemyPokemon;

    private static final String VERSION_GROUP = "red-blue";

    private AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    public Pokemon selectOwnPokemonByName(final String pokemonName, final int level) {
        currentOwnPokemon = getPokemonByName(pokemonName, level);
        return currentOwnPokemon;
    }

    public Pokemon selectEnemyPokemonByName(final String pokemonName, int level) {
        currentEnemyPokemon = getPokemonByName(pokemonName, level);
        return currentEnemyPokemon;
    }
    public Pokemon getPokemonByName(final String pokemonName, int level) {
        if (level == 0) level = 5;
        PokemonDto pokemonDto;
        if (pokemonStorage.containsKey(pokemonName)) {
            pokemonDto = pokemonStorage.get(pokemonName);
        } else {
            final String url = "https://pokeapi.co/api/v2/pokemon/" + pokemonName;
            pokemonDto = restTemplate.getForObject(url, PokemonDto.class);
        }
        pokemonStorage.put(pokemonName, pokemonDto);

        return Pokemon.builder()
                .name(pokemonDto.getName().toUpperCase())
                .backSpriteUrl(pokemonDto.getSprites().getBackDefault())
                .frontSpriteUrl(pokemonDto.getSprites().getFrontDefault())
                .maxHp(calculateMaxHp(level, pokemonDto))
                .currentHp(calculateMaxHp(level, pokemonDto))
                .stats(convertAndCalculateToStatsAmount(pokemonDto.getStats(), level))
                .moves(convertToMoves(getFourRandomMoves(level, pokemonDto)))
                .level(level)
                .build();
    }

    private List<StatAmount> convertAndCalculateToStatsAmount(List<StatDtoWrapper> statDtoWrapperList, int level) {
        List<StatAmount> statAmountList = new ArrayList<>();

        for (StatDtoWrapper statDtoWrapper : statDtoWrapperList) {
            int statAmount = calculateStat(level, statDtoWrapper.getBaseStat());
            statAmountList.add(
                    StatAmount.builder()
                            .amount(statAmount)
                            .stat(Stat.valueOfLabel(statDtoWrapper.getStat().getName()))
                            .build()
            );
        }

        return statAmountList;
    }

    public MoveDto getMoveByName(final String moveName) {
        if (moveStorage.containsKey(moveName)) {
            return moveStorage.get(moveName);
        }
        final String url = "https://pokeapi.co/api/v2/move/" + moveName;
        final MoveDto moveDto = restTemplate.getForObject(url, MoveDto.class);
        System.out.println("Current cash misses" + counter.incrementAndGet());
        moveStorage.put(moveName, moveDto);

        return moveDto;
    }

    public List<MoveDto> getFourRandomMoves(int level, PokemonDto pokemonDto) {
        List<ThinMoveDto> allPossibleMoves = new ArrayList<>();

        // Filter out moves by level
        for (ThinMoveWrapperDto thinMoveWrapperDto : pokemonDto.getMoves()) {
            for (VersionGroupDetailsDto versionGroupDetailsDto : thinMoveWrapperDto.getVersionGroupDetails()) {
                if (VERSION_GROUP.equals(versionGroupDetailsDto.getVersionGroup().getName())) {
                    if (versionGroupDetailsDto.getLevelLearnedAt() > 0 && versionGroupDetailsDto.getLevelLearnedAt() <= level) {
                        allPossibleMoves.add(thinMoveWrapperDto.getMove());
                    }
                    break;
                }
            }
        }

        List<MoveDto> moves = new ArrayList<>();

        // IF we have 4 or less
        if (allPossibleMoves.size() <= 4) {
            for (ThinMoveDto thinMoveDto : allPossibleMoves) {
                moves.add(getMoveByName(thinMoveDto.getName()));
            }
            return moves;
        }

        // Query each move to build the combination of four


        while (moves.size() < 4) {
            int randomNumber = getRandomValue(0, allPossibleMoves.size()-1);
            ThinMoveDto randomizedMove = allPossibleMoves.get(randomNumber);
            moves.add(getMoveByName(randomizedMove.getName()));
            allPossibleMoves.remove(randomNumber);
        }

        return moves;
    }

    private List<Move> convertToMoves(List<MoveDto> moveDtoList) {
        List<Move> moves = new ArrayList<>();
        for (MoveDto moveDto : moveDtoList) {
            moves.add(convertToMove(moveDto));
        }
        return moves;
    }

    private Move convertToMove(MoveDto moveDto) {
        Move move = Move.builder()
                .accuracy(moveDto.getAccuracy())
                .power(moveDto.getPower())
                .name(moveDto.getName())
                .build();
        if (moveDto.getTarget() != null) {
            Target target = Target.SELECTED_POKEMON;
            switch (moveDto.getTarget().getName()) {

                case "selected-pokemon":
                    target = Target.SELECTED_POKEMON;
                    break;
                case "all-opponents":
                    target = Target.SELECTED_POKEMON;
                    break;
                case "user":
                    target = Target.USER;
                    break;
            }
            move = move.toBuilder().target(target).build();
        }

        if (moveDto.getDamageClass() != null) {
            DamageClass damageClass = DamageClass.STATUS;
            switch (moveDto.getDamageClass().getName()) {
                case "physical":
                    damageClass = DamageClass.PHYSICAL;
                    break;
                case "special":
                    damageClass = DamageClass.SPECIAL;
                    break;
                case "status":
                    damageClass = DamageClass.STATUS;
                    break;
            }
            move = move.toBuilder().damageClass(damageClass).build();
        }


        if (moveDto.getStatChanges() != null) {
            // TODO add stat changes (it is a list of effects)
        }
        Type type = Type.NORMAL;
        switch(moveDto.getType().getName()) {
            case "normal":
                type = Type.NORMAL;
                break;
            case "water":
                type = Type.WATER;
                break;
            case "fire":
                type = Type.FIRE;
                break;
            case "electric":
                type = Type.ELECTRIC;
                break;
            case "grass":
                type = Type.GRASS;
                break;
        }

        return move.toBuilder().type(type).build();
    }

    /**
     * Stats Formula for gen3+
     * Stats = (floor(0.01 x (2 x Base) x Level) + 5)
     */
    private int calculateStat(int level, int baseStat) {
        return Math.round((float)Math.floor(
                0.01 * (2 * baseStat * level) + level + 5
        ));
    }


    /**
     * HP we will be using:
     * HP = floor(0.01 x (2 x Base) x Level) + Level + 10
     */
    private int calculateMaxHp(int level, PokemonDto pokemonDto) {

        for (StatDtoWrapper statDtoWrapper : pokemonDto.getStats()) {
            if ("hp".equals(statDtoWrapper.getStat().getName())) {
                return Math.round((float)Math.floor(
                        0.01 * (2 * Integer.valueOf(statDtoWrapper.getBaseStat()) * level) + level + 10
                ));
            }
        }

        throw new RuntimeException("HP BASE STAT WASNT FOUND");
    }
    public TurnInformation executeTurn() {
        if (currentOwnPokemon == null || currentEnemyPokemon == null) return null;
        // Pokemons must have been set before
        return executeTurn(currentOwnPokemon, currentEnemyPokemon);
    }
    private TurnInformation executeTurn(Pokemon ownPokemon, Pokemon enemyPokemon) {
        List<TurnAction> actions = new ArrayList<>();
        // each pokemon randomize their move choices
        int ownPokemonMoveIndex = getRandomValue(0, ownPokemon.getMoves().size());
        int enemyPokemonMoveIndex = getRandomValue(0, enemyPokemon.getMoves().size());
        Move ownPokemonMove = ownPokemon.getMoves().get(ownPokemonMoveIndex);
        Move enemyPokemonMove = enemyPokemon.getMoves().get(enemyPokemonMoveIndex);

        // Decide who goes first
        boolean ownPokemonGoesFirst = false;
        if (ownPokemon.getStatAmount(Stat.SPEED) > enemyPokemon.getStatAmount(Stat.SPEED)) {
            ownPokemonGoesFirst = true;
        }
        int damageAgainstEnemyPokemon = calculateDamage(ownPokemon.getLevel(), ownPokemonMove.getPower(), ownPokemon.getStatAmount(Stat.ATTACK), enemyPokemon.getStatAmount(Stat.DEFENSE));
        int damageAgainstOwnPokemon = calculateDamage(enemyPokemon.getLevel(), enemyPokemonMove.getPower(), enemyPokemon.getStatAmount(Stat.ATTACK), ownPokemon.getStatAmount(Stat.DEFENSE));
        if (ownPokemonGoesFirst) {
            // Execute own move against foe
            // calculate damage of own pokemon
            actions.add(TurnAction.builder()
                    .text(buildPokemonUsedMove(ownPokemon.getName(), ownPokemonMove.getName(), false))
                    .type(TurnActionType.TEXT_ONLY)
                    .build());

            enemyPokemon.dealDamage(damageAgainstEnemyPokemon);
            actions.add(TurnAction.builder()
                    .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY)
                    .damage(damageAgainstEnemyPokemon)
                    .build());
            // Execute enemy turn
            // Calculate their damage
            actions.add(TurnAction.builder()
                    .text(buildPokemonUsedMove(enemyPokemon.getName(), enemyPokemonMove.getName(), true))
                    .type(TurnActionType.TEXT_ONLY)
                    .build());

            ownPokemon.dealDamage(damageAgainstOwnPokemon);

            actions.add(TurnAction.builder()
                    .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_OWN)
                    .damage(damageAgainstOwnPokemon)
                    .build());
        } else {
            // Execute enemy turn
            // Calculate their damage
            actions.add(TurnAction.builder()
                            .text(buildPokemonUsedMove(enemyPokemon.getName(), enemyPokemonMove.getName(), true))
                            .type(TurnActionType.TEXT_ONLY)
                    .build());

            ownPokemon.dealDamage(damageAgainstOwnPokemon);

            actions.add(TurnAction.builder()
                            .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_OWN)
                            .damage(damageAgainstOwnPokemon)
                    .build());

            // Execute own move against foe
            // calculate damage of own pokemon
            actions.add(TurnAction.builder()
                            .text(buildPokemonUsedMove(ownPokemon.getName(), ownPokemonMove.getName(), false))
                            .type(TurnActionType.TEXT_ONLY)
                    .build());

            enemyPokemon.dealDamage(damageAgainstEnemyPokemon);
            actions.add(TurnAction.builder()
                        .type(TurnActionType.DAMAGE_ANIMATION_AGAINST_ENEMY)
                        .damage(damageAgainstEnemyPokemon)
                    .build());
        }

        actions.add(TurnAction.builder()
                .text("What will " + ownPokemon.getName().toUpperCase() + " do?")
                .type(TurnActionType.TEXT_ONLY)
                .build());

        return TurnInformation.builder().actions(actions).build();
    }

    private int calculateDamage(int level, int movePower, int attack, int defense) {
        if (movePower == 0) return 0;
        return Math.round(
                ((((level * 2)/5 + 2) * movePower * attack/defense)/50) + 2
        );
    }

    private String buildPokemonUsedMove(String pokemonName, String moveName, boolean isFoe) {
        String prefix = isFoe ? "Foe " : "";
        return prefix + pokemonName + " used " + moveName.toUpperCase();
    }

    private int getRandomValue(int rangeStart, int rangeEnd) {
        final Random random = new Random();
        return random.ints(rangeStart, rangeEnd).findFirst().getAsInt();
    }

}

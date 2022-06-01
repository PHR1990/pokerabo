package nl.rabobank.pirates.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.pirates.client.PokemonApiRestClient;
import nl.rabobank.pirates.client.move.EffectDto;
import nl.rabobank.pirates.model.common.Type;
import nl.rabobank.pirates.client.move.MoveDto;
import nl.rabobank.pirates.client.move.StatChangeDto;
import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.client.pokemon.ThinMoveDto;
import nl.rabobank.pirates.client.pokemon.ThinMoveWrapperDto;
import nl.rabobank.pirates.client.pokemon.VersionGroupDetailsDto;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatChange;
import nl.rabobank.pirates.model.move.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static nl.rabobank.pirates.model.move.StatusEffect.Condition.*;

/**
 * Several types of moves are not supported.
 * Basically only damage, status buffs/debuffs and accuracy for now.
 */
@Component
@RequiredArgsConstructor
public class GetMoveService {

    private final PokemonApiRestClient pokemonApiRestClient;

    private final RollService rollService;

    //DDiemer: this is useful, did not know about this class :)
    private final AtomicInteger counter = new AtomicInteger(0);

    private final Map<String, MoveDto> moveStorage = new ConcurrentHashMap<>();

    private static final String RED_BLUE_VERSION_GROUP = "red-blue";

    // Create a move set builder, find moves that would make sense for a particular pokemon.

    // Round robin from lowest level to highest
    // store locally move names that are known to do damage from highest to lowest and save their names
    
    //DDiemer: please refactor so that there is only one getFourRandomMoves method (same method with params reversed to prevent duplicate method is bad practice)
    //Make sure the method only does what it describes (determine and return 4 or less random moves for the given pokemon/level) and delegate the conversion part to another method
    //I would suggest creating mapper classes to convert a DTO into an entity which would clean up the code in general. There are libraries that can do this
    //e.g. 'modelmapper' or you could use reflection to copy the properties, but do so inside new mapper classes.
    public List<Move> getFourRandomMoves(PokemonDto pokemonDto, int level) {
        return convertToMoves(getFourRandomMoves(level, pokemonDto));
    }

    public MoveDto getMoveByName(final String moveName) {
        if (moveStorage.containsKey(moveName)) {
            return moveStorage.get(moveName);
        }
        final MoveDto moveDto = pokemonApiRestClient.getMoveByName(moveName);
        
        //DDiemer: use proper logging framework
        System.out.println("Current cash misses" + counter.incrementAndGet());
        moveStorage.put(moveName, moveDto);

        return moveDto;
    }

    private List<MoveDto> getFourRandomMoves(int level, PokemonDto pokemonDto) {
        List<ThinMoveDto> allPossibleMoves = new ArrayList<>();

        // Filter out moves by level
        for (ThinMoveWrapperDto thinMoveWrapperDto : pokemonDto.getMoves()) {
            if (isMoveAllowed(level,  thinMoveWrapperDto)) {
                allPossibleMoves.add(thinMoveWrapperDto.getMove());
            }
        }
        
        /* DDiemer: it could be that no moves are in the list of allPossibleMoves at this point and the program is frozen. Its a functional decision what should happen in this case: 
         * Either do no move, display action text like 'pokemon has no moves!' and let the other pokemon do it next move, but this could turn into a standoff when neither pokemon has moves.
         * Alternatively display error message to the user or throw an exception to let the dev / tester know more moves need to implemented (my preference in this case because
         * this situation is not part of the game play)  
         */
        
        /*  DDiemer: the below part is not technically doing anything random so it should not be part of this method. I suggest to split this method into 2 parts:
         * 1) 	a method capable of determining the list of supported moves (List<ThinMoveDto> getSupportedMoves(PokemonDto pokemonDto, int level))
         * 2) 	a method capable of random selecting X moves from a list (List<MoveDto> getRandomMoves(List<ThinMoveDto> allPossibleMoves, int numberOfMoves)) which will
         * 		take into account the case IF allPossibleMoves.size() <= numberOfMoves the randomization is skipped and allPossibleMoves is directly returned
         * Finally use the aforementioned to-be-created mapper class to convert the MoveDto items in the list to Move entities
         */
        List<MoveDto> moves = new ArrayList<>();
        // IF we have 4 or less
        //DDiemer: 4 is a 'magic number', please move to a class level constant (MAX_NUMBER_OF_MOVES) and replace all instances (although the method name in this case clarifies it a bit)
        if (allPossibleMoves.size() <= 4) {
            for (ThinMoveDto thinMoveDto : allPossibleMoves) {
                moves.add(getMoveByName(thinMoveDto.getName()));
            }
            return moves;
        }
        // Query each move to build the combination of four
        while (moves.size() < 4) {
            int randomNumber = rollService.getRandomValue(0, allPossibleMoves.size()-1);
            ThinMoveDto randomizedMove = allPossibleMoves.get(randomNumber);
            moves.add(getMoveByName(randomizedMove.getName()));
            allPossibleMoves.remove(randomNumber);
        }
        return moves;
    }

    private boolean isMoveAllowed(int level, ThinMoveWrapperDto thinMoveWrapperDto) {
        boolean isMoveAllowed = false;

        //DDiemer: not sure why MoveServiceConstants is an interface
        if (MoveServiceConstants.PROHIBITED_MOVES.contains(thinMoveWrapperDto.getMove().getName())) {
            return false;
        }

        for (VersionGroupDetailsDto versionGroupDetailsDto : thinMoveWrapperDto.getVersionGroupDetails()) {
        	/* DDiemer: not sure if this is supported with the API (I only briefly checked the documentation) but
        	 * please check if it is possible to provide the versionGroup as a query parameter. No need to retrieve
        	 * data that will never be used and it keeps the API calls smaller
        	 */
            if (RED_BLUE_VERSION_GROUP.equals(versionGroupDetailsDto.getVersionGroup().getName())) {
                if (isMoveLearnedWithoutLevel(versionGroupDetailsDto) &&
                        versionGroupDetailsDto.getLevelLearnedAt() <= level) {
                    isMoveAllowed = true;
                } else {
                    return false;
                }

            }
        }

        return isMoveAllowed;
    }

    private boolean isMoveLearnedWithoutLevel(VersionGroupDetailsDto versionGroupDetailsDto) {
        return versionGroupDetailsDto.getLevelLearnedAt() > 0;
    }

    private List<Move> convertToMoves(List<MoveDto> moveDtoList) {
        List<Move> moves = new ArrayList<>();
        for (MoveDto moveDto : moveDtoList) {
            moves.add(convertToMove(moveDto));
        }
        return moves;
    }

    /* DDiemer: as suggested do this in mapper classes. Looking at the additional logic required to create the Move entity 
     * a custom-written mapper class is more suitable than using a model mapper API or reflection 
     */
    private Move convertToMove(MoveDto moveDto) {
        Move move = Move.builder()
                .accuracy(moveDto.getAccuracy())
                .power(moveDto.getPower())
                .name(moveDto.getName())
                .build();
        if (moveDto.getTarget() != null) {
            Target target = switch (moveDto.getTarget().getName()) {
                case "selected-pokemon", "all-opponents", "all-other-pokemon", "random-opponent" -> Target.SELECTED_POKEMON;
                case "user", "users-field" -> Target.USER;
                default -> throw new RuntimeException("unknown target" + moveDto.getTarget().getName() + " move = " + moveDto.getName());
            };
            move = move.toBuilder().target(target).build();
        }

        if (moveDto.getDamageClass() != null) {
            DamageClass damageClass = switch (moveDto.getDamageClass().getName()) {
                case "physical" -> DamageClass.PHYSICAL;
                case "special" -> DamageClass.SPECIAL;
                case "status" -> DamageClass.STATUS;
                default -> throw new RuntimeException("unknown damage class" + moveDto.getDamageClass().getName());
            };
            move = move.toBuilder().damageClass(damageClass).build();
        }

        if (moveDto.getStatChanges() != null) {
            List<StatChange> statChanges = new ArrayList<>();
            for (StatChangeDto statChangeDto : moveDto.getStatChanges()) {
                statChanges.add(
                        StatChange.builder().changeAmount(statChangeDto.getChange())
                                .stat(Stat.valueOfLabel(statChangeDto.getStat().getName()))
                                .build()
                );
            }
            move = move.toBuilder().statChanges(statChanges).build();
        }

        move = move.toBuilder()
        		//DDiemer: watch out for NPEs, not sure if you can always trust the API to return EffectEntries for all moves
                .hitTimes(convertHitTimes(moveDto.getEffectEntries().get(0)))
                .statusEffect(convertStatusEffect(moveDto))
            .build();

        return move.toBuilder().type(Type.valueOfLabel(moveDto.getName())).build();
    }

    /*
    Refactor for statusEffect to contain an effect percentage.
     */
    /* DDiemer: The below two methods should not be part of this class but moved to StatusEffectMapper / HitTimesMapper or a factory.
     * Additionally both methods are perfect for unit testing, please write those
     */
    private StatusEffect convertStatusEffect(MoveDto moveDto) {
        EffectDto effectDto = moveDto.getEffectEntries().get(0);
        int effectChance = moveDto.getEffectChance() != null ? moveDto.getEffectChance() : 100 ;
    
        //DDiemer: although there is no performance win or functional consequence it is slightly better practice to use if/else if/else
        if (effectDto.getEffect().toLowerCase().contains("puts the target to sleep")) {
            return StatusEffect.builder().chance(effectChance).condition(SLEEP).build();
        }
        if (effectDto.getEffect().toLowerCase().contains("poisons the target")) {
            return StatusEffect.builder().chance(effectChance).condition(POISON).build();
        }
        if (effectDto.getEffect().toLowerCase().contains("badly poisons the target")) {
            return StatusEffect.builder().chance(effectChance).condition(BADLY_POISONED).build();
        }
        if (effectDto.getEffect().toLowerCase().contains("paralyzes the target")) {
            return StatusEffect.builder().chance(effectChance).condition(PARALYZED).build();
        }
        if (effectDto.getEffect().toLowerCase().contains("confuses the target.")) {
            return StatusEffect.builder().chance(effectChance).condition(CONFUSED).build();
        }
        if (effectDto.getEffect().toLowerCase().contains("has a $effect_chance% chance to burn the target.")) {
            return StatusEffect.builder().chance(effectChance).condition(BURN).build();
        }
        if (effectDto.getEffect().toLowerCase().contains("has a $effect_chance% chance to poison the target.")) {
            return StatusEffect.builder().chance(effectChance).condition(POISON).build();
        }
        if (effectDto.getEffect().toLowerCase().contains("has a $effect_chance% chance to confuse the target.")) {
            return StatusEffect.builder().chance(effectChance).condition(CONFUSED).build();
        }
        if (effectDto.getEffect().toLowerCase().contains("Has a $effect_chance% chance to freeze the target")) {
            return StatusEffect.builder().chance(effectChance).condition(FROZEN).build();
        }


        return StatusEffect.builder().chance(effectChance).condition(NONE).build();
    }
    // TODO need to do the same for STATS, moves like bubble-beam and psychic affect status with a chance
    // TODO need to also consider that some moves may increase status as a secondary effect

    private HitTimes convertHitTimes(EffectDto effectDto) {
        if (effectDto.getEffect().toLowerCase().contains("hits 2–5 times in one turn")) {
            return HitTimes.TWO_TO_FIVE;
        }
        if (effectDto.getEffect().toLowerCase().contains("hits twice in one turn")) {
            return HitTimes.TWICE;
        }
        if (effectDto.getEffect().toLowerCase().contains("hits twice in the same turn")) {
            return HitTimes.TWICE;
        }
        return HitTimes.ONCE;
    }

}

package nl.rabobank.pirates.service;

import nl.rabobank.pirates.client.PokemonApiRestClient;
import nl.rabobank.pirates.model.common.Type;
import nl.rabobank.pirates.client.move.MoveDto;
import nl.rabobank.pirates.client.move.StatChangeDto;
import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.client.pokemon.ThinMoveDto;
import nl.rabobank.pirates.client.pokemon.ThinMoveWrapperDto;
import nl.rabobank.pirates.client.pokemon.VersionGroupDetailsDto;
import nl.rabobank.pirates.model.common.Stat;
import nl.rabobank.pirates.model.common.StatChange;
import nl.rabobank.pirates.model.move.DamageClass;
import nl.rabobank.pirates.model.move.Move;
import nl.rabobank.pirates.model.move.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Several types of moves are not supported.
 * Basically only damage, status buffs/debuffs and accuracy for now.
 */
@Component
public class MoveService {

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private PokemonApiRestClient pokemonApiRestClient;

    private AtomicInteger counter = new AtomicInteger(0);

    private Map<String, MoveDto> moveStorage = new ConcurrentHashMap<>();

    private static final String RED_BLUE_VERSION_GROUP = "red-blue";


    // Status effects are all missing for now
    // Create a move set builder, find moves that would make sense for a particular pokemon.

    // Round robin from lowest level to highest
    // store locally move names that are known to do damage from highest to lowest and save their names
    public List<Move> getFourRandomMoves(PokemonDto pokemonDto, int level) {
        return convertToMoves(getFourRandomMoves(level, pokemonDto));
    }

    public MoveDto getMoveByName(final String moveName) {
        if (moveStorage.containsKey(moveName)) {
            return moveStorage.get(moveName);
        }
        final MoveDto moveDto = pokemonApiRestClient.getMoveByName(moveName);
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
            int randomNumber = calculationService.getRandomValue(0, allPossibleMoves.size()-1);
            ThinMoveDto randomizedMove = allPossibleMoves.get(randomNumber);
            moves.add(getMoveByName(randomizedMove.getName()));
            allPossibleMoves.remove(randomNumber);
        }
        return moves;
    }

    private boolean isMoveAllowed(int level, ThinMoveWrapperDto thinMoveWrapperDto) {
        boolean isMoveAllowed = false;

        if (MoveServiceConstants.PROHIBITED_MOVES.contains(thinMoveWrapperDto.getMove().getName())) {
            return false;
        }

        for (VersionGroupDetailsDto versionGroupDetailsDto : thinMoveWrapperDto.getVersionGroupDetails()) {
            if (RED_BLUE_VERSION_GROUP.equals(versionGroupDetailsDto.getVersionGroup().getName())) {
                if (versionGroupDetailsDto.getLevelLearnedAt() > 0 && versionGroupDetailsDto.getLevelLearnedAt() <= level) {
                    isMoveAllowed = true;
                } else {
                    return false;
                }

            }
        }

        return isMoveAllowed;
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

        return move.toBuilder().type(Type.valueOfLabel(moveDto.getName())).build();
    }

}

package nl.rabobank.pirates.rest;

import nl.rabobank.pirates.client.common.Type;
import nl.rabobank.pirates.client.move.MoveDto;
import nl.rabobank.pirates.client.move.StatChangeDto;
import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.client.pokemon.StatDtoWrapper;
import nl.rabobank.pirates.core.PokemonService;
import nl.rabobank.pirates.domain.DamageClass;
import nl.rabobank.pirates.domain.Move;
import nl.rabobank.pirates.domain.PokemonResponse;
import nl.rabobank.pirates.domain.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/pokemon/")
@CrossOrigin
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;


    @GetMapping(path="{pokemonName}", produces = "application/json")
    public PokemonResponse getPokemon(@PathVariable final String pokemonName) {
        PokemonDto pokemonDto = pokemonService.getPokemonByName(pokemonName);
        System.out.println(pokemonDto);
        return PokemonResponse.builder()
                .name(pokemonDto.getName())
                .backSpriteUrl(pokemonDto.getSprites().getBackDefault())
                .frontSpriteUrl(pokemonDto.getSprites().getFrontDefault())
                .maxHp(calculateMaxHp(5, pokemonDto))
                .currentHp(calculateMaxHp(5, pokemonDto))
                //.moves(Collections.singletonList(convertToMove(pokemonService.getMoveByName("tackle"))))
                .moves(convertToMoves(pokemonService.getFourRandomMoves(5, pokemonDto)))
            .build();

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
    private int calculateStat(String stat, int level, PokemonDto pokemonDto) {
        for (StatDtoWrapper statDtoWrapper : pokemonDto.getStats()) {
            if (stat.equals(statDtoWrapper.getStat().getName())) {
                return Math.round((float)Math.floor(
                        0.01 * (2 * Integer.valueOf(statDtoWrapper.getBaseStat()) * level) + level + 5
                ));
            }
        }
        throw new RuntimeException(stat + " BASE STAT WASNT FOUND");
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

}

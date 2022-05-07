package nl.rabobank.pirates.core;

import nl.rabobank.pirates.client.move.MoveDto;
import nl.rabobank.pirates.client.pokemon.PokemonDto;
import nl.rabobank.pirates.client.pokemon.ThinMoveDto;
import nl.rabobank.pirates.client.pokemon.ThinMoveWrapperDto;
import nl.rabobank.pirates.client.pokemon.VersionGroupDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PokemonService {

    private HashMap<String, PokemonDto> pokemonStorage = new HashMap<>();
    private HashMap<String, MoveDto> moveStorage = new HashMap<>();

    private static final String VERSION_GROUP = "red-blue";

    private AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    public PokemonDto getPokemonByName(final String pokemonName) {
        if (pokemonStorage.containsKey(pokemonName)) {
            return pokemonStorage.get(pokemonName);
        }

        final String url = "https://pokeapi.co/api/v2/pokemon/" + pokemonName;
        final PokemonDto pokemonDto = restTemplate.getForObject(url, PokemonDto.class);

        pokemonStorage.put(pokemonName, pokemonDto);
        System.out.println("Current cash misses" + counter.incrementAndGet());
        return pokemonDto;
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
        final Random random = new Random();

        while (moves.size() < 4) {
            int randomNumber = random.ints(0, allPossibleMoves.size()-1).findFirst().getAsInt();
            ThinMoveDto randomizedMove = allPossibleMoves.get(randomNumber);
            moves.add(getMoveByName(randomizedMove.getName()));
            allPossibleMoves.remove(randomNumber);
        }

        return moves;
    }



}

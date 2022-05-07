package nl.rabobank.pirates.domain;

import lombok.*;

import java.net.URL;
import java.util.List;


@Getter
@Builder
public class PokemonResponse {

    private String name;

    private URL backSpriteUrl;

    private URL frontSpriteUrl;

    private int maxHp;

    private int currentHp;

    private List<Move> moves;

}

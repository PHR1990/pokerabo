package nl.rabobank.pirates.service;

import java.util.Arrays;
import java.util.List;

public interface MoveServiceConstants {
    public static final List<String> PROHIBITED_MOVES
            = Arrays.asList(
            "double-team",
            "poison-powder",
            "sleep-powder",
            "leech-seed",
            "rage",
            "fury-swipes",
            "double-kick",
            "comet-punch",
            "focus-energy",
            "thunder-wave",
            "metronome",
            "dig",
            "hyper-beam",
            "amnesia",
            "clamp",
            "bind",
            "bide",
            "acird-armor",
            "barrage",
            "bonemerang",
            "confure-ray",
            "disable",
            "double-slap",
            "dream-eater",
            "egg-bomb",
            "explosion",
            "fissure",
            "fury-attack",
            "glare",
            "guilliotine",
            "haze",
            "horn-drill",
            "hypnosis",
            "leech-life",
            "leech-seed",
            "lovely-kiss",
            "light-screen",
            "mimic",
            "minimize",
            "mist",
            "night-shade",
            "pin-missile",
            "rest",
            "roar",
            "recover"

    );

    public List<String> movesToPrioritizeWhenBuilding = Arrays.asList(
            "earthquake",
            "psychic",
            "blizzard",
            "surf",
            "thunder",
            "waterfall",
            "flamethrower",
            "hydro-pump",
            "fire-punch",
            "fire-blast",
            "thunder-bolt",
            "razor-leaf",
            "aurora-beam",
            "body-slam",
            "drill-peck",
            "headbutt",
            "hyper-fang",
            "ice-beam",
            "mega-kick",
            "mega-punch"
    );
}

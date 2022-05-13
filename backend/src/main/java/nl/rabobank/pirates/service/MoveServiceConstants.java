package nl.rabobank.pirates.service;

import java.util.Arrays;
import java.util.List;

public interface MoveServiceConstants {
    List<String> PROHIBITED_MOVES
            = Arrays.asList(
            "double-team",
            "poison-powder",
            "sleep-powder",
            "leech-seed",
            "rage",
            "focus-energy",
            "thunder-wave",
            "metronome",
            "dig",
            "hyper-beam",
            "amnesia",
            "clamp",
            "bind",
            "bide",
            "confure-ray",
            "disable",
            "dream-eater",
            "explosion",
            "fissure",
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
            "rest",
            "roar",
            "recover",
            "sing"
    );

    List<String> MOVES_TO_PRIORITIZE_WHEN_BUILDING = Arrays.asList(
            "double-slap",
            "fury-swipes",
            "pin-missile",
            "fury-attack",
            "double-kick",
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

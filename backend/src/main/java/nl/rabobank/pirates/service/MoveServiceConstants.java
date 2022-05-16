package nl.rabobank.pirates.service;

import java.util.Arrays;
import java.util.List;

public interface MoveServiceConstants {
    List<String> PROHIBITED_MOVES
            = Arrays.asList(
            "double-team",  // Needs accuracy
            "rage",
            "focus-energy",
            "metronome",    // Possible but very niche
            "dig",          // Needs to implement immunity and 2 turn attacks
            "hyper-beam",
            "amnesia",      // needs to be specially tested
            "clamp",
            "bind",
            "bide",         // Very niche, no reason to implement
            "confure-ray",  // Just requires confusion
            "disable",      // Annoying to implement
            "dream-eater",  // Needs sleep
            "explosion",    // A bit useless in a simulation
            "fissure",
            "glare",
            "guilliotine",
            "haze",
            "horn-drill",
            "leech-life",
            "leech-seed", // Seeded condition
            "light-screen",
            "mimic",    // Improbable to implement
            "minimize", // Need to implement evasion
            "mist",
            "night-shade", // Flat amount on HP
            "rest", // Special type of sleep
            "roar", // No reason to implement
            "recover",
            "whirlwind",
            "reflect",
            "counter"
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

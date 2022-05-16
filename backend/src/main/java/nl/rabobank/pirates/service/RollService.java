package nl.rabobank.pirates.service;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RollService {

    final Random random = new Random();

    public boolean isRollSuccessful(int chance) {
        return chance >= getRandomValue(0, 101);
    }

    public int getRandomValue(int rangeStart, int rangeEnd) {

        return random.ints(rangeStart, rangeEnd).findFirst().getAsInt();
    }
}

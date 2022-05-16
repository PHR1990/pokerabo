package nl.rabobank.pirates.move.mock;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.pirates.client.move.MoveDto;
import nl.rabobank.pirates.model.common.Type;
import nl.rabobank.pirates.model.move.DamageClass;
import nl.rabobank.pirates.model.move.Move;

import java.io.File;
import java.io.IOException;

public class MoveFactoryTest {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static MoveDto makeScratchDto() throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(new File("src/test/resources/move/scratch.json"), MoveDto.class);
    }

    public static Move makeScratch() {
        return Move.builder().type(Type.NORMAL).name("Scratch").power(40).damageClass(DamageClass.PHYSICAL).accuracy(100).build();
    }
}

package nl.rabobank.pirates.move.mock;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.pirates.client.move.MoveDto;

import java.io.File;
import java.io.IOException;

public class MoveDtoFactoryTest {

    public static final ObjectMapper objectMapper = new ObjectMapper();


    public static MoveDto getScratch() throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(new File("src/test/resources/scratch.json"), MoveDto.class);
    }
}

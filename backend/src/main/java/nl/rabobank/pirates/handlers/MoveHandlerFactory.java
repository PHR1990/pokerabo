package nl.rabobank.pirates.handlers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.rabobank.pirates.model.move.DamageClass;

@Component
public class MoveHandlerFactory {
	
	private static Map<DamageClass, MoveHandler> handlerMap;

    @Autowired
    public MoveHandlerFactory(List<MoveHandler> handlers) {
    	handlerMap = handlers.stream().collect(Collectors.toUnmodifiableMap(MoveHandler::getClassType, Function.identity()));
    }

    public static <T> MoveHandler getHandler(DamageClass damageClass) {
        return Optional.ofNullable(handlerMap.get(damageClass)).orElseThrow(IllegalArgumentException::new);
    }

}

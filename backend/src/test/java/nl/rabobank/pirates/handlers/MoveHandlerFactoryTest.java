package nl.rabobank.pirates.handlers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.rabobank.pirates.model.move.DamageClass;

public class MoveHandlerFactoryTest {
	
	@SuppressWarnings("static-access")
	@Test
    public void handler_factory_returns_correct_handler_class() {
		
		List<MoveHandler> movehandlers = new ArrayList<>();
		movehandlers.add(new PhysicalMoveHandler());
		movehandlers.add(new StatusMoveHandler());
		movehandlers.add(new SpecialMoveHandler());
		
		MoveHandlerFactory moveHandlerFactory = new MoveHandlerFactory(movehandlers);
		
		MoveHandler physicalMoveHandler = moveHandlerFactory.getHandler(DamageClass.PHYSICAL);
		assertEquals(physicalMoveHandler.getClass(), PhysicalMoveHandler.class);
		
		MoveHandler statusMoveHandler = moveHandlerFactory.getHandler(DamageClass.STATUS);
		assertEquals(statusMoveHandler.getClass(), StatusMoveHandler.class);
		
		MoveHandler specialMoveHandler = moveHandlerFactory.getHandler(DamageClass.SPECIAL);
		assertEquals(specialMoveHandler.getClass(), SpecialMoveHandler.class);
	}
}

package ru.pupov.spring_state_machine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS;
import ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES;

import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.ADD_CHECK_IN_TICKETS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.ADD_CHECK_OUT_TICKETS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.ADD_REPORTING_DOCUMENTS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.ADD_SPECIFICATION;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.CLOSE_WORK_TRIP;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.START_ARRIVAL_ROAD;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.START_DEPARTURE_ROAD;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.START_WORKING;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.*;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class SpringStateMachineApplicationTests {

	@Autowired
	private StateMachine<WORK_TRIP_STATES, WORK_TRIP_EVENTS> stateMachine;

	@Test
	void contextLoads() {
	}

	@Test
	void testInit() {
		Assertions.assertNotNull(stateMachine);
		Assertions.assertEquals(stateMachine.getState().getId(), CREATED);
	}

	@Test
	void greenWay() {
		//sendEvent(Mono.just(MessageBuilder.withPayload(MyEvent.MY_EVENT).build())).subscribe()
		stateMachine.sendEvent(ADD_SPECIFICATION);
		stateMachine.sendEvent(ADD_CHECK_IN_TICKETS);
		stateMachine.sendEvent(START_DEPARTURE_ROAD);
		stateMachine.sendEvent(START_WORKING);
		stateMachine.sendEvent(ADD_CHECK_OUT_TICKETS);
		stateMachine.sendEvent(START_ARRIVAL_ROAD);
		stateMachine.sendEvent(ADD_REPORTING_DOCUMENTS);
		stateMachine.sendEvent(CLOSE_WORK_TRIP);
		Assertions.assertEquals(stateMachine.getState().getId(), CREATED);
	}
}

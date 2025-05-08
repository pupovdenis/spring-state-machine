package ru.pupov.spring_state_machine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import reactor.core.publisher.Mono;
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
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.CLOSED;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.CREATED;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.SPECIFIED;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class SpringStateMachineApplicationTests {


    @Autowired
    public StateMachine<WORK_TRIP_STATES, WORK_TRIP_EVENTS> stateMachine;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void setUp() {
        stateMachine.startReactively().subscribe();
    }

    @Test
    void testInit() {
        Assertions.assertNotNull(stateMachine);
        Assertions.assertEquals(stateMachine.getState().getId(), CREATED);
    }

    @Test
    void testGreenWay() {
        //sendEvent(Mono.just(MessageBuilder.withPayload(MyEvent.MY_EVENT).build())).subscribe()
        sendEvent(stateMachine, ADD_SPECIFICATION);
        sendEvent(stateMachine, ADD_CHECK_IN_TICKETS);
        sendEvent(stateMachine, START_DEPARTURE_ROAD);
        sendEvent(stateMachine, START_WORKING);
        sendEvent(stateMachine, ADD_CHECK_OUT_TICKETS);
        sendEvent(stateMachine, START_ARRIVAL_ROAD);
        sendEvent(stateMachine, ADD_REPORTING_DOCUMENTS);
        sendEvent(stateMachine, CLOSE_WORK_TRIP);
        Assertions.assertEquals(CLOSED, stateMachine.getState().getId());
    }

    @Test
    void testWrongWay() {
        sendEvent(stateMachine, ADD_SPECIFICATION);
        sendEvent(stateMachine, START_WORKING);
        Assertions.assertEquals(SPECIFIED, stateMachine.getState().getId());
    }

    private void sendEvent(StateMachine<WORK_TRIP_STATES, WORK_TRIP_EVENTS> stateMachine, WORK_TRIP_EVENTS event) {
        stateMachine.sendEvent(
                Mono.just(MessageBuilder
                        .withPayload(event)
                        .build())
        ).subscribe();
    }
}

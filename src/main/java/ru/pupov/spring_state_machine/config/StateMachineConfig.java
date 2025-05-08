package ru.pupov.spring_state_machine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
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

@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<WORK_TRIP_STATES, WORK_TRIP_EVENTS> {
    @Override
    public void configure(StateMachineConfigurationConfigurer<WORK_TRIP_STATES, WORK_TRIP_EVENTS> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<WORK_TRIP_STATES, WORK_TRIP_EVENTS> states) throws Exception {
        states.withStates()
                .initial(CREATED)
                .state(SPECIFIED)
                .state(FIXED_CHECK_IN_TICKETS)
                .state(DEPARTURE_ROAD_FACT)
                .state(IN_PROGRESS)
                .state(FIXED_CHECK_OUT_TICKETS)
                .state(ARRIVAL_ROAD_FACT)
                .state(DOCUMENTS_READY)
                .end(CLOSED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<WORK_TRIP_STATES, WORK_TRIP_EVENTS> transitions) throws Exception {
        transitions
                .withExternal().state(CREATED).target(SPECIFIED).event(ADD_SPECIFICATION)
                .and().withExternal().state(SPECIFIED).target(FIXED_CHECK_IN_TICKETS).event(ADD_CHECK_IN_TICKETS)
                .and().withExternal().state(FIXED_CHECK_IN_TICKETS).target(DEPARTURE_ROAD_FACT).event(START_DEPARTURE_ROAD)
                .and().withExternal().state(DEPARTURE_ROAD_FACT).target(IN_PROGRESS).event(START_WORKING)
                .and().withExternal().state(IN_PROGRESS).target(FIXED_CHECK_OUT_TICKETS).event(ADD_CHECK_OUT_TICKETS)
                .and().withExternal().state(FIXED_CHECK_OUT_TICKETS).target(ARRIVAL_ROAD_FACT).event(START_ARRIVAL_ROAD)
                .and().withExternal().state(ARRIVAL_ROAD_FACT).target(DOCUMENTS_READY).event(ADD_REPORTING_DOCUMENTS)
                .and().withExternal().state(DOCUMENTS_READY).target(CLOSED).event(CLOSE_WORK_TRIP);
    }
}

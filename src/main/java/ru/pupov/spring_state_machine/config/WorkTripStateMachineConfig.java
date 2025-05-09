package ru.pupov.spring_state_machine.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS;
import ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES;

import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.ADD_CHECK_IN_TICKETS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.ADD_CHECK_OUT_TICKETS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.ADD_REPORTING_DOCUMENTS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.ADD_SPECIFICATION;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.CHANGE_FIXED_CHECK_OUT_TICKETS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.CLOSE_WORK_TRIP;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.CHANGE_FIXED_CHECK_IN_TICKETS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.START_ARRIVAL_ROAD;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.START_DEPARTURE_ROAD;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_EVENTS.START_WORKING;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.ARRIVAL_ROAD_FACT;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.CLOSED;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.CREATED;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.DEPARTURE_ROAD_FACT;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.DOCUMENTS_READY;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.FIXED_CHECK_IN_TICKETS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.FIXED_CHECK_OUT_TICKETS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.IN_PROGRESS;
import static ru.pupov.spring_state_machine.statemachine.WORK_TRIP_STATES.SPECIFIED;

@Configuration
@EnableStateMachine
@Slf4j
public class WorkTripStateMachineConfig extends EnumStateMachineConfigurerAdapter<WORK_TRIP_STATES, WORK_TRIP_EVENTS> {
    @Override
    public void configure(StateMachineConfigurationConfigurer<WORK_TRIP_STATES, WORK_TRIP_EVENTS> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<WORK_TRIP_STATES, WORK_TRIP_EVENTS> states) throws Exception {
        System.out.println("Registering states...");
        states.withStates()
                .initial(CREATED, notifyOperator())
                //.states(EnumSet.allOf(WORK_TRIP_STATES.class));
                .state(SPECIFIED, context -> log.info("need check-in tickets"))
                .state(FIXED_CHECK_IN_TICKETS)
                .state(DEPARTURE_ROAD_FACT)
                .state(IN_PROGRESS, context -> log.info("need check-out tickets"))
                .state(FIXED_CHECK_OUT_TICKETS)
                .state(ARRIVAL_ROAD_FACT, context -> log.info("need documents"))
                .state(DOCUMENTS_READY)
                .end(CLOSED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<WORK_TRIP_STATES, WORK_TRIP_EVENTS> transitions) throws Exception {
        transitions.withExternal().source(CREATED).target(SPECIFIED).event(ADD_SPECIFICATION).guard(validateSpecification())
                .and().withExternal().source(SPECIFIED).target(FIXED_CHECK_IN_TICKETS).event(ADD_CHECK_IN_TICKETS)
                .and().withExternal().source(FIXED_CHECK_IN_TICKETS).target(DEPARTURE_ROAD_FACT).event(START_DEPARTURE_ROAD)
                .and().withExternal().source(DEPARTURE_ROAD_FACT).target(IN_PROGRESS).event(START_WORKING).action(context -> log.info("working"))
                .and().withExternal().source(IN_PROGRESS).target(FIXED_CHECK_OUT_TICKETS).event(ADD_CHECK_OUT_TICKETS)
                .and().withExternal().source(FIXED_CHECK_OUT_TICKETS).target(ARRIVAL_ROAD_FACT).event(START_ARRIVAL_ROAD)
                .and().withExternal().source(ARRIVAL_ROAD_FACT).target(DOCUMENTS_READY).event(ADD_REPORTING_DOCUMENTS).action(context -> log.info("notify to send documents"))
                .and().withExternal().source(DOCUMENTS_READY).target(CLOSED).event(CLOSE_WORK_TRIP)

                .and().withInternal().source(FIXED_CHECK_IN_TICKETS).event(CHANGE_FIXED_CHECK_IN_TICKETS).action(context -> log.info("notify that check-in tickets was changed"))
                .and().withInternal().source(FIXED_CHECK_OUT_TICKETS).event(CHANGE_FIXED_CHECK_OUT_TICKETS).action(context -> log.info("notify that check-out tickets was changed"));
    }

    private Guard<WORK_TRIP_STATES, WORK_TRIP_EVENTS> validateSpecification() {
        return context -> {
            log.warn("validate specification");
            context.getExtendedState()
                    .getVariables()
                    .put("isValidated", "true");
            return true;
        };
    }

    private Action<WORK_TRIP_STATES, WORK_TRIP_EVENTS> notifyOperator() {
        return context -> log.info("need documents");
    }

    private StateMachineListener<WORK_TRIP_STATES, WORK_TRIP_EVENTS> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void transition(Transition<WORK_TRIP_STATES, WORK_TRIP_EVENTS> transition) {
                log.info("change {} to {}",
                        ofNullableState(transition.getSource()),
                        ofNullableState(transition.getTarget()));
            }

            @Override
            public void eventNotAccepted(Message<WORK_TRIP_EVENTS> event) {
                log.error("not accepted: {}", event.getPayload());
            }

            //duct tape
            private Object ofNullableState(State<WORK_TRIP_STATES, WORK_TRIP_EVENTS> s) {
                return s != null ? s.getId() : null;
            }
        };
    }
}

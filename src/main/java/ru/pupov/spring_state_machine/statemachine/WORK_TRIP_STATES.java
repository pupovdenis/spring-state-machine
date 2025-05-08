package ru.pupov.spring_state_machine.statemachine;

public enum WORK_TRIP_STATES {
    CREATED,
    SPECIFIED,
    FIXED_CHECK_IN_TICKETS,
    DEPARTURE_ROAD_FACT,
    IN_PROGRESS,
    FIXED_CHECK_OUT_TICKETS,
    ARRIVAL_ROAD_FACT,
    DOCUMENTS_READY,
    CLOSED
}

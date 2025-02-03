CREATE TABLE floors
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    capacity INTEGER                                  NOT NULL,
    CONSTRAINT pk_floors PRIMARY KEY (id)
);

CREATE TABLE tickets
(
    id            UUID                        NOT NULL,
    entry_gate_id UUID                        NOT NULL,
    exit_gate_id  UUID,
    time_of_entry TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    time_of_exit  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_tickets PRIMARY KEY (id)
);
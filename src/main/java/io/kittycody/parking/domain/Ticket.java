package io.kittycody.parking.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID entryGateId;

    private UUID exitGateId;

    @Column(nullable = false)
    private LocalDateTime timeOfEntry;

    private LocalDateTime timeOfExit;

    protected Ticket() {}

    public Ticket(UUID entryGateId, LocalDateTime now) {
        this.id = UUID.randomUUID();
        this.entryGateId = entryGateId;
        this.timeOfEntry = now;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEntryGateId() {
        return entryGateId;
    }

    public UUID getExitGateId() {
        return exitGateId;
    }

    public LocalDateTime getTimeOfEntry() {
        return timeOfEntry;
    }

    public LocalDateTime getTimeOfExit() {
        return timeOfExit;
    }
}

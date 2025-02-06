package io.kittycody.parking.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "floors")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int capacity;

    public Floor(int capacity) {
        this.capacity = capacity;
    }

    protected Floor() {}
}

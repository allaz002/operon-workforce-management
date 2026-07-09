package com.operon.workforce.shift;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "shifts")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(nullable = false, length = 100)
    private String role;

    @Column(name = "required_employees", nullable = false)
    private Integer requiredEmployees;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Shift() {
    }

    public Shift(Instant startTime, Instant endTime, String role, Integer requiredEmployees, String location, String note) {
        validateTimeRange(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
        this.role = role;
        this.requiredEmployees = requiredEmployees;
        this.location = location;
        this.note = note;
        this.createdAt = Instant.now();
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public String getRole() {
        return role;
    }

    public Integer getRequiredEmployees() {
        return requiredEmployees;
    }

    public String getLocation() {
        return location;
    }

    public String getNote() {
        return note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public void validateTimeRange(Instant startTime, Instant endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new InvalidShiftTimeRangeException();
        }
    }

    public void update(Instant startTime, Instant endTime, String role, Integer requiredEmployees, String location, String note) {
        validateTimeRange(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
        this.role = role;
        this.requiredEmployees = requiredEmployees;
        this.location = location;
        this.note = note;
    }
}

package com.operon.workforce.shiftassignment;

import com.operon.workforce.shift.Shift;
import com.operon.workforce.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "shift_assignments")
public class ShiftAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ShiftAssignment() {
    }

    public ShiftAssignment(Shift shift, User user) {
        this.shift = shift;
        this.user = user;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getShiftId() {
        return shift.getId();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

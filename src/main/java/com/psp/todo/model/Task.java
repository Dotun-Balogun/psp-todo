package com.psp.todo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private User user;
    @ManyToOne
    private Category category;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(length = 2000)
    private String description;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;
    private boolean done;
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Overdue is calculated, never stored, so it is always correct. */
    public boolean isOverdue() {
        return !done && dueDate != null && dueDate.isBefore(LocalDate.now());
    }
}

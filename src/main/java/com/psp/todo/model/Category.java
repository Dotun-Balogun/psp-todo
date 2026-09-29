package com.psp.todo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    @Column(length = 10)
    private String colorCode;

    public Category(String name, String colorCode) {
        this.name = name;
        this.colorCode = colorCode;
    }
}
